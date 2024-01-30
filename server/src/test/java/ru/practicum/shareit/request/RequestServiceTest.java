package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @Test
    public void testSetDescription() {
        ItemRequest itemRequest = new ItemRequest();
        String description = "test description";
        itemRequest.setDescription(description);
        assertEquals(description, itemRequest.getDescription());
    }

    @Test
    void addRequest() {
        int userId = 1;
        User newUser = new User(userId, "test", "test@test.com");
        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("test");
        ItemRequest request = ItemRequestMapper.toModel(dto, newUser);
        request.setItems(Collections.emptyList());
        when(itemRequestRepository.save(any()))
                .thenReturn(request);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(newUser));
        assertEquals(request, requestService.addRequest(dto, userId));
    }

    @Test
    void getRequests() {
        int userId = 1;
        int itemId = 1;
        int requestId = 1;

        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("test");

        User newUser = new User(userId, "test", "test@test.com");
        ItemRequest request = ItemRequestMapper.toModel(dto, newUser);
        request.setId(requestId);

        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, newUser.getId(), request.getId());
        Item item = ItemMapper.toModel(itemDto, newUser, request);
        item.setId(itemId);
        request.setItems(Collections.singletonList(item));
        ItemRequestWithItemsDto withItemsDto = ItemRequestMapper.toWithItemsDto(request);
        when(itemRequestRepository.findAllByRequesterId(anyInt(), any()))
                .thenReturn(List.of(request));
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        assertEquals(withItemsDto.getDescription(), requestService.getRequests(userId).get(0).getDescription());
    }

    @Test
    void getRequestsPageable() {
        int userId = 1;
        int itemId = 1;
        int requestId = 1;

        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("test");

        User newUser = new User(userId, "test", "test@test.com");
        ItemRequest request = ItemRequestMapper.toModel(dto, newUser);
        request.setId(requestId);

        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, newUser.getId(), request.getId());
        Item item = ItemMapper.toModel(itemDto, newUser, request);
        item.setId(itemId);
        request.setItems(Collections.singletonList(item));
        ItemRequestWithItemsDto withItemsDto = ItemRequestMapper.toWithItemsDto(request);

        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdIsNot(anyInt(), any())).thenReturn(List.of(request));
        assertEquals(withItemsDto.getDescription(),
                requestService.getRequests(userId, 0, 10).get(0).getDescription());
    }

    @Test
    void getRequestById() {
        int userId = 1;
        int itemId = 1;
        int requestId = 1;

        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("test");

        User newUser = new User(userId, "test", "test@test.com");
        ItemRequest request = ItemRequestMapper.toModel(dto, newUser);
        request.setId(requestId);

        CreateItemDto itemDto = new CreateItemDto("TestItem", "DescriptionTest", true, newUser.getId(), request.getId());
        Item item = ItemMapper.toModel(itemDto, newUser, request);
        item.setId(itemId);
        request.setItems(Collections.singletonList(item));
        ItemRequestWithItemsDto withItemsDto = ItemRequestMapper.toWithItemsDto(request);

        when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(request));
        assertEquals(withItemsDto.getDescription(), requestService.getRequestById(userId, requestId).getDescription());
    }

}