package com.github.jiizuz.booking.controller;

import com.github.jiizuz.booking.Constants;
import com.github.jiizuz.booking.booking.Booking;
import com.github.jiizuz.booking.booking.ImmutableBooking;
import com.github.jiizuz.booking.data.BookingRepository;
import com.github.jiizuz.booking.data.BookingRepositoryFactory;
import com.github.jiizuz.booking.gson.InstantTypeAdapter;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import io.muserver.MuRequest;
import io.muserver.MuResponse;
import lombok.*;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;

/**
 * Controller that handles the bookings of the restaurant.
 *
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @since 1.0
 */
@RequiredArgsConstructor
public class BookingController {

    // TODO Jiizuz: 5/3/2024 Split this controller into two controllers, one for each handler

    /**
     * Repository to store/retrieve the bookings.
     */
    @NonNull
    private final BookingRepository bookingRepository = BookingRepositoryFactory.getInstance();

    /**
     * {@link GsonBuilder} to serialize and deserialize the requests and responses.
     */
    private final GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

    /**
     * {@link Gson} to serialize and deserialize the requests and responses.
     */
    private final Gson gson = gsonBuilder
            .create();

    /**
     * {@link Gson} to serialize and deserialize the requests and responses in a pretty format.
     */
    private final Gson gsonPretty = gsonBuilder
            .setPrettyPrinting()
            .create();

    /**
     * Creates a booking (reserves a table) for the customer.
     * The request must contain the customer name, the table size and the date and time of the booking.
     *
     * @param request    the request of the client
     * @param response   the response to send to the client
     * @param pathParams the path parameters of the request
     */
    public void createBooking(final @NonNull MuRequest request, final @NonNull MuResponse response, final @NonNull Map<String, String> pathParams) throws IOException {
        // The expected body should not be very large
        final CreateBookingRequest bookingRequest = parseBookingRequest(request, response);
        if (bookingRequest == null || !validBooking(bookingRequest, response)) { // the request is invalid
            return;
        }

        final Booking booking = parseBooking(bookingRequest);
        if (!validBookingStatus(booking, response)) { // the booking status is invalid
            return;
        }

        // store the booking
        final String bookingId = bookingRepository.save(booking);

        response.status(201);
        response.write(Response.of("The booking was created successfully. See you there! :)", true, bookingId).toJson(gson));
    }

    /**
     * Parses the booking request into a booking object.
     *
     * @param bookingRequest the request to parse
     * @return the booking object
     */
    @NonNull
    public Booking parseBooking(final @NonNull CreateBookingRequest bookingRequest) {
        return ImmutableBooking.builder()
                .customerName(bookingRequest.getCustomerName())
                .tableSize(bookingRequest.getTableSize())
                .date(Instant.parse(bookingRequest.getDate() + "T" + bookingRequest.getTime() + ":00Z"))
                .build();
    }

    /**
     * Parses the booking request from the client.
     *
     * @param request  the request of the client
     * @param response the response to send to the client
     * @return the booking request, or {@code null} if the request is invalid
     * @throws IOException if an I/O error occurs while reading the request body
     */
    public CreateBookingRequest parseBookingRequest(final @NonNull MuRequest request, final @NonNull MuResponse response) throws IOException {
        try {
            final CreateBookingRequest bookingRequest = gson.fromJson(request.readBodyAsString(), CreateBookingRequest.class);
            if (bookingRequest == null) {
                response.status(400);
                response.write(Response.of("The request body is invalid.", false).toJson(gson));
                return null;
            }
            return bookingRequest;
        } catch (JsonSyntaxException exception) {
            response.status(400);
            response.write(Response.of("The request body is invalid.", false).toJson(gson));
            return null;
        }
    }

    /**
     * Validates the status of the booking.
     *
     * @param booking  the booking to validate
     * @param response the response to send to the client
     * @return whether the booking status is valid
     * @see Constants
     */
    public boolean validBookingStatus(final @NonNull Booking booking, final @NonNull MuResponse response) {
        // Check if the table size is less than the table capacity
        if (booking.getTableSize() > Constants.TABLE_CAPACITY) {
            response.status(400);
            response.write(Response.of("The table size must be less than " + Constants.TABLE_CAPACITY + ".", false).toJson(gson));
            return false;
        }
        // Check if the booking is outside the opening hours
        final LocalDateTime bookingDate = LocalDateTime.ofInstant(booking.getDate(), ZoneOffset.UTC);
        final LocalDateTime openingTime = bookingDate.withHour(Constants.OPENING_TIME.getHour()).withMinute(Constants.OPENING_TIME.getMinute());
        final LocalDateTime closingTime = bookingDate.withHour(Constants.CLOSING_TIME.getHour()).withMinute(Constants.CLOSING_TIME.getMinute());
        if (bookingDate.isBefore(openingTime) || bookingDate.isAfter(closingTime)) {
            response.status(400);
            response.write(Response.of("The reservation time is outside the opening hours.", false).toJson(gson));
            return false;
        }
        // Check if the restaurant is full at this time
        final Set<Booking> currentBookings = bookingRepository.getBookingsBetween(booking.getDate(), booking.getDate().plus(Constants.TIME_SLOT));
        if (currentBookings.size() >= Constants.TABLES) {
            response.status(400);
            response.write(Response.of("The restaurant is full at this time.", false).toJson(gson));
            return false;
        }
        return true;
    }

