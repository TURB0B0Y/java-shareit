package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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
        Booking model = BookingMapper.toModel(bookingDto, user, item);
        model.setId(1);
        when(bookingService.approveBooking(anyInt(), anyBoolean(), anyInt()))
                .thenReturn(model);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(model.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.id").value(model.getItem().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.name").value(model.getItem().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.description").value(model.getItem().getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.available").value(model.getItem().isAvailable()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.id").value(model.getBooker().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.name").value(model.getBooker().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.email").value(model.getBooker().getEmail()));
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

        Booking model = BookingMapper.toModel(bookingDto, user, item);
        model.setId(1);
        model.setStatus(BookingStatus.APPROVED);
        when(bookingService.getBooking(anyInt(), anyInt()))
                .thenReturn(model);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(model.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(model.getStatus().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.id").value(model.getItem().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.name").value(model.getItem().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.description").value(model.getItem().getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.available").value(model.getItem().isAvailable()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.id").value(model.getBooker().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.name").value(model.getBooker().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.email").value(model.getBooker().getEmail()));
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

        Booking model = BookingMapper.toModel(bookingDto, user, item);
        model.setId(1);
        when(bookingService.getAllBookingsByBookerId(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(model));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(model.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].item.id").value(model.getItem().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].item.name").value(model.getItem().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].item.description").value(model.getItem().getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].item.available").value(model.getItem().isAvailable()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].booker.id").value(model.getBooker().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].booker.name").value(model.getBooker().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].booker.email").value(model.getBooker().getEmail()));
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

        Booking model = BookingMapper.toModel(bookingDto, user, item);
        model.setId(1);
        when(bookingService.getAllBookingByItemsByOwnerId(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(model));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(model.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].item.id").value(model.getItem().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].item.name").value(model.getItem().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].item.description").value(model.getItem().getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].item.available").value(model.getItem().isAvailable()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].booker.id").value(model.getBooker().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].booker.name").value(model.getBooker().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].booker.email").value(model.getBooker().getEmail()));
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
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists());
    }
}