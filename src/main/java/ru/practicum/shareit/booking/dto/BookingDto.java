package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class BookingDto {

    private Integer id;
    private LocalDate start;
    private LocalDate end;
    private Integer itemId;
    private Integer bookerId;
    private BookingStatus status;

}
