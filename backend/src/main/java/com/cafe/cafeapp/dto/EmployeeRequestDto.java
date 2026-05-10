package com.cafe.cafeapp.dto;

import com.cafe.cafeapp.enums.EmployeeRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestDto {
    @Schema(description = "Имя работника", example = "Иван")
    @NotBlank(message = "Имя не должно быть пустым")
    private String firstName;

    @Schema(description = "Фамилия работника", example = "Иванов")
    @NotBlank(message = "Фамилия не должна быть пустой")
    private String lastName;

    @Schema(description = "Отчество работника", example = "Иванович")
    @NotBlank(message = "Отчество не можзет быть пустым")
    private String middleName;

    @Schema(description = "Роль работника", example = "BARISTA")
    private EmployeeRole role;
    private String imageUrl;
    private String email;
    private String password;
}
