package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:shareit", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @MockBean
    private final ItemService service;
    @Autowired
    ObjectMapper mapper;

    @Test
    void createItemTest(@Autowired MockMvc mvc) throws Exception {
        ItemShort item = new ItemShort("name", "description", true, 0L);
        when(service.createItem(any(), any()))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId()), Long.class));
    }

    @Test
    void createItemCommentTest(@Autowired MockMvc mvc) throws Exception {
        ItemShort item = new ItemShort("name", "description", true, 0L);
        when(service.createItem(any(), any()))
                .thenReturn(item);

        CommentDto commentDto = new CommentDto("text", "authorName");
        when(service.createComment(any(), any(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", item.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", notNullValue()));
    }

    @Test
    void createItemThrowNotFoundExceptionTest(@Autowired MockMvc mvc) throws Exception {
        ItemShort item = new ItemShort("name", "description", true, 0L);
        when(service.createItem(any(), any()))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItemTest(@Autowired MockMvc mvc) throws Exception {
        ItemShort item = new ItemShort("name", "description", true, 0L);
        when(service.createItem(any(), any()))
                .thenReturn(item);

        Item itemUpdate = new Item("up", "updesc", false);
        when(service.updateItem(any(), any(), any()))
                .thenReturn(itemUpdate);

        mvc.perform(patch("/items/{itemId}", item.getId())
                        .content(mapper.writeValueAsString(itemUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemUpdate.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemUpdate.getName())))
                .andExpect(jsonPath("$.description", is(itemUpdate.getDescription())))
                .andExpect(jsonPath("$.available", is(itemUpdate.getAvailable())));
    }

    @Test
    void getItemTest(@Autowired MockMvc mvc) throws Exception {
        ItemShort item = new ItemShort("name", "description", true, 0L);
        when(service.createItem(any(), any()))
                .thenReturn(item);

        ItemDto item1 = new ItemDto("name", "description", true);
        when(service.getItem(any(),any()))
                .thenReturn(item1);

        mvc.perform(get("/items/{itemId}", item.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName())))
                .andExpect(jsonPath("$.description", is(item1.getDescription())))
                .andExpect(jsonPath("$.available", is(item1.getAvailable())));
    }

    @Test
    void getItemsTest(@Autowired MockMvc mvc) throws Exception {
        ItemShort item = new ItemShort("name", "description", true, 0L);
        when(service.createItem(any(), any()))
                .thenReturn(item);

        ItemDto itemDto = new ItemDto("name", "description", true);
        when(service.getItems(any(), any(), any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void getItemsSearchTest(@Autowired MockMvc mvc) throws Exception {
        ItemShort item = new ItemShort("name", "description", true, 0L);
        when(service.createItem(any(), any()))
                .thenReturn(item);

        Item item1 = ItemMapper.toItem(item);
        when(service.getItemSearch(any(), any(), any(), any()))
                .thenReturn(List.of(item1));

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item1.getName())))
                .andExpect(jsonPath("$[0].description", is(item1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item1.getAvailable())));
    }
}
