package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.APIBadRequestException;
import ru.practicum.shareit.exception.APINotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(int bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new APINotFoundException("Бронирование id %s не найдено", bookingId));
    }

    @Override
    @Transactional
    public Booking createBooking(CreateBookingDto dto, int userId) {
        LocalDateTime nowTime = LocalDateTime.now();
        if (dto.getStart().isBefore(nowTime)) {
            throw new APIBadRequestException("Дата начала %s находится в прошлом. Время сервера: [%s]", dto.getStart(), nowTime);
        }
        if (!dto.getEnd().isAfter(dto.getStart())) {
            throw new APIBadRequestException("Дата начала %s после либо равна дате окончания %s", dto.getStart(), dto.getEnd());
        }

        Item item = getItemById(dto.getItemId());
        if (!item.isAvailable()) {
            throw new APIBadRequestException("Предмет %s недоступен для бронирования", item.getId());
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new APINotFoundException("Предмет %s недоступен для бронирования", item.getId());
        }

        User booker = getUserById(userId);

        Booking booking = BookingMapper.toModel(dto, booker, item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setId(null);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking approveBooking(int bookingId, boolean approved, int userId) {
        Booking booking = getBookingById(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new APINotFoundException("Бронирование %s не найдено", bookingId);
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new APIBadRequestException("Бронирование %s не находится в статусе ожидания", bookingId);
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBooking(int bookingId, int userId) {
        Booking booking = getBookingById(bookingId);
        int itemOwnerId = booking.getItem().getOwner().getId();
        int bookerOwnerId = booking.getBooker().getId();
        if (itemOwnerId != userId && bookerOwnerId != userId) {
            throw new APINotFoundException("Бронирование %s не найдено", bookingId);
        }
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookingsByBookerId(int userId, BookingState state, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new APINotFoundException("Пользователь %s не найден", userId);
        }

        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from / size, size, sort);
        LocalDateTime nowTime = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBefore(userId, nowTime, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfter(userId, nowTime, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndEndIsAfterAndStartIsBefore(userId, nowTime, nowTime, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                return Collections.emptyList();
        }
        return bookings;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookingByItemsByOwnerId(int userId, BookingState state, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new APINotFoundException("Пользователь %s не найден", userId);
        }

        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from / size, size, sort);
        LocalDateTime nowTime = LocalDateTime.now();
        switch (state) {
            case PAST:
                bookings = bookingRepository.bookingsForItemPast(userId, nowTime, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.bookingsForItemFuture(userId, nowTime, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.bookingsForItemCurrent(userId, nowTime, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.bookingsByItemOwnerAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.bookingsByItemOwnerAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                bookings = bookingRepository.bookingsForItem(userId, pageable);
        }
        return bookings;
    }

    private User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new APINotFoundException("Пользователь id %s не найден", userId));
    }

    private Item getItemById(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new APINotFoundException("Предмет %s не найден ", itemId));
    }

}
