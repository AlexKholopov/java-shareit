package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemIncome;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemServiceJpaTest {


    private final EntityManager em;
    private final ItemService service;
    private final UserService userService;

    @Test
    void saveItemTest() {

        ItemIncome itemIncome = new ItemIncome();
        itemIncome.setName("Name");
        itemIncome.setAvailable(true);
        itemIncome.setDescription("Description");
        UserDto userDto = new UserDto();
        userDto.setEmail("email@email.com");
        userDto.setName("Name");
        var user = userService.createUser(userDto);
        service.createItem(itemIncome, user.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query
                .setParameter("name", itemIncome.getName())
                .getSingleResult();


        assertThat(item.getName(), equalTo(itemIncome.getName()));
        assertThat(item.getAvailable(), equalTo(itemIncome.getAvailable()));
    }

    @Test
    void getUsersItemsTest() {

        ItemIncome itemIncome = new ItemIncome();
        itemIncome.setName("Name");
        itemIncome.setAvailable(true);
        itemIncome.setDescription("Description");
        UserDto userDto = new UserDto();
        userDto.setEmail("email@email.com");
        userDto.setName("Name");
        var user = userService.createUser(userDto);
        var item = service.createItem(itemIncome, user.getId());
        var res = service.getUserItems(0, 1, user.getId());

        assertEquals(List.of(item), res);
    }
}
