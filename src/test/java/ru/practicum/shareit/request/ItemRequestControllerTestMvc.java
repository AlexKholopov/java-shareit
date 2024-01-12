package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestInput;
import ru.practicum.shareit.request.service.ItemRequestService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTestMvc {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void createRequest() {
        ItemRequestInput input = new ItemRequestInput();
        var dto = new ItemRequestDto();
        dto.setDescription("description");
        input.setDescription("description");
        Mockito.when(itemRequestService.createRequest(input, 1L)).thenReturn(dto);

        var res = mockMvc.perform(MockMvcRequestBuilders.post("/requests").header("X-Sharer-User-Id", "1")
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
    void findRequestsById() {
        long requestId = 0L;
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{id}", requestId).header("X-Sharer-User-Id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemRequestService).getById(1, requestId);
    }

    @SneakyThrows
    @Test
    void getAllRequests() {
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all?from=1&size=2", 1L).header("X-Sharer-User-Id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemRequestService).findAllRequests(1, 2, 1L);
    }

    @SneakyThrows
    @Test
    void getById() {
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{id}", 2L).header("X-Sharer-User-Id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(itemRequestService).getById(1L, 2L);
    }
}