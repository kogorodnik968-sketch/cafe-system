package com.cafe.cafeapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDto {
    @Schema(description = "Id ингридиента", example = "2")
    private Long id;
    @Schema(description = "Название ингридиента", example = "Молоко")
    @NotBlank(message = "Название ингридиента не может быть пустым")
    private String name;
}
