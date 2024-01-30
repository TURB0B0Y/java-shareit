package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import java.util.Collection;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class ItemBookingCommentDto {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer ownerId;
    private Integer requestId;
    @ToString.Exclude
    private ShortBookingDto lastBooking;
    @ToString.Exclude
    private ShortBookingDto nextBooking;
    @ToString.Exclude
    private Collection<CommentDto> comments;

}
