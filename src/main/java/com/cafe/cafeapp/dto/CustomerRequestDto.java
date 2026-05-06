package com.cafe.cafeapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequestDto {
    @Schema(description = "Имя пользователя", example = "Иван")
    @NotBlank(message = "Имя не должно быть пустым")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Иванов")
    @NotBlank(message = "Фамилия не должна быть пустой")
    private String lastName;

    @Schema(description = "Отчество пользователя", example = "Иванович")
    @NotBlank(message = "Отчество не можзет быть пустым")
    private String middleName;

    @Schema(description = "Телефон пользователя", example = "+375298057123")
    @Size(min = 13, max = 13, message = "Телефон должен бфть в формате +XXXXXXXXXXXX")
    @Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Некорректный формат телефона")
    private String phoneNumber;
    private String imageUrl;
    private String email;
    private String password;
}
