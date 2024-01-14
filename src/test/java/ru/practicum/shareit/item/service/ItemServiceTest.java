package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingForItem;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.LockedException;
import ru.practicum.shareit.exceptions.NoAuthorizationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.CommentIncome;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemIncome;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemService itemService;
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void createItemWithoutRequestSuccess() {
        User owner = new User();
        owner.setId(1L);
        ItemIncome itemIncome = new ItemIncome();
        ItemDto itemResult = new ItemDto();
        itemResult.setComments(List.of());

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        var res = itemService.createItem(itemIncome, 1L);

        assertEquals(itemResult, res);
    }

    @Test
    void createItemWithRequestSuccess() {
        User owner = new User();
        owner.setId(1L);
        ItemIncome itemIncome = new ItemIncome();
        itemIncome.setRequestId(1L);
        ItemDto itemResult = new ItemDto();
        itemResult.setComments(List.of());

        Mockito.when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(new ItemRequest()));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        var res = itemService.createItem(itemIncome, 1L);

        assertEquals(itemResult, res);
    }

    @Test
    void createItemFailNoUser() {

        ItemIncome itemIncome = new ItemIncome();

        var res = assertThrows(NotFoundException.class, () -> itemService.createItem(itemIncome, 1L));

        assertEquals("No such owner was found", res.getMessage());
    }

    @Test
    void updateItemSuccess() {
        User owner = new User();
        owner.setId(1L);
        ItemIncome itemIncome = new ItemIncome();
        itemIncome.setRequestId(1L);
        itemIncome.setId(1L);
        Item item = new Item();
        item.setOwner(owner);

        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        var res = itemService.updateItem(itemIncome, 1L);

        assertEquals(itemMapper.toDTO(item, List.of()), res);
    }

    @Test
    void updateItemFailAuthorization() {
        User owner = new User();
        owner.setId(2L);
        ItemIncome itemIncome = new ItemIncome();
        itemIncome.setRequestId(1L);
        itemIncome.setId(1L);
        Item item = new Item();
        item.setOwner(owner);

        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        var res = assertThrows(NoAuthorizationException.class,
                () -> itemService.updateItem(itemIncome, 1L));

        assertEquals("You do not have authorization to update the object", res.getMessage());
    }

    @Test
    void updateItemFailNoUser() {
        ItemIncome itemIncome = new ItemIncome();

        var res = assertThrows(NotFoundException.class, () -> itemService.updateItem(itemIncome, 1L));

        assertEquals("No such item was found", res.getMessage());
    }

    @Test
    void updateItemSuccessAllFields() {
        User owner = new User();
        owner.setId(1L);
        ItemIncome itemIncome = new ItemIncome();
        itemIncome.setRequestId(1L);
        itemIncome.setId(1L);
        itemIncome.setDescription("Description");
        itemIncome.setName("Name");
        itemIncome.setAvailable(true);
        Item item = new Item();
        item.setOwner(owner);

        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        var res = itemService.updateItem(itemIncome, 1L);

        assertEquals(itemMapper.toDTO(item, List.of()), res);
    }

    @Test
    void getUserItemsSuccessWithBookings() {
        User owner = new User();
        owner.setId(1L);
        User booker = new User();
        owner.setId(0L);
        ItemIncome itemIncome = new ItemIncome();
        itemIncome.setRequestId(1L);
        itemIncome.setId(1L);
        Item item = new Item();
        item.setOwner(owner);
        item.setId(1L);
        var prevStart = LocalDateTime.now().minusMinutes(10);
        var prevEnd = LocalDateTime.now().minusMinutes(1);
        var nextStart = LocalDateTime.now().plusMinutes(1);
        var nextEnd = LocalDateTime.now().plusMinutes(10);
        Booking prevBooking = new Booking();
        prevBooking.setId(1L);
        prevBooking.setBooker(booker);
        prevBooking.setStart(prevStart);
        prevBooking.setEnd(prevEnd);
        prevBooking.setItem(item);
        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setBooker(booker);
        nextBooking.setStart(nextStart);
        nextBooking.setEnd(nextEnd);
        nextBooking.setItem(item);

        Mockito.when(bookingRepository.findByItemInAndStartLessThanEqualAndStatus(Mockito.anyCollection(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Status.class)))
                .thenReturn(List.of(prevBooking));
        Mockito.when(bookingRepository.findByItemInAndStartAfterAndStatus(Mockito.anyCollection(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Status.class)))
                .thenReturn(List.of(nextBooking));
        Mockito.when(itemRepository.findByOwner(Mockito.any(User.class), Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(item)));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        var res = itemService.getUserItems(0, 2, 1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setLastBooking(new BookingForItem(1L, 0L));
        itemDto.setNextBooking(new BookingForItem(2L, 0L));
        itemDto.setComments(List.of());
        assertEquals(List.of(itemDto), res);
    }

    @Test
    void getUserItemsSuccessWithoutBookings() {
        User owner = new User();
        owner.setId(1L);
        ItemIncome itemIncome = new ItemIncome();
        itemIncome.setRequestId(1L);
        itemIncome.setId(1L);
        Item item = new Item();
        item.setOwner(owner);
        item.setId(1L);


        Mockito.when(itemRepository.findByOwner(Mockito.any(User.class), Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(item)));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        var res = itemService.getUserItems(0, 2, 1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setComments(List.of());
        assertEquals(List.of(itemDto), res);
    }

    @Test
    void getUserItemsFailNoUser() {
        var res = assertThrows(ConflictException.class, () -> itemService.getUserItems(0, 1, 1L));

        assertEquals("No such user was found", res.getMessage());
    }

    @Test
    void searchItemsSuccess() {
        User owner = new User();
        owner.setId(1L);
        ItemIncome itemIncome = new ItemIncome();
        itemIncome.setRequestId(1L);
        itemIncome.setId(1L);
        Item item = new Item();
        item.setOwner(owner);
        item.setId(1L);
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setId(1L);
        comment.setCreated(LocalDateTime.now());

        Mockito.when(itemRepository.findByText(Mockito.anyString(), Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(item)));
        Mockito.when(commentRepository.findByItemIn(Mockito.anyList())).thenReturn(List.of(comment));
        var res = itemService.searchItems(0, 2, "text");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setComments(List.of(commentMapper.toDTO(comment)));
        assertEquals(List.of(itemDto), res);
    }

    @Test
    void searchItemsFailEmptyQuery() {
        var res = itemService.searchItems(0, 2, "");
        assertEquals(List.of(), res);
    }

    @Test
    void getItemByIdSuccessWithComments() {
        User owner = new User();
        owner.setId(1L);
        ItemIncome itemIncome = new ItemIncome();
        itemIncome.setRequestId(1L);
        itemIncome.setId(1L);
        Item item = new Item();
        item.setOwner(owner);
        item.setId(1L);
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setId(1L);
        comment.setCreated(LocalDateTime.now());

        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findByItem(item)).thenReturn(List.of(comment));
        var res = itemService.getItemById(1L, 2);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setComments(List.of(commentMapper.toDTO(comment)));
        assertEquals(itemDto, res);
    }

    @Test
    void getItemByIdSuccessWithBookings() {
        User owner = new User();
        owner.setId(1L);
        User booker = new User();
        booker.setId(0L);
        Item item = new Item();
        item.setOwner(owner);
        item.setId(1L);
        Item item1 = new Item();
        item.setOwner(owner);
        item.setId(1L);
        var prevStart = LocalDateTime.now().minusMinutes(10);
        var prevEnd = LocalDateTime.now().minusMinutes(1);
        var nextStart = LocalDateTime.now().plusMinutes(1);
        var nextEnd = LocalDateTime.now().plusMinutes(10);
        Booking prevBooking = new Booking();
        prevBooking.setId(1L);
        prevBooking.setBooker(booker);
        prevBooking.setStart(prevStart);
        prevBooking.setEnd(prevEnd);
        prevBooking.setItem(item);
        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setBooker(booker);
        nextBooking.setStart(nextStart);
        nextBooking.setEnd(nextEnd);
        nextBooking.setItem(item);


        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findByItem(item)).thenReturn(List.of());
        Mockito.when(bookingRepository.findByItemAndStartLessThanEqualAndStatus(Mockito.any(Item.class),
                Mockito.any(LocalDateTime.class),
                Mockito.any(Status.class))).thenReturn(List.of(prevBooking));
        Mockito.when(bookingRepository.findByItemAndStartAfterAndStatus(Mockito.any(Item.class),
                Mockito.any(LocalDateTime.class),
                Mockito.any(Status.class))).thenReturn(List.of(nextBooking));
        var res = itemService.getItemById(1L, 1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setComments(List.of());
        itemDto.setLastBooking(new BookingForItem(1L, 0L));
        itemDto.setNextBooking(new BookingForItem(2L, 0L));
        assertEquals(itemDto, res);
        item.equals(item1);
    }

    @Test
    void getItemByIdSuccessWithoutBookings() {
        User owner = new User();
        owner.setId(1L);
        User booker = new User();
        booker.setId(0L);
        Item item = new Item();
        item.setOwner(owner);
        item.setId(1L);
        Item item1 = new Item();
        item.setOwner(owner);
        item.setId(1L);
        var prevStart = LocalDateTime.now().minusMinutes(10);
        var prevEnd = LocalDateTime.now().minusMinutes(1);
        var nextStart = LocalDateTime.now().plusMinutes(1);
        var nextEnd = LocalDateTime.now().plusMinutes(10);
        Booking prevBooking = new Booking();
        prevBooking.setId(1L);
        prevBooking.setBooker(booker);
        prevBooking.setStart(prevStart);
        prevBooking.setEnd(prevEnd);
        prevBooking.setItem(item);
        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setBooker(booker);
        nextBooking.setStart(nextStart);
        nextBooking.setEnd(nextEnd);
        nextBooking.setItem(item);


        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findByItem(item)).thenReturn(List.of());
        Mockito.when(bookingRepository.findByItemAndStartLessThanEqualAndStatus(Mockito.any(Item.class),
                Mockito.any(LocalDateTime.class),
                Mockito.any(Status.class))).thenReturn(List.of());
        Mockito.when(bookingRepository.findByItemAndStartAfterAndStatus(Mockito.any(Item.class),
                Mockito.any(LocalDateTime.class),
                Mockito.any(Status.class))).thenReturn(List.of());
        var res = itemService.getItemById(1L, 1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setComments(List.of());
        assertEquals(itemDto, res);
        item.equals(item1);
    }

    @Test
    void getItemByIdFailNoItem() {
        var res = assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 2));
        assertEquals("No such item was found", res.getMessage());
    }

    @Test
    void addCommentSuccess() {
        User user = new User();
        user.setId(1L);
        User owner = new User();
        owner.setId(0L);
        Item item = new Item();
        item.setOwner(owner);
        item.setId(1L);
        CommentIncome commentIncome = new CommentIncome();
        commentIncome.setText("Sample text");
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setItem(item);
        comment.setText("Sample text");

        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.existsAcceptedByBookerAndItemAndTime(Mockito.any(User.class),
                        Mockito.any(Item.class),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(true);
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);
        var res = itemService.addComment(1L, 1L, commentIncome);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Sample text");
        assertEquals(commentDto, res);
    }

    @Test
    void addCommentFailNoBookings() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(user);
        item.setId(1L);


        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.existsAcceptedByBookerAndItemAndTime(Mockito.any(User.class),
                        Mockito.any(Item.class),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(false);

        var res = assertThrows(LockedException.class, () -> itemService.addComment(1L, 1L, new CommentIncome()));
        assertEquals("No bookings by that user was found", res.getMessage());
    }

    @Test
    void addCommentFailCommentByOwner() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(user);
        item.setId(1L);


        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.existsAcceptedByBookerAndItemAndTime(Mockito.any(User.class),
                        Mockito.any(Item.class),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(true);

        var res = assertThrows(LockedException.class, () -> itemService.addComment(1L, 1L, new CommentIncome()));
        assertEquals("You can't comment your item", res.getMessage());
    }

    @Test
    void addCommentFailNoItem() {
        CommentIncome commentIncome = new CommentIncome();
        Item item = new Item();
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        var res = assertThrows(NotFoundException.class, () -> itemService.addComment(1L, 1L, commentIncome));

        assertEquals("No such user was found", res.getMessage());
    }

    @Test
    void addCommentFailNoUser() {
        CommentIncome commentIncome = new CommentIncome();
        var res = assertThrows(NotFoundException.class, () -> itemService.addComment(1L, 1L, commentIncome));

        assertEquals("No such item was found", res.getMessage());
    }

    @Test
    void mappingTest() {
        User user = new User();
        user.setName("Name");
        Item item = new Item();
        var created = LocalDateTime.now();
        Comment comment = new Comment();
        comment.setText("text");
        comment.setCreated(created);
        comment.setId(1L);
        comment.setUser(user);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("text");
        commentDto.setId(1L);
        commentDto.setAuthorName("Name");
        commentDto.setCreated(created);

        var maybeNull = commentMapper.fromDTO(null, null, null);
        assertNull(maybeNull);

        var d = commentMapper.fromDTO(commentDto, null, null);
        comment.setUser(null);
        assertEquals(comment, d);

        d = commentMapper.fromDTO(null, user, null);
        comment.setUser(user);
        comment.setCreated(null);
        comment.setText(null);
        comment.setId(1L);
        d.setId(1L);
        assertEquals(comment, d);

        d = commentMapper.fromDTO(null, null, item);
        comment.setUser(null);
        comment.setItem(item);
        d.setId(1L);
        assertEquals(comment, d);

        d = commentMapper.fromIncome(new CommentIncome(), null, null, null);
        comment.setUser(null);
        comment.setItem(null);
        comment.setId(1L);
        d.setId(1L);
        assertEquals(comment, d);

        d = commentMapper.fromIncome(null, user, null, null);
        comment.setUser(user);
        comment.setId(1L);
        d.setId(1L);
        assertEquals(comment, d);

        d = commentMapper.fromIncome(null, null, item, null);
        comment.setUser(null);
        comment.setItem(item);
        comment.setId(1L);
        d.setId(1L);
        d = commentMapper.fromIncome(null, null, null, 1L);
        assertEquals(comment, d);

        maybeNull = commentMapper.fromIncome(null, null, null, null);
        assertNull(maybeNull);

        Item expItem = new Item();
        var i = itemMapper.toModel(new ItemIncome(), null);
        expItem.setId(0L);
        assertEquals(expItem, i);

        i = itemMapper.toModel(null, null);
        assertNull(i);

        i = itemMapper.toModel(null, null, null);
        assertNull(i);

        i = itemMapper.toModel(new ItemIncome(), null, null);
        assertEquals(expItem, i);

        i = itemMapper.toModel(null, null, new ItemRequest());
        expItem.setRequest(new ItemRequest());
        i.setId(0L);
        assertEquals(expItem, i);


        ItemDto id = itemMapper.toDTO(null, null);
        assertNull(id);

        id = itemMapper.toDTO(item, null);
        assertEquals(new ItemDto(), id);

        id = itemMapper.toDTO(null, List.of());
        ItemDto itemDto = new ItemDto();
        itemDto.setComments(List.of());
        assertEquals(itemDto, id);

        var com = commentMapper.toDTO(null);
        assertNull(com);

        var resComment = commentMapper.fromDTO(commentDto, user, new Item());
        assertEquals(comment, resComment);
    }
}