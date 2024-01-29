package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.APIAccessDeniedException;
import ru.practicum.shareit.exception.APIBadRequestException;
import ru.practicum.shareit.exception.APINotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    public void beforeEach() {
        itemService = new ItemServiceImpl(
                itemRepository,
                commentRepository,
                userRepository,
                bookingRepository,
                itemRequestRepository
        );
    }

    @Test
    void addItem() {
        int itemId = 1;
        int userId = 1;
        CreateItemDto createItemDto = new CreateItemDto("TestItem", "DescriptionTest", true, userId, null);
        User user = new User(userId, "test", "test@test.com");
        Item item = ItemMapper.toModel(createItemDto, user, null);
        item.setId(itemId);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any()))
                .thenReturn(item);
        assertEquals(createItemDto.getName(), itemService.createItem(createItemDto).getName());
    }

    @Test
    void createItemGetItemRequestByIdTest() {
        int itemId = 1;
        int userId = 1;
        int requestId = 1;
        User user = new User(userId, "test", "test@test.com");

        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("test");
        ItemRequest request = ItemRequestMapper.toModel(dto, user);
        request.setId(requestId);

        CreateItemDto createItemDto = new CreateItemDto("TestItem", "DescriptionTest", true, userId, requestId);
        Item item = ItemMapper.toModel(createItemDto, user, request);
        item.setId(itemId);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(request));
        when(itemRepository.save(any()))
                .thenReturn(item);
        assertEquals(createItemDto.getName(), itemService.createItem(createItemDto).getName());
    }

    @Test
    void deleteItemById() {
        int itemId = 1;
        int userId = 1;
        User user = new User(userId, "test", "test@test.com");
        CreateItemDto createItemDto = new CreateItemDto("TestItem", "DescriptionTest", true, userId, null);
        Item item = ItemMapper.toModel(createItemDto, user, null);
        item.setId(itemId);
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        assertThrows(APIAccessDeniedException.class, () -> itemService.deleteItemById(itemId, 4));
    }

    @Test
    void addItemNoUser() {
        CreateItemDto createItemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        assertThrows(NullPointerException.class, () -> itemService.createItem(createItemDto));
    }

    @Test
    void patchItem() {
        int itemId = 1;
        int requestId = 1;
        int ownerId = 1;
        final ItemRequest request = new ItemRequest(
                requestId,
                "description",
                new User(3, "test", "test@test.com"),
                LocalDateTime.now(),
                Collections.emptyList()
        );
        User user = new User(ownerId, "test", "test@test.com");
        CreateItemDto createItemDto = new CreateItemDto("TestItem", "DescriptionTest", true, ownerId, request.getId());
        Item item = ItemMapper.toModel(createItemDto, user, request);
        item.setId(itemId);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        assertEquals(
                item,
                itemService.editItem(ItemDto.builder().id(itemId)
                        .ownerId(ownerId).requestId(requestId).build())
        );
    }

    @Test
    void patchItemNotFound() {
        int itemId = 1;
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .name("TestItem")
                .description("DescriptionTest")
                .available(true)
                .build();
        assertThrows(APINotFoundException.class, () -> itemService.editItem(itemDto));
    }

    @Test
    void patchItemWithIdOnly() {
        int itemId = 1;
        int ownerId = 1;
        CreateItemDto createItemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        User user = new User(ownerId, "test", "test@test.com");
        Item item = ItemMapper.toModel(createItemDto, user, null);
        ItemDto newItemDto = ItemDto.builder()
                .id(itemId)
                .ownerId(ownerId)
                .build();
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        assertEquals(item, itemService.editItem(newItemDto));
    }

    @Test
    void bookingsIsNotEmptyGetItem() {
        int itemId = 1;
        int ownerId = 1;
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        User user = new User(ownerId, "test", "test@test.com");
        Item item = ItemMapper.toModel(itemDto, user, null);
        item.setId(itemId);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setStatus(BookingStatus.WAITING);

        ItemBookingCommentDto getItemDto = ItemMapper.toItemBookingCommentDto(item, null, null, Collections.emptyList());

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItem_Id(anyInt()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findFirstByItem_IdAndStartBefore(anyInt(), any(), any()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItem_IdAndStartAfterAndStatusNot(anyInt(), any(), any(), any()))
                .thenReturn(Optional.empty());
        assertEquals(getItemDto, itemService.getItemBookingById(itemId, ownerId));
    }

    @Test
    void getItem() {
        int itemId = 1;
        int ownerId = 1;
        User user = new User(ownerId, "test", "test@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, user, null);
        ItemBookingCommentDto getItemDto = ItemMapper.toItemBookingCommentDto(item, null, null, Collections.emptyList());

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItem_Id(anyInt()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findFirstByItem_IdAndStartBefore(anyInt(), any(), any()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItem_IdAndStartAfterAndStatusNot(anyInt(), any(), any(), any()))
                .thenReturn(Optional.empty());

        assertEquals(getItemDto, itemService.getItemBookingById(itemId, ownerId));
    }

    @Test
    void getItemNotFound() {
        int itemId = 1;
        int ownerId = 1;
        assertThrows(APINotFoundException.class, () -> itemService.getItemBookingById(itemId, ownerId));
    }

    @Test
    void getAllItemsByOwner() {
        int ownerId = 1;
        User user = new User(ownerId, "test", "test@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, user, null);
        ItemBookingDto getItemDto = ItemMapper.toItemBookingDto(item, null, null);

        when(itemRepository.findAllByOwnerId(anyInt(), any())).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemInAndStartBefore(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemInAndStartAfterAndStatusNot(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        assertEquals(List.of(getItemDto), itemService.getAllByOwnerId(ownerId, 0, 10));
    }

    @Test
    void searchItem() {
        int ownerId = 1;
        User user = new User(ownerId, "test", "test@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, user, null);

        when(itemRepository.findAllByNameOrDescription(anyString(), any())).thenReturn(List.of(item));

        assertEquals(List.of(item), itemService.search("test", 0, 10));
    }

    @Test
    void searchItemEmptyText() {
        assertEquals(Collections.emptyList(), itemService.search("", 0, 10));
    }

    @Test
    void addComment() {
        int itemId = 1;
        int ownerId = 1;
        User user = new User(ownerId, "test", "test@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, user, null);
        item.setId(itemId);
        Comment comment = new Comment();
        comment.setId(1);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        comment.setText("test");
        comment.setAuthor(new User(ownerId, "test", "test@test.com"));
        Booking booking = new Booking(1, LocalDateTime.MIN, LocalDateTime.MIN.plusHours(1), item, new User(ownerId, "test", "test@test.com"), BookingStatus.APPROVED);
        when(bookingRepository.findFirstByItem_IdAndBooker_IdAndEndBefore(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(booking));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        assertEquals(comment.getText(), itemService.addComment(CommentMapper.toDto(comment), itemId, 1).getText());
    }

    @Test
    void addCommentNoBooking() {
        int itemId = 1;
        int ownerId = 1;
        User user = new User(ownerId, "test", "test@test.com");
        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, null, null);
        Item item = ItemMapper.toModel(itemDto, user, null);
        item.setId(itemId);
        Comment comment = new Comment();
        comment.setId(1);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        comment.setText("test");
        comment.setAuthor(new User(ownerId, "test", "test@test.com"));
        when(bookingRepository.findFirstByItem_IdAndBooker_IdAndEndBefore(anyInt(), anyInt(), any()))
                .thenReturn(Optional.empty());

        assertThrows(APIBadRequestException.class, () -> itemService.addComment(CommentMapper.toDto(comment), itemId, 1));
    }
}