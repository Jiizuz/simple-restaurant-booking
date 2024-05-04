package com.github.jiizuz.booking.controller;

import com.github.jiizuz.booking.Constants;
import com.github.jiizuz.booking.booking.Booking;
import com.github.jiizuz.booking.data.BookingRepository;
import com.github.jiizuz.booking.data.BookingRepositoryFactory;
import io.muserver.MuRequest;
import io.muserver.MuResponse;
import io.muserver.RequestParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    /**
     * {@link BookingRepository} instance to test.
     */
    @Mock
    private BookingRepository repository;

    /**
     * {@link BookingController} instance to test.
     */
    private BookingController controller;

    @BeforeEach
    public void setUp() {
        repository = mock(BookingRepository.class);
        // Mock the static method call within a try-with-resources block
        try (MockedStatic<BookingRepositoryFactory> mockedFactory = Mockito.mockStatic(BookingRepositoryFactory.class)) {
            mockedFactory.when(BookingRepositoryFactory::getInstance).thenReturn(repository);
            controller = new BookingController();
        }
    }

    @Test
    public void whenCreateBooking_thenBookingIsCreated() throws IOException {
        MuRequest request = mock(MuRequest.class);
        MuResponse response = mock(MuResponse.class);

        when(request.readBodyAsString()).thenReturn("{\"customer_name\":\"Uscanga\",\"table_size\":4,\"date\":\"2024-05-01\",\"time\":\"14:00\"}");
        when(repository.save(any(Booking.class))).thenReturn("1");
        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);

        controller.createBooking(request, response, new HashMap<>(0));

        verify(repository).save(any(Booking.class));
        verify(response).status(201);
        verify(response).write(responseCaptor.capture());

        String responseContent = responseCaptor.getValue();
        assertTrue(responseContent.contains("\"success\":true"), "Response should contain 'success':true");
        assertTrue(responseContent.contains("\"booking_id\":\"1\""), "Response should contain 'id':'1'");
    }

    @Test
    public void whenCreateBooking_thenBookingIsNotCreatedRestaurantIsFull() throws IOException {
        MuRequest request = mock(MuRequest.class);
        MuResponse response = mock(MuResponse.class);
        //noinspection unchecked
        Set<Booking> bookings = mock(Set.class);

        when(request.readBodyAsString()).thenReturn("{\"customer_name\":\"Uscanga\",\"table_size\":4,\"date\":\"2024-05-01\",\"time\":\"14:00\"}");
        when(bookings.size()).thenReturn(Constants.TABLES);
        when(repository.getBookingsBetween(any(Instant.class), any(Instant.class))).thenReturn(bookings);
        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);

        controller.createBooking(request, response, new HashMap<>(0));

        verify(response).status(400);
        verify(response).write(responseCaptor.capture());

        String responseContent = responseCaptor.getValue();
        assertTrue(responseContent.contains("\"success\":false"), "Response should contain 'success':false");
    }

    @ParameterizedTest
    @CsvSource({
            "{/* empty */}", // Incorrect JSON
            "\"test\"", // Incorrect JSON
            "test", // Incorrect JSON
            "''", // Empty body
            "{\"table_size\":4,\"date\":\"2024-05-01\",\"time\":\"14:00\"}", // Missing customer name
            "{\"customer_name\":\"\",\"table_size\":4,\"date\":\"2024-05-01\",\"time\":\"14:00\"}", // Empty customer name
            "{\"customer_name\":\"UscangaUscangaUscangaUscangaUscangaUscangaUscangaUscanga\",\"table_size\":4,\"date\":\"2024-05-01\",\"time\":\"14:00\"}", // Customer name too longs
            "{\"customer_name\":25,\"table_size\":4,\"date\":\"2024-05-01\",\"time\":\"14:00\"}", // Customer name not a string
            "{\"customer_name\":\"Uscanga\",\"table_size\":0,\"date\":\"2024-05-01\",\"time\":\"14:00\"}", // Table size 0
            "{\"customer_name\":\"Uscanga\",\"table_size\":-1,\"date\":\"2024-05-01\",\"time\":\"14:00\"}", // Table size negative
            "{\"customer_name\":\"Uscanga\",\"table_size\":999999,\"date\":\"2024-05-01\",\"time\":\"14:00\"}", // Table size too big
            "{\"customer_name\":\"Uscanga\",\"table_size\":false,\"date\":\"2024-05-01\",\"time\":\"14:00\"}", // Table size not a number
            "{\"customer_name\":\"Uscanga\",\"table_size\":4,\"time\":\"14:00\"}", // Missing date
            "{\"customer_name\":\"Uscanga\",\"table_size\":4,\"date\":null,\"time\":\"14:00\"}", // Missing date
            "{\"customer_name\":\"Uscanga\",\"table_size\":4,\"date\":\"\",\"time\":\"14:00\"}", // Empty date
            "{\"customer_name\":\"Uscanga\",\"table_size\":4,\"date\":\"24-05-01\",\"time\":\"14:00\"}", // Incorrect date format
            "{\"customer_name\":\"Uscanga\",\"table_size\":4,\"date\":\"2024/05-01\",\"time\":\"14:00\"}", // Incorrect date format
            "{\"customer_name\":\"Uscanga\",\"table_size\":4,\"date\":\"2024/05/01\",\"time\":\"14:00\"}", // Incorrect date format
            "{\"customer_name\":\"Uscanga\",\"table_size\":4,\"date\":\"01-05-2024\",\"time\":\"14:00\"}", // Incorrect date format
            "{\"customer_name\":\"Uscanga\",\"table_size\":4,\"date\":\"2024-05-01\"}", // Missing time
            "{\"customer_name\":\"Uscanga\",\"table_size\":4,\"date\":\"2024-51-01\",\"time\":\"14:00\"}", // Incorrect date format
            "{\"customer_name\":\"Uscanga\",\"table_size\":4,\"date\":\"2024-05-01\",\"time\":\"\"}", // Empty time
            "{\"customer_name\":\"Uscanga\",\"table_size\":4,\"date\":\"2024-05-01\",\"time\":\"1400\"}", // Incorrect time format
            "{\"customer_name\":\"Uscanga\",\"table_size\":4,\"date\":\"2024-05-01\",\"time\":\"14\"}", // Incorrect time format
            "{\"customer_name\":\"Uscanga\",\"table_size\":4,\"date\":\"2024-05-01\",\"time\":\"25:00\"}", // Incorrect time format
            "{\"customer_name\":\"Uscanga\",\"table_size\":4,\"date\":\"2024-05-01\",\"time\":\"05:00\"}", // Outside the restaurant hours
    })
    public void whenCreateBooking_thenBookingIsNotCreated(final String body) throws IOException {
        MuRequest request = mock(MuRequest.class);
        MuResponse response = mock(MuResponse.class);

        when(request.readBodyAsString()).thenReturn(body);
        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);

        controller.createBooking(request, response, new HashMap<>(0));

        verify(repository, times(0)).save(any(Booking.class));
        verify(response).status(400);
        verify(response).write(responseCaptor.capture());

        String responseContent = responseCaptor.getValue();
        assertTrue(responseContent.contains("\"success\":false"), "Response should contain 'success':false");
    }

    @Test
    public void whenGetBookingsOfDay_thenBookingsAreReturned() {
        MuRequest request = mock(MuRequest.class);
        MuResponse response = mock(MuResponse.class);
        RequestParameters requestParameters = mock(RequestParameters.class);
        Set<Booking> bookings = new HashSet<>();

        when(request.query()).thenReturn(requestParameters);
        when(requestParameters.get("date")).thenReturn("2024-05-01");
        when(requestParameters.contains("pretty")).thenReturn(false);

        when(repository.getBookingsOfDay(any(Instant.class))).thenReturn(bookings);
        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);

        controller.getBookingsOfDay(request, response, new HashMap<>(0));

        verify(repository).getBookingsOfDay(any(Instant.class));
        verify(response).status(200);
        verify(response).write(responseCaptor.capture());

        String responseContent = responseCaptor.getValue();
        assertEquals("[]", responseContent, "Response should be an empty array");
    }

    @ParameterizedTest
    @NullSource
    @CsvSource({
            "''", // Empty date
            "20240501", // Incorrect date format
            "2024-0501", // Incorrect date format
            "24-05-01", // Incorrect date format
            "2024/05/01", // Incorrect date format
    })
    public void whenGetBookingsOfDay_thenErrorIsReturned(final String date) {
        MuRequest request = mock(MuRequest.class);
        MuResponse response = mock(MuResponse.class);
        RequestParameters requestParameters = mock(RequestParameters.class);

        when(request.query()).thenReturn(requestParameters);
        when(requestParameters.get("date")).thenReturn(date);

        ArgumentCaptor<String> responseCaptor = ArgumentCaptor.forClass(String.class);

        controller.getBookingsOfDay(request, response, new HashMap<>(0));

        verify(requestParameters, times(0)).contains("pretty");
        verify(repository, times(0)).getBookingsOfDay(any(Instant.class));
        verify(response).status(400);
        verify(response).write(responseCaptor.capture());

        String responseContent = responseCaptor.getValue();
        assertTrue(responseContent.contains("\"success\":false"), "Response should contain 'success':false");
    }
}
