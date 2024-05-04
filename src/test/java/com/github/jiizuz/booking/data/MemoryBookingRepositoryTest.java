package com.github.jiizuz.booking.data;

import com.github.jiizuz.booking.booking.Booking;
import com.github.jiizuz.booking.booking.ImmutableBooking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for the {@link MemoryBookingRepository}.
 *
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @since 1.0
 */
public class MemoryBookingRepositoryTest {

    /**
     * {@link MemoryBookingRepository} to test.
     */
    private MemoryBookingRepository repository;

    /**
     * Sample {@link Booking} to use in the tests.
     */
    private final ImmutableBooking sampleBooking = ImmutableBooking.builder().customerName("Uscanga").tableSize(4).date(Instant.parse("2023-05-01T14:00:00Z")).build()
            .withId("1");

    @BeforeEach
    public void setUp() {
        repository = new MemoryBookingRepository();
    }

    @Test
    public void whenSave_thenStoreBooking() {
        Booking booking = sampleBooking
                .withId("1");

        String id = repository.save(booking);

        assertEquals("1", id, "The id returned was not the expected id");
    }

    @Test
    public void whenSave_thenUpdateBooking() {
        Booking booking = sampleBooking
                .withId("1");

        repository.save(booking);

        Booking updatedBooking = ImmutableBooking.copyOf(booking).withTableSize(6);

        String id = repository.save(updatedBooking); // Update the table size

        assertEquals("1", id, "The id returned was not the expected id");
        assertEquals(6, repository.get("1").getTableSize(), "The table size was not the expected table size");
    }

    @Test
    public void whenSave_thenGenerateUniqueId() {
        Booking booking = sampleBooking;

        String id = repository.save(booking);

        assertEquals(id, repository.get(id).getId(), "The id returned was not the expected id");
    }

    @Test
    public void whenGet_thenRetrieveBooking() {
        Booking booking = sampleBooking
                .withId("1");

        repository.save(booking);

        Booking found = repository.get("1");

        assertEquals(booking, found, "The booking returned was not the expected booking");
    }

    @Test
    public void whenDelete_thenRemoveBooking() {
        Booking booking = sampleBooking
                .withId("1");

        repository.save(booking);

        Booking deleted = repository.delete("1");

        assertEquals(booking, deleted, "The booking returned was not the expected booking");
        assertEquals(0, repository.getAll().size(), "The amount of bookings was not the expected amount");
    }

    @Test
    public void whenSave_thenRetrieveAllBookings() {
        Booking booking1 = sampleBooking
                .withId("1");
        Booking booking2 = sampleBooking
                .withId("2");

        repository.save(booking1);
        repository.save(booking2);

        assertEquals(2, repository.getAll().size(), "The amount of bookings was not the expected amount");
    }

    @Test
    public void whenSave_thenRetrieveAllBookingsWithoutDuplicates() {
        Booking booking1 = sampleBooking
                .withId("1");
        Booking booking2 = sampleBooking
                .withId("2");

        repository.save(booking1);
        repository.save(booking2);
        repository.save(booking1);

        assertEquals(2, repository.getAll().size(), "The amount of bookings was not the expected amount");
    }

    @Test
    public void whenGetBookingsOfTheDay_thenRetrieveAllBookingsOfTheDay() {
        Booking booking1 = sampleBooking
                .withId("1");
        Booking booking2 = sampleBooking
                .withId("2");
        final Instant date = Instant.parse("2023-05-01T00:00:00Z");

        repository.save(booking1);
        repository.save(booking2);

        assertEquals(2, repository.getBookingsOfDay(date).size(), "The amount of bookings was not the expected amount");
    }

