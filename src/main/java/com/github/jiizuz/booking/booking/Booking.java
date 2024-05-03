package com.github.jiizuz.booking.booking;

import lombok.NonNull;

import java.time.Instant;

/**
 * Represents a booking made by a customer.
 *
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @since 1.0
 */
public interface Booking {

    /**
     * Returns the name of the customer that made the booking.
     *
     * @return the name of the customer
     */
    @NonNull
    String getCustomerName();

    /**
     * Returns the size of the table that the customer wants to book.
     *
     * @return the size of the table
     * @apiNote The size of the table is the number of people that will be in the booking.
     */
    int getTableSize();

    /**
     * Returns the date and time of the booking.
     *
     * @return the date and time of the booking
     */
    @NonNull
    Instant getDate();
}
