package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {

    @Test
    void toModelMapperTest() {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        User owner = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, owner, null);
        item.setId(1);
        Booking booking = BookingMapper.toModel(dto, owner, item);
        booking.setId(1);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto bookingDto = BookingMapper.toDto(booking);
        assertEquals(booking.getId(), bookingDto.getId());
    }

    @Test
    void toDtoMapperTest() {
        User user = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, user, null);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));
        booking.setStatus(BookingStatus.APPROVED);

        BookingDto dto = BookingMapper.toDto(booking);

        assertEquals(booking.getId(), dto.getId());
    }

    @Test
    void toShortBookingDtoMapperTest() {
        User user = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, user, null);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));
        booking.setStatus(BookingStatus.APPROVED);

        ShortBookingDto dto = BookingMapper.toShortBookingDto(booking);

        assertEquals(booking.getId(), dto.getId());
    }
}
