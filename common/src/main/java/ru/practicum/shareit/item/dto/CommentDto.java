package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {

    private Integer id;

    @NotNull
    @NotBlank
    private String text;

    private Integer itemId;
    private Integer authorId;
    private String authorName;
    private LocalDateTime created;

}
