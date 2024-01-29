package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(
            @RequestBody CreateBookingDto dto,
            @RequestHeader("X-Sharer-User-Id") int userId
    ) {
        log.info("createBooking {} from {}", dto, userId);
        Booking booking = bookingService.createBooking(dto, userId);
        return BookingMapper.toDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @PathVariable int bookingId,
            @RequestParam boolean approved,
            @RequestHeader("X-Sharer-User-Id") int userId
    ) {
        log.info("approveBooking {} {} {}", bookingId, approved, userId);
        Booking booking = bookingService.approveBooking(bookingId, approved, userId);
        return BookingMapper.toDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("getBooking {} {}", bookingId, userId);
        Booking booking = bookingService.getBooking(bookingId, userId);
        return BookingMapper.toDto(booking);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByBookerId(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam BookingState state,
            @RequestParam int from,
            @RequestParam int size
    ) {
        log.info("getAllBookingsByBookerId {} {} {}", state, from, size);
        List<Booking> bookings = bookingService.getAllBookingsByBookerId(userId, state, from, size);
        return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingItemsByBookerId(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam BookingState state,
            @RequestParam int from,
            @RequestParam int size
    ) {
        log.info("getAllBookingItemsByBookerId {} {} {}", state, from, size);
        List<Booking> bookings = bookingService.getAllBookingByItemsByOwnerId(userId, state, from, size);
        return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }

}
