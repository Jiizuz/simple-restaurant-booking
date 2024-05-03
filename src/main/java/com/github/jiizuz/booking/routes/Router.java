package com.github.jiizuz.booking.routes;

import io.muserver.MuServerBuilder;
import lombok.NonNull;

/**
 * Simple router that defines the routes of the application.
 *
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @since 1.0
 */
public interface Router {

    /**
     * Sets up the routes of this router.
     *
     * @param serverBuilder the server builder to set up the routes
     */
    void setupRoutes(@NonNull MuServerBuilder serverBuilder);
}
