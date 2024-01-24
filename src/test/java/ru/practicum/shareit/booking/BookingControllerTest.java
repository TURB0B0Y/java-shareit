package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.CustomExceptionHandler;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BookingController.class, CustomExceptionHandler.class})
public class BookingControllerTest {

    @MockBean
    BookingService bookingService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    void approveBooking() throws Exception {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        Item item = new Item();
        item.setId(1);
        item.setName("TestItem");
        item.setDescription("DescriptionTest");
        item.setAvailable(true);

        User user = new User(2, "test2", "test2@test.com");
        CreateBookingDto bookingDto = CreateBookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();
        when(bookingService.approveBooking(anyInt(), anyBoolean(), anyInt()))
                .thenReturn(BookingMapper.toModel(bookingDto, user, item));

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking() throws Exception {
        Item item = new Item();
        item.setId(1);
        item.setName("TestItem");
        item.setDescription("DescriptionTest");
        item.setAvailable(true);

        User user = new User(2, "test2", "test2@test.com");
        CreateBookingDto bookingDto = CreateBookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        when(bookingService.getBooking(anyInt(), anyInt()))
                .thenReturn(BookingMapper.toModel(bookingDto, user, item));

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBooking() throws Exception {
        Item item = new Item();
        item.setId(1);
        item.setName("TestItem");
        item.setDescription("DescriptionTest");
        item.setAvailable(true);

        User user = new User(2, "test2", "test2@test.com");
        CreateBookingDto bookingDto = CreateBookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        when(bookingService.getAllBookingsByBookerId(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(BookingMapper.toModel(bookingDto, user, item)));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingItems() throws Exception {
        Item item = new Item();
        item.setId(1);
        item.setName("TestItem");
        item.setDescription("DescriptionTest");
        item.setAvailable(true);

        User user = new User(2, "test2", "test2@test.com");
        CreateBookingDto bookingDto = CreateBookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        when(bookingService.getAllBookingByItemsByOwnerId(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(BookingMapper.toModel(bookingDto, user, item)));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingItemsUnsupportedStatus() throws Exception {
        Item item = new Item();
        item.setId(1);
        item.setName("TestItem");
        item.setDescription("DescriptionTest");
        item.setAvailable(true);

        User user = new User(2, "test2", "test2@test.com");
        CreateBookingDto bookingDto = CreateBookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        when(bookingService.getAllBookingByItemsByOwnerId(anyInt(), any(), anyInt(), anyInt()))
                .thenThrow(MethodArgumentTypeMismatchException.class);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}