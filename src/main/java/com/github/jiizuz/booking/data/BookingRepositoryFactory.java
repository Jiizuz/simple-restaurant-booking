package com.github.jiizuz.booking.data;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Factory to retrieve the singleton {@link BookingRepository} instance.
 *
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @since 1.0
 */
@UtilityClass
public class BookingRepositoryFactory {

    /**
     * Returns the singleton {@link BookingRepository} instance.
     *
     * @return the singleton {@link BookingRepository} instance
     */
    @NonNull
    public BookingRepository getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * SingletonHolder is loaded on the first execution of {@link #getInstance()}
     * or the first access to SingletonHolder#INSTANCE, not before.
     *
     * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
     * @since 1.0
     */
    private class SingletonHolder {

        /**
         * {@link BookingRepository} instance of the factory.
         */
        private static final BookingRepository INSTANCE = new MemoryBookingRepository();
    }
}
