package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.APINotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(SpringExtension.class)
public class ItemServiceIntegrationTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    public void setup() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository);
    }

    @Test
    public void testAddRequest_ExistingUser_ReturnsItemRequest() {
        CreateItemRequestDto requestDto = new CreateItemRequestDto();
        int userId = 1;
        User user = new User();
        Mockito.when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(new ItemRequest());

        ItemRequest result = itemRequestService.addRequest(requestDto, userId);

        Assertions.assertNotNull(result);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(any(ItemRequest.class));
    }

    @Test
    public void testAddRequest_NonExistingUser_ThrowsNotFoundException() {
        CreateItemRequestDto requestDto = new CreateItemRequestDto();
        int userId = 1;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(APINotFoundException.class, () -> itemRequestService.addRequest(requestDto, userId));
        Mockito.verify(itemRequestRepository, Mockito.never()).save(any(ItemRequest.class));
    }

    @Test
    public void testGetRequests_ExistingUser_ReturnsListOfGetItemRequestDto() {
        int userId = 1;
        new User();
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRepository.findAllByOwnerId(anyInt(), any())).thenReturn(new ArrayList<>());
        Mockito.when(itemRequestRepository.findAllByRequesterId(userId, Sort.by(Sort.Direction.ASC, "created")))
                .thenReturn(new ArrayList<>());

        List<ItemRequestWithItemsDto> result = itemRequestService.getRequests(userId);

        Assertions.assertNotNull(result);
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequesterId(userId, Sort.by(Sort.Direction.ASC, "created"));
    }

    @Test
    public void testGetRequests_NonExistingUser_ThrowsNotFoundException() {
        int userId = 1;
        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Assertions.assertThrows(APINotFoundException.class, () -> itemRequestService.getRequests(userId));
        Mockito.verify(itemRepository, Mockito.never()).findAllByOwnerId(anyInt(), any());
        Mockito.verify(itemRequestRepository, Mockito.never())
                .findAllByRequesterId(anyInt(), any(Sort.class));
    }

    @Test
    public void testGetRequestsPageable_NonExistingUser_ThrowsNotFoundException() {
        int userId = 1;
        int from = 0;
        int size = 10;
        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Assertions.assertThrows(APINotFoundException.class, () -> itemRequestService.getRequests(userId, from, size));
        Mockito.verify(itemRepository, Mockito.never()).findAllByOwnerId(anyInt(), any());
    }

    @Test
    public void testGetRequestById_ExistingUserAndExistingRequest_ReturnsGetItemRequestDto() {
        int userId = 1;
        int requestId = 1;
        new User();
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRequestRepository.existsById(requestId)).thenReturn(true);
        ItemRequest request = new ItemRequest();
        request.setItems(Collections.emptyList());
        Mockito.when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        ItemRequestWithItemsDto result = itemRequestService.getRequestById(userId, requestId);

        Assertions.assertNotNull(result);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(requestId);
    }

    @Test
    public void testGetRequestById_NonExistingUser_ThrowsNotFoundException() {
        int userId = 1;
        int requestId = 1;
        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Assertions.assertThrows(APINotFoundException.class, () -> itemRequestService.getRequestById(userId, requestId));
        Mockito.verify(itemRequestRepository, Mockito.never()).findById(anyInt());
        Mockito.verify(itemRepository, Mockito.never()).findAllByOwnerId(anyInt(), any());
    }

    @Test
    public void testGetRequestById_ExistingUserAndNonExistingRequest_ThrowsNotFoundException() {
        int userId = 1;
        int requestId = 1;
        new User();
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRequestRepository.existsById(requestId)).thenReturn(false);

        Assertions.assertThrows(APINotFoundException.class, () -> itemRequestService.getRequestById(userId, requestId));
    }
}