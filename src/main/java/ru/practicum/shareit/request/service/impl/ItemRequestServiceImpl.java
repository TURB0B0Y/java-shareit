package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.APINotFoundException;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequest addRequest(CreateItemRequestDto dto, int userId) {
        User user = getUserById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toModel(dto, user);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestWithItemsDto> getRequests(int userId) {
        if (notExistsUserById(userId)) {
            throw new APINotFoundException("Пользователь id %s не найден", userId);
        }
        return itemRequestRepository.findAllByRequesterId(
                        userId,
                        Sort.by(Sort.Direction.ASC, "created")
                ).stream()
                .map(ItemRequestMapper::toWithItemsDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestWithItemsDto> getRequests(int userId, int from, int size) {
        if (notExistsUserById(userId)) {
            throw new APINotFoundException("Пользователь id %s не найден", userId);
        }
        return itemRequestRepository.findAllByRequesterIdIsNot(userId, PageRequest.of(
                        from / size,
                        size,
                        Sort.by(Sort.Direction.ASC, "created")
                ))
                .stream()
                .map(ItemRequestMapper::toWithItemsDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestWithItemsDto getRequestById(int userId, int requestId) {
        if (notExistsUserById(userId)) {
            throw new APINotFoundException("Пользователь id %s не найден", userId);
        }
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new APINotFoundException("Запрос id %s не найден", requestId));
        return ItemRequestMapper.toWithItemsDto(request);
    }

    private User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new APINotFoundException("Пользователь id %s не найден", userId));
    }

    private boolean notExistsUserById(int userId) {
        return !userRepository.existsById(userId);
    }

}
