package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemBookingCommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    void saveNewItem() throws Exception {
        final CreateItemDto itemDto = new CreateItemDto("test", "testtest", true, 0, null);
        Item model = ItemMapper.toModel(itemDto, null, null);
        model.setId(1);
        when(itemService.createItem(any()))
                .thenReturn(model);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(model.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void saveNewItemNoHeader() throws Exception {
        final CreateItemDto itemDto = new CreateItemDto("test", "testtest", true, 0, null);
        when(itemService.createItem(any()))
                .thenReturn(ItemMapper.toModel(itemDto, null, null));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists());
    }

    @Test
    void patchItem() throws Exception {
        final CreateItemDto itemDto = new CreateItemDto("test", "testtest", true, 0, null);
        Item model = ItemMapper.toModel(itemDto, null, null);
        model.setId(1);
        when(itemService.editItem(any()))
                .thenReturn(model);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(model.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void getItem() throws Exception {
        final CreateItemDto itemDto = new CreateItemDto("test", "testtest", true, 0, null);
        Item item = ItemMapper.toModel(itemDto, null, null);
        item.setId(1);
        ItemBookingCommentDto model = ItemMapper.toItemBookingCommentDto(item, null, null, Collections.emptyList());
        when(itemService.getItemBookingById(anyInt(), anyInt()))
                .thenReturn(model);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(model.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(model.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(model.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(model.getAvailable()));
    }

    @Test
    void getAllItems() throws Exception {
        final CreateItemDto itemDto = new CreateItemDto("test", "testtest", true, 0, null);
        ItemBookingDto model = ItemMapper.toItemBookingDto(ItemMapper.toModel(itemDto, null, null), null, null);
        when(itemService.getAllByOwnerId(anyInt(), anyInt(), anyInt())).thenReturn(List.of(
                model
        ));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(model.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void searchAllItems() throws Exception {
        final CreateItemDto itemDto = new CreateItemDto("test", "testtest", true, 0, null);
        Item model = ItemMapper.toModel(itemDto, null, null);
        model.setId(1);
        when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(model));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .param("text", "searchItem")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(model.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void addComment() throws Exception {
        final Comment comment = new Comment();
        comment.setText("test");
        Item item = new Item();
        item.setId(1);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(new User(1, "test", "test@test.com"));
        comment.setId(1);
        when(itemService.addComment(any(), anyInt(), anyInt()))
                .thenReturn(comment);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(comment.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text").value(comment.getText()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorId").value(comment.getAuthor().getId()));
    }
}