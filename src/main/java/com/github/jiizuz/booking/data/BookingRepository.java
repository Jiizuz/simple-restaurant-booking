package com.github.jiizuz.booking.data;

import com.github.jiizuz.booking.booking.Booking;
import lombok.NonNull;

import java.time.Instant;
import java.util.Set;

/**
 * Represents a repository that handles the bookings.
 *
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @see com.github.jiizuz.booking.data.Repository
 * @since 1.0
 */
public interface BookingRepository extends Repository<Booking, String> {

    /**
     * Returns all the bookings of a particular day.
     *
     * <p>The bookings are sorted by the time of the booking.
     *
     * <p>The time of the given {@link Instant} is ignored.
     *
     * @param date the date of the bookings
     * @return the bookings of the day in the given date
     * @throws NullPointerException if the date is {@code null}
     */
    @NonNull
    Set<Booking> getBookingsOfDay(@NonNull Instant date);

    /**
     * Returns all the bookings between two dates.
     *
     * <p>The bookings are sorted by the time of the booking.
     *
     * <p>The time of the given {@link Instant Instants} are ignored.
     *
     * @param start the start instant
     * @param end   the end instant
     * @return the bookings between the two instants
     * @throws NullPointerException     if the start or end instants are {@code null}
     * @throws IllegalArgumentException if the start instant is after the end instant
     */
    @NonNull
    Set<Booking> getBookingsBetween(@NonNull Instant start, @NonNull Instant end);
}
