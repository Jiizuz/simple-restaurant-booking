package com.github.jiizuz.booking;

import com.github.jiizuz.booking.routes.BookingRouter;
import com.github.jiizuz.booking.routes.Router;
import io.muserver.MuServerBuilder;
import lombok.experimental.UtilityClass;

import java.util.stream.Stream;

/**
 * Entry point of the application.
 *
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @since 1.0
 */
@UtilityClass
public class Main {

    // Simple Restaurant Booking application
    //
    // - As a customer I want to be able to request a booking at this restaurant. (Create a booking)
    // - As the restaurant owner I want to be able to see all bookings for a particular day. (Read bookings)
    //
    // Each booking has a [customer name], [table size], [date] and [time]. Assume [time slots are for 2 hours].
    //
    // Assumptions:
    // - The restaurant is open from 9am to 9pm.
    // - The restaurant has 10 tables.
    // - The restaurant can only take one booking at a time per table.
    // - The tables have a maximum capacity of 10 people.

    /**
     * Main method of the application.
     *
     * @param args arguments passed to the application
     */
    public void main(final String[] args) {
        setupHttpServer();
    }

    /**
     * Sets up the HTTP server of the application.
     */
    public void setupHttpServer() {
        // Create the server builder
        final MuServerBuilder serverBuilder = MuServerBuilder.httpServer();

        // Set up the routes
        final Router[] routers = {
                new BookingRouter(),
        };

        Stream.of(routers).forEach(router -> router.setupRoutes(serverBuilder));

        // Start the server
        serverBuilder.start();
    }
}
