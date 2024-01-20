package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestInput;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemRequestService itemRequestService;
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @Spy
    private ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);

    @Test
    void createRequestSuccess() {
        User user = new User();
        ItemRequestInput itemRequestInput = new ItemRequestInput();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        itemRequestService.createRequest(itemRequestInput, 1L);
        verify(itemRequestRepository).save(Mockito.any(ItemRequest.class));
    }

    @Test
    void findUserRequestsSuccessWithItems() {
        var created = LocalDateTime.now();
        User user = new User();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(created);
        itemRequest.setId(1L);
        Item item = new Item();
        item.setRequest(itemRequest);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findByOwner(user)).thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findByRequestIn(List.of(itemRequest))).thenReturn(List.of(item));
        var res = itemRequestService.findUserRequests(1L);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setItems(List.of(itemMapper.toRequest(item)));
        itemRequestDto.setCreated(created);
        itemRequestDto.setId(1L);
        assertEquals(List.of(itemRequestDto), res);
    }

    @Test
    void findUserRequestsSuccessWithoutItems() {
        var created = LocalDateTime.now();
        User user = new User();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(created);
        itemRequest.setId(1L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findByOwner(user)).thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findByRequestIn(List.of(itemRequest))).thenReturn(List.of());
        var res = itemRequestService.findUserRequests(1L);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setItems(List.of());
        itemRequestDto.setCreated(created);
        itemRequestDto.setId(1L);
        assertEquals(List.of(itemRequestDto), res);
    }

    @Test
    void findAllRequestsSuccessWithItems() {
        var created = LocalDateTime.now();
        User user = new User();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(created);
        itemRequest.setId(1L);
        Item item = new Item();
        item.setRequest(itemRequest);

        Mockito.when(userRepository.findById(0L)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findByOwnerNot(Mockito.any(User.class),
                        Mockito.any(PageRequest.class)))
                .thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findByRequestIn(List.of(itemRequest))).thenReturn(List.of(item));
        var res = itemRequestService.findAllRequests(0, 2, 0L);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setItems(List.of(itemMapper.toRequest(item)));
        itemRequestDto.setCreated(created);
        itemRequestDto.setId(1L);
        assertEquals(List.of(itemRequestDto), res);
    }

    @Test
    void findAllRequestsSuccessWithoutItems() {
        var created = LocalDateTime.now();
        User user = new User();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(created);
        itemRequest.setId(1L);
        Item item = new Item();
        item.setRequest(itemRequest);

        Mockito.when(userRepository.findById(0L)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findByOwnerNot(Mockito.any(User.class),
                        Mockito.any(PageRequest.class)))
                .thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findByRequestIn(List.of(itemRequest))).thenReturn(List.of());
        var res = itemRequestService.findAllRequests(0, 2, 0L);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setItems(List.of());
        itemRequestDto.setCreated(created);
        itemRequestDto.setId(1L);
        assertEquals(List.of(itemRequestDto), res);
    }

    @Test
    void getByIdSuccess() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        Item item = new Item();
        item.setRequest(itemRequest);

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findByRequestIn(Mockito.anyList())).thenReturn(List.of(item));

        var res = itemRequestService.getById(1L, 1L);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setItems(List.of(itemMapper.toRequest(item)));
        itemRequestDto.setId(1L);
        assertEquals(itemRequestDto, res);
    }
}