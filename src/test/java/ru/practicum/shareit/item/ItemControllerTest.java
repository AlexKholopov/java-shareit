package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.CommentIncome;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemIncome;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;

    @Test
    void createItem() {
        ItemIncome itemIncome = new ItemIncome();
        Mockito.when(itemService.createItem(itemIncome, 1L)).thenReturn(new ItemDto());

        ItemDto item = itemController.createItem(itemIncome, 1L);

        assertEquals(new ItemDto(), item);
    }

    @Test
    void updateItem() {
        ItemIncome itemIncome = new ItemIncome();
        Mockito.when(itemService.updateItem(itemIncome, 1L)).thenReturn(new ItemDto());

        ItemDto item = itemController.updateItem(itemIncome, 1L, 1L);

        assertEquals(new ItemDto(), item);
    }

    @Test
    void getUsersItems() {
        List<ItemDto> expectedItems = List.of(new ItemDto());
        Mockito.when(itemService.getUserItems(1, 2, 1L)).thenReturn(expectedItems);

        var response = itemController.getUsersItems(1, 2, 1L);

        assertEquals(expectedItems, response);
    }

    @Test
    void searchItems() {
        List<ItemDto> expectedItems = List.of(new ItemDto());
        Mockito.when(itemService.searchItems(1, 2, "text")).thenReturn(expectedItems);

        var response = itemController.searchItems(1, 2, "text");

        assertEquals(expectedItems, response);
    }

    @Test
    void getItemById() {
        ItemDto expectedItem = new ItemDto();
        Mockito.when(itemService.getItemById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(expectedItem);

        var response = itemController.getItemById(1L, 1L);

        assertEquals(expectedItem, response);
    }

    @Test
    void addComment() {
        CommentIncome commentIncome = new CommentIncome();
        Mockito.when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentIncome.class))).thenReturn(new CommentDto());

        var response = itemController.addComment(1L, 1L, commentIncome);

        assertEquals(new CommentDto(), response);
    }
}