    /**
     * Validates the booking request.
     *
     * <p>If the request is not valid, the response will be sent to the client.
     *
     * @param bookingRequest the request to validate
     * @param response       the response to send to the client
     * @return whether the booking request is valid
     */
    public boolean validBooking(final @NonNull CreateBookingRequest bookingRequest, final @NonNull MuResponse response) {
        final String customerName = bookingRequest.getCustomerName();
        final int tableSize = bookingRequest.getTableSize();
        final String date = bookingRequest.getDate();
        final String time = bookingRequest.getTime();

        // validate the customer name

        if (customerName == null || customerName.isEmpty()) {
            response.status(400);
            response.write(Response.of("The customer name is required.", false).toJson(gson));
            return false;
        }
        if (customerName.length() > 64) {
            response.status(400);
            response.write(Response.of("The customer name is too long.", false).toJson(gson));
            return false;
        }

        // validate the table size
        if (tableSize <= 0) {
            response.status(400);
            response.write(Response.of("The table size must be greater than 0.", false).toJson(gson));
            return false;
        }

        // validate the date
        if (date == null || date.isEmpty()) {
            response.status(400);
            response.write(Response.of("The date is required.", false).toJson(gson));
            return false;
        }
        if (date.length() != 10) {
            response.status(400);
            response.write(Response.of("The date must be in the format yyyy-MM-dd.", false).toJson(gson));
            return false;
        }
        try {
            Instant.parse(date + "T00:00:00Z");
        } catch (DateTimeParseException exception) {
            response.status(400);
            response.write(Response.of("The date is invalid.", false).toJson(gson));
            return false;
        }

        // validate the time
        if (time == null || time.isEmpty()) {
            response.status(400);
            response.write(Response.of("The time is required.", false).toJson(gson));
            return false;
        }
        if (time.length() != 5) {
            response.status(400);
            response.write(Response.of("The time must be in the format HH:mm.", false).toJson(gson));
            return false;
        }
        try {
            Instant.parse("1970-01-01T" + time + ":00Z");
        } catch (DateTimeParseException exception) {
            response.status(400);
            response.write(Response.of("The time is invalid.", false).toJson(gson));
            return false;
        }

        return true;
    }

    /**
     * Gets all the bookings for a particular day.
     * The request must contain the date of the bookings.
     *
     * @param request    the request of the client
     * @param response   the response to send to the client
     * @param pathParams the path parameters of the request
     */
    public void getBookingsOfDay(final @NonNull MuRequest request, final @NonNull MuResponse response, final @NonNull Map<String, String> pathParams) {
        if (!validQuery(request, response)) {
            return;
        }
        final Instant date = Instant.parse(request.query().get("date") + "T00:00:00Z");
        final boolean pretty = request.query().contains("pretty");
        final Gson gsonToFormat = pretty ? gsonPretty : gson;

        final Set<Booking> bookings = bookingRepository.getBookingsOfDay(date);

        response.write(gsonToFormat.toJson(bookings));
    }

    /**
     * Validates the query to get the bookings.
     *
     * <p>If the request is not valid, the response will be sent to the client.
     *
     * @param request  the request to validate
     * @param response the response to send to the client
     * @return whether the query is valid
     */
    public boolean validQuery(final @NonNull MuRequest request, final @NonNull MuResponse response) {
        final String date = request.query().get("date");

        // validate the date
        if (date == null || date.isEmpty()) {
            response.status(400);
            response.write(Response.of("The date is required.", false).toJson(gson));
            return false;
        }
        if (date.length() != 10) {
            response.status(400);
            response.write(Response.of("The date must be in the format yyyy-MM-dd.", false).toJson(gson));
            return false;
        }
        try {
            Instant.parse(date + "T00:00:00Z");
        } catch (DateTimeParseException exception) {
            response.status(400);
            response.write(Response.of("The date is invalid.", false).toJson(gson));
            return false;
        }

        return true;
    }

    /**
     * Request to create a booking.
     *
     * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
     * @since 1.0
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class CreateBookingRequest {

        /**
         * The name of the customer.
         */
        @SerializedName("customer_name")
        private String customerName;

        /**
         * The size of the table.
         */
        @Builder.Default
        @SerializedName("table_size")
        private int tableSize = 0;

        /**
         * The date of the booking.
         */
        @SerializedName("date")
        private String date;

        /**
         * The time of the booking.
         */
        @SerializedName("time")
        private String time;
    }

    /**
     * Response to create a booking.
     *
     * <p>This response will contain the message of the response and whether the request was successful.
     *
     * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
     * @since 1.0
     */
    @RequiredArgsConstructor(staticName = "of")
    @AllArgsConstructor(staticName = "of")
    public static final class Response {

        /**
         * The message of the response.
         */
        private final String message;

        /**
         * Whether the request was successful.
         */
        private final boolean success;

        /**
         * The UniqueId of the booking.
         */
        private String bookingId = null;

        /**
         * Converts this object to a JSON string.
         *
         * @return the JSON string of this object
         */
        @NonNull
        public String toJson(final @NonNull Gson gson) {
            return gson.toJson(this);
        }
    }
}
