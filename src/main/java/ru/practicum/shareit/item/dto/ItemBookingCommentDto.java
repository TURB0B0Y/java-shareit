package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import java.util.Collection;

@Getter
@Setter
@ToString
@Builder
public class ItemBookingCommentDto {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer ownerId;
    private Integer requestId;
    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
    private Collection<CommentDto> comments;

}
