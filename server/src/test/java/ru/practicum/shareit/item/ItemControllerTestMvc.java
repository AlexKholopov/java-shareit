package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.CommentIncome;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemIncome;
import ru.practicum.shareit.item.service.ItemService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(ItemController.class)
class ItemControllerTestMvc {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    @SneakyThrows
    @Test
    void createItemSuccess() {
        ItemIncome input = new ItemIncome();
        input.setDescription("description");
        input.setName("name");
        input.setAvailable(true);
        var dto = new ItemDto();
        dto.setDescription("description");
        Mockito.when(itemService.createItem(input, 1L)).thenReturn(dto);

        var res = mockMvc.perform(MockMvcRequestBuilders.post("/items").header("X-Sharer-User-Id", "1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dto), res);
    }

    /*
    @SneakyThrows
    @Test
    void createItemFailValidation() {
        ItemIncome input = new ItemIncome();
        input.setDescription("description");
        var dto = new ItemDto();
        dto.setDescription("description");
        Mockito.when(itemService.createItem(input, 1L)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items").header("X-Sharer-User-Id", "1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

     */

    @SneakyThrows
    @Test
    void updateItemTestSuccess() {
        ItemIncome input = new ItemIncome();
        input.setId(1L);
        input.setDescription("description");
        input.setName("name");
        input.setAvailable(true);
        var dto = new ItemDto();
        dto.setDescription("description");
        Mockito.when(itemService.updateItem(input, 1L)).thenReturn(dto);

        var res = mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", 1L).header("X-Sharer-User-Id", "1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dto), res);
    }

    @SneakyThrows
    @Test
    void getUsersItems() {
        mockMvc.perform(MockMvcRequestBuilders.get("/items?from=1&size=2").header("X-Sharer-User-Id", "3"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemService).getUserItems(1, 2, 3);
    }

    @SneakyThrows
    @Test
    void searchItems() {
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?from=1&size=2&text=search"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemService).searchItems(1, 2, "search");
    }

    @SneakyThrows
    @Test
    void getItemById() {
        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", 2L).header("X-Sharer-User-Id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemService).getItemById(2L, 1L);
    }

    @SneakyThrows
    @Test
    void addComment() {
        CommentIncome commentIncome = new CommentIncome();
        commentIncome.setText("text");
        CommentDto dto = new CommentDto();
        Mockito.when(itemService.addComment(1L, 2L, commentIncome)).thenReturn(dto);

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", 1L).header("X-Sharer-User-Id", "2")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentIncome)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dto), result);

    }
}