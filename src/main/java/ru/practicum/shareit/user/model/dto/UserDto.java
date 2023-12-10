package ru.practicum.shareit.user.model.dto;

import lombok.Data;
import ru.practicum.shareit.utils.Marker;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnCreate.class)
    @Column(unique = true)
    private String email;

    @NotBlank(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnCreate.class)
    private String name;
}
