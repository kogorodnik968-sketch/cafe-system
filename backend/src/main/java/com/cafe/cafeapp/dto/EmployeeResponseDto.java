package com.cafe.cafeapp.dto;

import com.cafe.cafeapp.enums.EmployeeRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDto {
    @Schema(description = "Id работника", example = "3")
    private Long id;
    @Schema(description = "Имя работника", example = "Иван")
    private String firstName;
    @Schema(description = "Фамилия работника", example = "Иванов")
    private String lastName;
    @Schema(description = "Отчество работника", example = "Иванович")
    private String middleName;
    @Schema(description = "Роль работника", example = "BARISTA")
    private EmployeeRole role;
    private String imageUrl;
    private String email;
    private String password;

}
