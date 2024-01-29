package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest addRequest(CreateItemRequestDto dto, int userId);

    List<ItemRequestWithItemsDto> getRequests(int userId);

    List<ItemRequestWithItemsDto> getRequests(int userId, int from, int size);

    ItemRequestWithItemsDto getRequestById(int userId, int requestId);

}
