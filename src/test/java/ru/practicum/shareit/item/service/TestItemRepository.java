package ru.practicum.shareit.item.service;

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
public class TestItemRepository {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User owner;
    private Item item;

    @BeforeEach
    void prepare() {
        owner = new User();
        item = new Item();
        owner.setName("Name");
        owner.setEmail("email@email.com");

        item.setOwner(owner);
        item.setName("Отвертка");
        item.setDescription("Крутая отвертка");
        item.setAvailable(true);
    }

    @Test
    void testFindByText() {
        var expectesList = List.of(item);

        userRepository.save(owner);
        repository.save(item);

        PageRequest pageRequest = PageRequest.of(0, 3);
        var res = repository.findByText("отв", pageRequest);

        assertEquals(expectesList, res);
    }

    @Test
    void testFindByOwner() {
        var expectesList = List.of(item);

        userRepository.save(owner);
        repository.save(item);

        PageRequest pageRequest = PageRequest.of(0, 3);
        var res = repository.findByOwner(owner, pageRequest);

        assertEquals(expectesList, res);
    }

    @Test
    void testFindByRequestIn() {
        User user = new User();
        user.setName("Name");
        user.setEmail("email2@email.com");
        userRepository.save(user);
        userRepository.save(owner);
        ItemRequest request = new ItemRequest();
        request.setCreated(LocalDateTime.now());
        request.setOwner(user);
        request.setDescription("Description");
        itemRequestRepository.save(request);
        item.setRequest(request);
        repository.save(item);

        var res = repository.findByRequestIn(List.of(request));

        assertEquals(List.of(item), res);
    }
}
