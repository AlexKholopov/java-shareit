package ru.practicum.shareit.item.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private long id;
    private String authorName;
    @NotBlank
    private String text;
    private LocalDateTime created;
}
