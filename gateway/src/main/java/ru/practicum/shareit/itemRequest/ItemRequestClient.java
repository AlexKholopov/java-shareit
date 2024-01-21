package ru.practicum.shareit.itemRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.itemRequest.dto.ItemRequestInput;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> createRequest(ItemRequestInput itemRequestInput, long user) {
        return post("", user, itemRequestInput);
    }

    public ResponseEntity<Object> findUserRequests(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findAllRequests(int from, int size, long userId) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all??from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getById(long requestId, long userId) {
        Map<String, Object> parameters = Map.of(
                "id", requestId
        );
        return get("/{id}", userId, parameters);
    }
}
