package com.github.jiizuz.booking.routes;

import com.github.jiizuz.booking.controller.BookingController;
import io.muserver.Method;
import io.muserver.MuServerBuilder;
import io.muserver.Routes;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Router that sets up the routes of the bookings.
 *
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @since 1.0
 */
@NoArgsConstructor
public final class BookingRouter implements Router {

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupRoutes(final @NonNull MuServerBuilder builder) {
        final BookingController controller = new BookingController();

        builder.addHandler(Routes.route(Method.POST, "/booking", controller::createBooking));
        builder.addHandler(Routes.route(Method.GET, "/booking", controller::getBookings));
    }
}