    @Test
    public void whenGetBookingsOfTheDay_thenRetrieveAllBookingsOfTheDayWithoutDuplicates() {
        Booking booking1 = sampleBooking
                .withId("1");
        Booking booking2 = sampleBooking
                .withId("2");
        final Instant date = Instant.parse("2023-05-01T00:00:00Z");

        repository.save(booking1);
        repository.save(booking2);
        repository.save(booking1);

        assertEquals(2, repository.getBookingsOfDay(date).size(), "The amount of bookings was not the expected amount");
    }

    @Test
    public void whenGetBookingsOfTheDay_thenRetrieveAllBookingsOfTheDayWithDifferentDates() {
        Booking booking1 = sampleBooking
                .withId("1");
        Booking booking2 = sampleBooking
                .withId("2");
        final Instant date = Instant.parse("2023-05-02T00:00:00Z");

        repository.save(booking1);
        repository.save(booking2);

        assertEquals(0, repository.getBookingsOfDay(date).size(), "The amount of bookings was not the expected amount");
    }

    @Test
    public void whenGetBookingsOfTheDay_thenRetrieveAllBookingsOfTheDayWithDifferentDatesAndHours() {
        Booking booking1 = sampleBooking
                .withId("1");
        Booking booking2 = sampleBooking.withDate(Instant.parse("2023-05-02T14:00:00Z"))
                .withId("2");
        final Instant date = Instant.parse("2023-05-01T00:00:00Z");

        repository.save(booking1);
        repository.save(booking2);

        assertEquals(1, repository.getBookingsOfDay(date).size(), "The amount of bookings was not the expected amount");
    }

    @Test
    public void whenGetBookingsOfTheDay_thenRetrieveAllBookingsOfTheDayInOrder() {
        Booking booking1 = sampleBooking
                .withId("1");
        Booking booking2 = sampleBooking.withDate(Instant.parse("2023-05-01T14:30:00Z"))
                .withId("2");
        final Instant date = Instant.parse("2023-05-01T00:00:00Z");

        repository.save(booking2);
        repository.save(booking1);

        final Iterator<Booking> iterator = repository.getBookingsOfDay(date).iterator();
        assertEquals(booking1, iterator.next(), "The booking returned was not the expected booking");
        assertEquals(booking2, iterator.next(), "The booking returned was not the expected booking");
        assertEquals(2, repository.getBookingsOfDay(date).size(), "The amount of bookings was not the expected amount");
    }

    @Test
    public void whenGetBookingsBetween_thenRetrieveAllBookingsBetweenDates() {
        Booking booking1 = sampleBooking
                .withId("1");
        Booking booking2 = sampleBooking.withDate(Instant.parse("2023-05-02T14:00:00Z"))
                .withId("2");
        final Instant start = Instant.parse("2023-05-01T00:00:00Z");
        final Instant end = Instant.parse("2023-05-02T00:00:00Z");

        repository.save(booking1);
        repository.save(booking2);

        assertEquals(1, repository.getBookingsBetween(start, end).size(), "The amount of bookings was not the expected amount");
    }

    @Test
    public void whenGetBookingsBetween_thenRetrieveAllBookingsBetweenDatesWithoutDuplicates() {
        Booking booking1 = sampleBooking
                .withId("1");
        Booking booking2 = sampleBooking.withDate(Instant.parse("2023-05-02T14:00:00Z"))
                .withId("2");
        final Instant start = Instant.parse("2023-05-01T00:00:00Z");
        final Instant end = Instant.parse("2023-05-02T00:00:00Z");

        repository.save(booking1);
        repository.save(booking2);
        repository.save(booking1);

        assertEquals(1, repository.getBookingsBetween(start, end).size(), "The amount of bookings was not the expected amount");
    }

    @Test
    public void whenGetBookingsBetween_thenThrowExceptionIfStartIsAfterEnd() {
        final Instant start = Instant.parse("2023-05-02T00:00:00Z");
        final Instant end = Instant.parse("2023-05-01T00:00:00Z");

        assertThrows(IllegalArgumentException.class, () -> repository.getBookingsBetween(start, end), "The exception was not thrown");
    }
}
