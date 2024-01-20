package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.dto.BookingForItem;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class DtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> jsonUser;
    @Autowired
    private JacksonTester<ItemDto> jsonItem;


    @Test
    void testUserDto() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Name");
        userDto.setEmail("email@email.com");
        userDto.setId(1L);
        JsonContent<UserDto> result = jsonUser.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("email@email.com");
    }

    @Test
    void testItemDto() throws IOException {
        User booker = new User();
        booker.setId(1L);
        BookingForItem prevBooking = new BookingForItem(1L, 1L);
        BookingForItem nextBooking = new BookingForItem(1L, 1L);
        ItemDto itemDto = new ItemDto();
        itemDto.setLastBooking(prevBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setName("Отвертка");
        itemDto.setRequestId(1L);
        itemDto.setDescription("Шикарная отвертка");

        JsonContent<ItemDto> result = jsonItem.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Отвертка");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Шикарная отвертка");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(1);
    }
}