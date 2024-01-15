package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemBookingCommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto.ItemDtoBuilder builder = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable());
        if (Objects.nonNull(item.getRequest())) {
            builder.requestId(item.getRequest().getId());
        }
        if (Objects.nonNull(item.getOwner())) {
            builder.ownerId(item.getOwner().getId());
        }
        return builder.build();
    }

    public static Item toModel(CreateItemDto dto, User owner) {
        Item item = new Item();
        item.setOwner(owner);
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        return item;
    }

    public static ItemBookingDto toItemBookingDto(Item item, Booking nextBooking, Booking lastBooking) {
        ItemBookingDto.ItemBookingDtoBuilder builder = ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable());
        if (Objects.nonNull(item.getRequest())) {
            builder.requestId(item.getRequest().getId());
        }
        if (Objects.nonNull(item.getOwner())) {
            builder.ownerId(item.getOwner().getId());
        }
        if (Objects.nonNull(nextBooking)) {
            builder.nextBooking(BookingMapper.toShortBookingDto(nextBooking));
        }
        if (Objects.nonNull(lastBooking)) {
            builder.lastBooking(BookingMapper.toShortBookingDto(lastBooking));
        }
        return builder.build();
    }

    public static ItemBookingCommentDto toItemBookingCommentDto(Item item, Booking nextBooking, Booking lastBooking, List<Comment> comments) {
        ItemBookingCommentDto.ItemBookingCommentDtoBuilder builder = ItemBookingCommentDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable());
        if (Objects.nonNull(item.getRequest())) {
            builder.requestId(item.getRequest().getId());
        }
        if (Objects.nonNull(item.getOwner())) {
            builder.ownerId(item.getOwner().getId());
        }
        if (Objects.nonNull(nextBooking)) {
            builder.nextBooking(BookingMapper.toShortBookingDto(nextBooking));
        }
        if (Objects.nonNull(lastBooking)) {
            builder.lastBooking(BookingMapper.toShortBookingDto(lastBooking));
        }
        if (Objects.nonNull(comments)) {
            builder.comments(comments.stream().map(CommentMapper::toDto).collect(Collectors.toList()));
        }
        return builder.build();
    }

}
