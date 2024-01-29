package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    Booking getBookingById(int bookingId);

    Booking createBooking(CreateBookingDto dto, int userId);

    Booking approveBooking(int bookingId, boolean approved, int userId);

    Booking getBooking(int bookingId, int userId);

    List<Booking> getAllBookingsByBookerId(int userId, BookingState state, int from, int size);

    List<Booking> getAllBookingByItemsByOwnerId(int userId, BookingState state, int from, int size);

}
