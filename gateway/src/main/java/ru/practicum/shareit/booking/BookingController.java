package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.APIBadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@SuppressWarnings("unused")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestBody @Valid CreateBookingDto dto,
            @RequestHeader("X-Sharer-User-Id") int userId
    ) {
        log.info("createBooking {} from {}", dto, userId);
        LocalDateTime nowTime = LocalDateTime.now();
        if (dto.getStart().isBefore(nowTime)) {
            throw new APIBadRequestException("Дата начала %s находится в прошлом. Время сервера: [%s]", dto.getStart(), nowTime);
        }
        if (!dto.getEnd().isAfter(dto.getStart())) {
            throw new APIBadRequestException("Дата начала %s после либо равна дате окончания %s", dto.getStart(), dto.getEnd());
        }
        return bookingClient.createBooking(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @PathVariable int bookingId,
            @RequestParam boolean approved,
            @RequestHeader("X-Sharer-User-Id") int userId
    ) {
        log.info("approveBooking {} {} {}", bookingId, approved, userId);
        return bookingClient.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("getBooking {} {}", bookingId, userId);
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByBookerId(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        log.info("getAllBookingsByBookerId {} {} {}", state, from, size);
        return bookingClient.getAllBookingsByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingItemsByBookerId(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        log.info("getAllBookingItemsByBookerId {} {} {}", state, from, size);
        return bookingClient.getAllBookingByItemsByOwnerId(userId, state, from, size);
    }

}
