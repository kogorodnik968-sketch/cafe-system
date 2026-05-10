package com.cafe.cafeapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    @Schema(description = "ID товара", example = "2")
    private Long id;

    @Schema(description = "Название товара", example = "Американо")
    @NotBlank(message = "Название категории не должно быть пустым")
    private String name;
}
