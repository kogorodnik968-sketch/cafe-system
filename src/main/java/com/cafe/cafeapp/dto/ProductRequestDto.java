package com.cafe.cafeapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDto {
    @Schema(description = "Название товара", example = "Американо")
    @NotBlank(message = "Нзавание товара не может быть пустым")
    private String name;
    @Schema(description = "Цена товара", example = "7.00")
    @Positive(message = "Цена не может быть отрицательной")
    private BigDecimal price;
    @Schema(description = "Id категории", example = "2")
    private Long categoryId;
    @Schema(description = "Список ингридиентов", example = "Молоко, Кофейные зёрна")
    private Set<Long> ingredientsId;
    @Schema(description = "Id тега", example = "2")
    private Long tagId;
}
