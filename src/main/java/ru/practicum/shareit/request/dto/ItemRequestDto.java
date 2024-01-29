package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ItemRequestDto {

    private Integer id;
    private String description;
    private Integer requesterId;
    private LocalDateTime created;

}
