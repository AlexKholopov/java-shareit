package ru.practicum.shareit.request.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ItemRequestInput {
    @NotBlank
    @Size(max = 200)
    private String description;
}
