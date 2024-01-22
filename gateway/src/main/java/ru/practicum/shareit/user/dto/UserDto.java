package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.utils.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @Email(groups = {Marker.OnUpdate.class, Marker.OnCreate.class})
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 40, groups = {Marker.OnUpdate.class, Marker.OnCreate.class})
    private String email;

    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 40, groups = {Marker.OnUpdate.class, Marker.OnCreate.class})
    private String name;
}
