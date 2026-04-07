package com.cafe.cafeapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseDto {
    @Schema(description = "Id пользователя", example = "3")
    private  Long id;
    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;
    @Schema(description = "Фамилия пользователя", example = "Иванов")
    private String lastName;
    @Schema(description = "Отчество пользователя", example = "Иванович")
    private String middleName;
    @Schema(description = "Телефон пользователя", example = "+375298057123")
    private String phoneNumber;
}
