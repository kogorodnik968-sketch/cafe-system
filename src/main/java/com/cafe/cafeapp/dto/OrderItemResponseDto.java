package com.cafe.cafeapp.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDto {
    @Schema(description = "Id элемента заказа", example = "7")
    private Long id;
    @Schema(description = "Название продукта", example = "Американо")
    private String productName;
    @Schema(description = "Количество продукта", example = "2")
    private Integer quantity;
    @Schema(description = "Цена", example = "12.00")
    private BigDecimal price;
}
