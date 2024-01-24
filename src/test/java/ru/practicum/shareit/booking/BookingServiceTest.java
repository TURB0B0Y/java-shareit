package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.exception.APIBadRequestException;
import ru.practicum.shareit.exception.APINotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void init() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    @Test
    void addBooking() {
        int itemId = 1;
        int ownerId = 2;
        int bookerId = 1;

        User newUser = new User(bookerId, "test", "test@test.com");
        User test = new User(3, "test", "test@test.com");
        final ItemRequest request = new ItemRequest(0, "description", test, LocalDateTime.now(), Collections.emptyList());
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, request.getId());
        Item item = ItemMapper.toModel(itemDto, new User(ownerId, "test2", "test2@test.com"), request);
        item.setId(itemId);

        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();
        Booking model = BookingMapper.toModel(dto, newUser, item);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(newUser));
        when(bookingRepository.save(any())).thenReturn(model);
        assertEquals(model, bookingService.createBooking(dto, bookerId));
    }

    @Test
    void testAddBookingItemUnavailable() {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();
        int bookerId = 2;

        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        User user = new User(bookerId, "test2", "test2@test.com");
        Item item = ItemMapper.toModel(itemDto, user, null);
        item.setId(1);
        itemRepository.save(item);

        assertThrows(APIBadRequestException.class, () -> bookingService.createBooking(dto, bookerId));
    }

    @Test
    void addBooking_whenItemIsNotAvailable() {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        User owner = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, owner, null);
        item.setId(1);
        item.setAvailable(false);

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(APIBadRequestException.class, () -> bookingService.createBooking(dto, owner.getId()));
    }

    @Test
    void addBooking_whenEndDateIsBeforeStartDate() {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().minusHours(1))
                .build();

        User owner = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, owner, null);
        item.setId(1);
        item.setAvailable(true);
        dto.setEnd(LocalDateTime.now().plusDays(-1));

        assertThrows(APIBadRequestException.class, () -> bookingService.createBooking(dto, owner.getId()));
    }

    @Test
    void addBooking_whenBookerIdIsInvalid() {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        User owner = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, owner, null);
        item.setId(1);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        assertThrows(APINotFoundException.class, () -> bookingService.createBooking(dto, owner.getId()));
    }

    @Test
    void approveBookingNotBooking() {
        int itemId = 1;
        int bookerId = 1;
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(APINotFoundException.class, () -> bookingService.approveBooking(bookerId, true, itemId));
    }

    @Test
    void approveBookingOtherItemOwner() {
        int bookerId = 1;

        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        User owner = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, owner, null);
        item.setId(1);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(BookingMapper.toModel(
                dto,
                owner,
                item
        )));
        assertThrows(APINotFoundException.class, () -> bookingService.approveBooking(bookerId, true, 2));
    }

    @Test
    void approveBooking() {
        int bookerId = 1;

        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        User owner = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, owner, null);
        item.setId(1);
        Booking booking = BookingMapper.toModel(dto, owner, item);
        booking.setId(1);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertEquals(booking, bookingService.approveBooking(bookerId, true, owner.getId()));
    }

    @Test
    void getBooking() {
        int bookerId = 1;

        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        User owner = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, owner, null);
        item.setId(1);
        Booking booking = BookingMapper.toModel(dto, owner, item);
        booking.setId(1);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        assertEquals(booking, bookingService.getBooking(booking.getId(), bookerId));
    }

    @Test
    public void getBookingWithDifferentBookerAndOwnerIds() {
        int bookingId = 1;
        int bookerId = 2;

        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        User owner = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, owner, null);
        item.setId(1);
        Booking booking = BookingMapper.toModel(dto, owner, item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(APINotFoundException.class, () -> bookingService.getBooking(bookingId, bookerId));
    }

    @Test
    void getBookingNotFound() {
        int bookerId = 1;
        assertThrows(APINotFoundException.class, () -> bookingService.getBooking(1, bookerId));
    }

    @Test
    void getAllBookings() {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        User owner = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, owner, null);
        item.setId(1);
        Booking booking = BookingMapper.toModel(dto, owner, item);

        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(bookingRepository.findAllByBookerId(anyInt(), any()))
                .thenReturn(Collections.singletonList(BookingMapper.toModel(dto, owner, item)));

        assertEquals(
                Collections.singletonList(booking),
                bookingService.getAllBookingsByBookerId(owner.getId(), BookingState.ALL, 0, 10)
        );
    }

    @Test
    void getPastBookings() {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().minusHours(3))
                .end(LocalDateTime.now().minusHours(2))
                .build();

        User owner = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, owner, null);
        item.setId(1);
        Booking booking = BookingMapper.toModel(dto, owner, item);

        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(bookingRepository.findByBookerIdAndEndBefore(anyInt(), any(), any())).thenReturn(Collections.singletonList(BookingMapper.toModel(dto, owner, item)));

        assertEquals(
                Collections.singletonList(booking),
                bookingService.getAllBookingsByBookerId(owner.getId(), BookingState.PAST, 0, 10)
        );
    }

    @Test
    void getFutureBookings() {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        User owner = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, owner, null);
        item.setId(1);
        Booking booking = BookingMapper.toModel(dto, owner, item);

        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStartAfter(anyInt(), any(), any())).thenReturn(Collections.singletonList(BookingMapper.toModel(dto, owner, item)));

        assertEquals(
                Collections.singletonList(booking),
                bookingService.getAllBookingsByBookerId(owner.getId(), BookingState.FUTURE, 0, 10)
        );
    }

    @Test
    void getCurrentBookings() {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        User owner = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, owner, null);
        item.setId(1);
        Booking booking = BookingMapper.toModel(dto, owner, item);

        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(bookingRepository.findByBookerIdAndEndIsAfterAndStartIsBefore(anyInt(), any(), any(), any()))
                .thenReturn(Collections.singletonList(BookingMapper.toModel(dto, owner, item)));

        assertEquals(
                Collections.singletonList(booking),
                bookingService.getAllBookingsByBookerId(owner.getId(), BookingState.CURRENT, 0, 10)
        );
    }

    @Test
    void getAllBookingsNotFoundUser() {
        when(userRepository.existsById(anyInt())).thenReturn(false);
        assertThrows(APINotFoundException.class, () -> bookingService.getAllBookingsByBookerId(1, BookingState.ALL, 0, 10));
    }

    @Test
    void getAllBookingsByItems() {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        User owner = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, owner, null);
        item.setId(1);
        Booking booking = BookingMapper.toModel(dto, owner, item);

        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(bookingRepository.bookingsForItem(anyInt(), any())).thenReturn(Collections.singletonList(BookingMapper.toModel(dto, owner, item)));
        assertEquals(
                Collections.singletonList(booking),
                bookingService.getAllBookingByItemsByOwnerId(1, BookingState.ALL, 0, 10)
        );
    }

    @Test
    void getPastBookingsByItems() {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        User owner = new User(1, "test2", "test2@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, owner, null);
        item.setId(1);
        Booking booking = BookingMapper.toModel(dto, owner, item);
        when(userRepository.existsById(anyInt())).thenReturn(true);
        List<Booking> bookingList = Collections.singletonList(BookingMapper.toModel(dto, owner, item));
        when(bookingRepository.bookingsForItemPast(anyInt(), any(), any())).thenReturn(bookingList);
        when(bookingRepository.bookingsForItemFuture(anyInt(), any(), any())).thenReturn(bookingList);
        when(bookingRepository.bookingsForItemCurrent(anyInt(), any(), any())).thenReturn(bookingList);
        when(bookingRepository.bookingsByItemOwnerAndStatus(anyInt(), any(), any())).thenReturn(bookingList);


        List<Booking> result = Collections.singletonList(booking);
        assertEquals(result, bookingService.getAllBookingByItemsByOwnerId(1, BookingState.PAST, 0, 10));
        assertEquals(result, bookingService.getAllBookingByItemsByOwnerId(1, BookingState.FUTURE, 0, 10));
        assertEquals(result, bookingService.getAllBookingByItemsByOwnerId(1, BookingState.CURRENT, 0, 10));
        assertEquals(result, bookingService.getAllBookingByItemsByOwnerId(1, BookingState.WAITING, 0, 10));
        assertEquals(result, bookingService.getAllBookingByItemsByOwnerId(1, BookingState.REJECTED, 0, 10));
    }

    @Test
    void getAllBookingsByItemsNotFoundUser() {
        when(userRepository.existsById(anyInt())).thenReturn(false);
        assertThrows(APINotFoundException.class,
                () -> bookingService.getAllBookingByItemsByOwnerId(1, BookingState.REJECTED, 0, 10));
    }

    @Test
    void fullBookingTest() {
        assertThrows(APINotFoundException.class,
                () -> bookingService.getAllBookingByItemsByOwnerId(1, BookingState.REJECTED, 0, 10));
    }
}