package com.cafe.cafeapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDto {
    @Schema(description = "Id тега", example = "2")
    private Long id;
    @Schema(description = "Название тега", example = "Стандарт")
    @NotBlank(message = "Название тега не может быть пустым")
    private String name;
}
