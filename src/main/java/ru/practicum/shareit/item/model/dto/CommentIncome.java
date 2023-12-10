package ru.practicum.shareit.item.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CommentIncome {
    @NotNull
    @NotBlank
    @Size(max = 1000)
    private String text;
}
