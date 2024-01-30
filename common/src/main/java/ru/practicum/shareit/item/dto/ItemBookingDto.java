package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class ItemBookingDto {

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

}
