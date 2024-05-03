package com.github.jiizuz.booking.data;

import com.github.jiizuz.booking.booking.Booking;
import com.github.jiizuz.booking.booking.ImmutableBooking;
import lombok.NonNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * {@link BookingRepository} that stores the bookings in memory.
 *
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @see com.github.jiizuz.booking.data.BookingRepository
 * @since 1.0
 */
public class MemoryBookingRepository implements BookingRepository {

    // Use HashMap and ReadWriteLock to keep fair access to the bookings

    /**
     * Map of bookings stored in memory by their UniqueId.
     */
    private final Map<String, Booking> bookingsById = new HashMap<>(16);

    /**
     * Lock to handle the concurrent access to the bookings.
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    /**
     * {@inheritDoc}
     */
    @Override
    public String save(final @NonNull Booking data) {
        lock.writeLock().lock();
        try {
            if (data.getId() == null) { // New Booking
                final String uniqueId = generateUniqueId(data);
                final Booking booking = ImmutableBooking.copyOf(data).withId(uniqueId);

                bookingsById.put(uniqueId, booking);
                return uniqueId;
            }

            // Update Booking (overwrite)
            final String uniqueId = data.getId();
            bookingsById.put(uniqueId, data);

            return uniqueId;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Booking get(final @NonNull String key) {
        lock.readLock().lock();
        try {
            return bookingsById.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Booking delete(final @NonNull String key) {
        lock.writeLock().lock();
        try {
            return bookingsById.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Set<Booking> getBookingsOfDay(final @NonNull Instant date) {
        // Convert the start and end instants into a date-instant
        final Instant start = date.truncatedTo(ChronoUnit.DAYS);
        final Instant end = date.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS);

        return getBookingsBetween(start, end); // Get bookings between the same day
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Set<Booking> getBookingsBetween(final @NonNull Instant start, final @NonNull Instant end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("The start instant must be before the end instant");
        }

        lock.readLock().lock();
        try {
            final Set<Booking> bookings = new HashSet<>(32);

            for (final Booking booking : bookingsById.values()) {
                final Instant bookingDate = booking.getDate();

                if (bookingDate.isAfter(start) && bookingDate.isBefore(end)) {
                    bookings.add(booking);
                }
            }

            return bookings.stream()
                    .sorted(Comparator.comparing(Booking::getDate))
                    .collect(LinkedHashSet::new, Set::add, Set::addAll);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Generates a UniqueId for the given {@link Booking}.
     *
     * @param booking the booking to generate the UniqueId
     * @return the UniqueId generated
     */
    @NonNull
    public String generateUniqueId(final @NonNull Booking booking) {
        return UUID.randomUUID().toString();
    }
}
