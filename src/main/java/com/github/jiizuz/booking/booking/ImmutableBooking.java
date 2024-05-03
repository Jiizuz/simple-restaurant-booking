package com.github.jiizuz.booking.booking;

import lombok.*;

import java.time.Instant;

/**
 * Represents a booking made by a customer. This class is immutable.
 *
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @see com.github.jiizuz.booking.booking.Booking
 * @since 1.0
 */
@Data
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImmutableBooking implements Booking {

    /**
     * The UniqueId of the booking. Can be {@code null} if the booking is not persisted.
     */
    @With
    @Builder.Default
    private final String id = null;

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

    /**
     * Creates a {@link ImmutableBooking} that copies the data of the given {@link Booking}.
     *
     * @param booking the booking to copy
     * @return the immutable booking with the data of the given booking
     * @throws NullPointerException if the booking is {@code null}
     */
    public static ImmutableBooking copyOf(final @NonNull Booking booking) {
        return ImmutableBooking.builder()
                .id(booking.getId())
                .customerName(booking.getCustomerName())
                .tableSize(booking.getTableSize())
                .date(booking.getDate())
                .build();
    }
}
