package com.cafe.cafeapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {
    @Schema(description = "Id товара", example = "12")
    private Long id;
    @Schema(description = "Название товара", example = "Американо")
    private String name;
    @Schema(description = "Цена товара", example = "7.00")
    private BigDecimal price;
    @Schema(description = "Название категории", example = "Кофе")
    private String categoryName;
    @Schema(description = "Список ингридиентов", example = "Молоко, Кофейные зёрна")
    private List<String> ingredientsName;
    @Schema(description = "Название тега", example = "Стандарт")
    private String tagName;
    private String imageUrl;
}
