package com.github.jiizuz.booking.controller;

import io.muserver.MuRequest;
import io.muserver.MuResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Controller that handles the bookings of the restaurant.
 *
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @since 1.0
 */
@RequiredArgsConstructor
public class BookingController {

    /**
     * Creates a booking (reserves a table) for the customer.
     * The request must contain the customer name, the table size and the date and time of the booking.
     *
     * @param request    the request of the client
     * @param response   the response to send to the client
     * @param pathParams the path parameters of the request
     */
    public void createBooking(final @NonNull MuRequest request, final @NonNull MuResponse response, final @NonNull Map<String, String> pathParams) {

    }

    /**
     * Gets all the bookings for a particular day.
     * The request must contain the date of the bookings.
     *
     * @param request    the request of the client
     * @param response   the response to send to the client
     * @param pathParams the path parameters of the request
     */
    public void getBookings(final @NonNull MuRequest request, final @NonNull MuResponse response, final @NonNull Map<String, String> pathParams) {

    }
}
