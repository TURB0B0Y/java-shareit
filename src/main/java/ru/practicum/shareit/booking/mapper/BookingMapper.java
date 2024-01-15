package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

public class BookingMapper {

    public static Booking toModel(CreateBookingDto dto, User booker, Item item) {
        return Booking.builder()
                .item(item)
                .booker(booker)
                .end(dto.getEnd())
                .start(dto.getStart())
                .build();
    }

    public static BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(Objects.isNull(booking.getBooker()) ? null : UserMapper.toDto(booking.getBooker()))
                .item(Objects.isNull(booking.getItem()) ? null : ItemMapper.toItemDto(booking.getItem()))
                .build();
    }

    public static ShortBookingDto toShortBookingDto(Booking booking) {
        return ShortBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .bookerId(Objects.isNull(booking.getBooker()) ? null : booking.getBooker().getId())
                .itemId(Objects.isNull(booking.getItem()) ? null : booking.getItem().getId())
                .build();
    }

}
