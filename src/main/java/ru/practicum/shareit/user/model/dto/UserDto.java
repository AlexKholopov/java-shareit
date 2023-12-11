package ru.practicum.shareit.user.model.dto;

import lombok.Data;
import ru.practicum.shareit.utils.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserDto {
    private Long id;

    @Email(groups = Marker.OnCreate.class)
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 40)
    private String email;

    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 40)
    private String name;
}
