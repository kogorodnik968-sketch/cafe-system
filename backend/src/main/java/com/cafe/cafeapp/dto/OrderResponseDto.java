package com.cafe.cafeapp.dto;

import com.cafe.cafeapp.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    @Schema(description = "Id заказа", example = "12")
    private Long id;
    @Schema(description = "Статус заказа", example = "IN_PROGRESS")
    private OrderStatus status;
    @Schema(description = "Имя пользователя", example = "Иван")
    private String customerFirstName;
    @Schema(description = "Фамилия пользователя", example = "Иванов")
    private String customerLastName;
    @Schema(description = "Список элементов заказа")
    private List<OrderItemResponseDto> items;
    @Schema(description = "Цена за весь заказ", example = "45.00")
    private BigDecimal totalPrice;
}
