package com.github.jiizuz.booking.booking;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

/**
 * Represents a booking made by a customer. This class is immutable.
 *
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @see com.github.jiizuz.booking.booking.Booking
 * @since 1.0
 */
@Data
@RequiredArgsConstructor(staticName = "of")
public final class ImmutableBooking implements Booking {

    /**
     * The name of the customer that made the booking.
     */
    @NonNull
    private final String customerName;

    /**
     * The size of the table that the customer wants to book.
     */
    private final int tableSize;

    /**
     * The date and time of the booking.
     */
    @NonNull
    private final Instant date;
}
