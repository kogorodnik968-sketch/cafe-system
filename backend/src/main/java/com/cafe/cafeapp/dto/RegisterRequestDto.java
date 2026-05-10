package com.cafe.cafeapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {
    private String firstName;
    private String lastName;
    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    private String email;
    @Size(min = 6, message = "Пароль должен быть минимум 6 символов")
    @NotBlank(message = "Пароль обязателен")
    private String password;
    @Size(min = 13, max = 13, message = "Телефон должен бфть в формате +XXXXXXXXXXXX")
    @Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Некорректный формат телефона")
    private String phoneNumber;
}
