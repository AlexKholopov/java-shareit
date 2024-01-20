package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class TestCommentRepository {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    private Comment comment;
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

        LocalDateTime now = LocalDateTime.now();
        comment = new Comment();
        comment.setItem(item);
        comment.setUser(user);
        comment.setText("Text");
        comment.setCreated(now);
    }

    @Test
    void testFindByItem() {
        var expectesList = List.of(comment);

        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        commentRepository.save(comment);


        var res = commentRepository.findByItem(item);

        assertEquals(expectesList, res);
    }

    @Test
    void testFindItemIn() {
        var expectesList = List.of(comment);

        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        commentRepository.save(comment);


        var res = commentRepository.findByItemIn(List.of(item));

        assertEquals(expectesList, res);
    }
}
