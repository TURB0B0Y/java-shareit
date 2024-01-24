package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemRequestController.class})
public class RequestControllerTest {
    @MockBean
    private ItemRequestService requestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    void addRequest() throws Exception {
        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("test");
        User user = new User(1, "test", "test@test.ru");
        when(requestService.addRequest(any(), anyInt()))
                .thenReturn(ItemRequestMapper.toModel(dto, user));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getRequests() throws Exception {
        CreateItemRequestDto requestDto = new CreateItemRequestDto();
        requestDto.setDescription("test");
        User user = new User(1, "test", "test@test.ru");
        ItemRequest itemRequest = ItemRequestMapper.toModel(requestDto, user);
        itemRequest.setItems(Collections.emptyList());
        when(requestService.getRequests(anyInt()))
                .thenReturn(List.of(ItemRequestMapper.toWithItemsDto(itemRequest)));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestsPageable() throws Exception {
        CreateItemRequestDto requestDto = new CreateItemRequestDto();
        requestDto.setDescription("test");
        User user = new User(1, "test", "test@test.ru");
        ItemRequest itemRequest = ItemRequestMapper.toModel(requestDto, user);
        itemRequest.setItems(Collections.emptyList());
        when(requestService.getRequests(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(ItemRequestMapper.toWithItemsDto(itemRequest)));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestById() throws Exception {
        CreateItemRequestDto requestDto = new CreateItemRequestDto();
        requestDto.setDescription("test");
        User user = new User(1, "test", "test@test.ru");
        ItemRequest itemRequest = ItemRequestMapper.toModel(requestDto, user);
        itemRequest.setItems(Collections.emptyList());
        when(requestService.getRequestById(anyInt(), anyInt()))
                .thenReturn(ItemRequestMapper.toWithItemsDto(itemRequest));

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}