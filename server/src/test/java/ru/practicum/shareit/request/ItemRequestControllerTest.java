package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestInput;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    private MockMvc mockMvc;
    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new ItemRequestController(itemRequestService)).build();
    }

    @Test
    void createReques() throws Exception {
        ItemRequestInput input = new ItemRequestInput();
        Mockito.when(itemRequestService.createRequest(Mockito.any(ItemRequestInput.class), Mockito.anyLong())).thenReturn(new ItemRequestDto());

        var res = itemRequestController.createRequest(input, 1L);

        assertEquals(new ItemRequestDto(), res);
    }

    @Test
    void findRequestsById() {
        Mockito.when(itemRequestService.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new ItemRequestDto());

        var res = itemRequestController.getById(1L, 1L);

        assertEquals(new ItemRequestDto(), res);
    }

    @Test
    void getAllRequests() {
        List<ItemRequestDto> expectedRequests = List.of(new ItemRequestDto());
        Mockito.when(itemRequestService.findAllRequests(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong())).thenReturn(expectedRequests);

        var res = itemRequestController.getAllRequests(1, 1, 1L);

        assertEquals(expectedRequests, res);
    }

    @Test
    void getById() {
        ItemRequestDto expectedRequest = new ItemRequestDto();
        Mockito.when(itemRequestService.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(expectedRequest);

        var res = itemRequestController.getById(1L, 1L);

        assertEquals(expectedRequest, res);
    }
}