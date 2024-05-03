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
    // - The application can accept bookings for any day, even if the day has passed.
    // (To allow the restaurant to store bookings that happened previously)
    // - The restaurant has no problem with bookings happening even 1 second before the reservation time.
    // (While the restaurant is open and the table is available)
    // - The restaurant operates in a single timezone (UTC).

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

        // Set up the server configuration
        serverBuilder.withInterface("localhost").withHttpPort(8080);

        // Set up the routes
        final Router[] routers = {
                new BookingRouter(),
        };

        Stream.of(routers).forEach(router -> router.setupRoutes(serverBuilder));

        // Retrieve the server information before starting it
        final String host = serverBuilder.interfaceHost();
        final int port = serverBuilder.httpPort();

        // Start the server
        serverBuilder.start();

        System.out.println("=========================================");
        System.out.println("Server started at http://" + host + ":" + port + "/");
        System.out.println("=========================================");
    }
}
