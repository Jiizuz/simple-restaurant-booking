package com.github.jiizuz.booking;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Utility class with constants of the application.
 *
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @since 1.0
 */
@UtilityClass
public class Constants {

    /**
     * The {@link LocalTime} of the opening time.
     */
    public LocalTime OPENING_TIME = LocalTime.of(9, 0);

    /**
     * The {@link LocalTime} of the closing time.
     */
    public LocalTime CLOSING_TIME = LocalTime.of(21, 0);

    /**
     * The number of tables the restaurant has.
     */
    public int TABLES = 10;

    /**
     * The capacity of each table.
     */
    public int TABLE_CAPACITY = 10;

    /**
     * The {@link Duration} of a time slot for a booking.
     */
    public Duration TIME_SLOT = Duration.ofHours(2);
}
