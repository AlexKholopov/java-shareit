package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class TestItemRequestRepository {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private PageRequest pageRequest = PageRequest.of(0, 3);
    private ItemRequest itemRequest;
    private User owner;
    private User user;
    private Item item;

    @BeforeEach
    void prepare() {
        owner = new User();
        owner.setName("Name");
        owner.setEmail("email@email.com");

        user = new User();
        user.setName("Name");
        user.setEmail("email2@email.com");

        item = new Item();
        item.setOwner(owner);
        item.setName("Отвертка");
        item.setDescription("Крутая отвертка");
        item.setAvailable(true);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Description");
        itemRequest.setOwner(owner);
        itemRequest.setCreated(LocalDateTime.now());

        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        itemRequestRepository.save(itemRequest);
    }

    @Test
    public void testFindByOwnerNot() {
        var expectedRequests = List.of(itemRequest);
        var res = itemRequestRepository.findByOwnerNot(user, pageRequest);

        assertEquals(expectedRequests, res);
    }

    @Test
    public void testFindByOwner() {
        var expectedRequests = List.of(itemRequest);
        var res = itemRequestRepository.findByOwner(owner);

        assertEquals(expectedRequests, res);
    }
}
