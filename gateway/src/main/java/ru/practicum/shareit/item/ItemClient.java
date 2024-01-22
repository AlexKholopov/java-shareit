package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentIncome;
import ru.practicum.shareit.item.dto.ItemIncome;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> createItem(ItemIncome itemIncome, long owner) {
        return post("", owner, itemIncome);
    }

    public ResponseEntity<Object> updateItem(ItemIncome itemIncome, long owner, long itemId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return patch("/{itemId}", owner, parameters, itemIncome);
    }

    public ResponseEntity<Object> searchItems(int from, int size, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?from={from}&size={size}&text={text}", null, parameters);
    }

    public ResponseEntity<Object> getUserItems(int from, int size, long owner) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", owner, parameters);
    }

    public ResponseEntity<Object> getItemById(long itemId, long userID) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return get("/{itemId}", userID, parameters);
    }

    public ResponseEntity<Object> addComment(long user, long itemId, CommentIncome commentIncome) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return post("/{itemId}/comment", user, parameters, commentIncome);
    }
}
