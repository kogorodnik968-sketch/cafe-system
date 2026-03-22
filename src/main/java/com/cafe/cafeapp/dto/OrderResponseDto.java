package com.cafe.cafeapp.dto;

import com.cafe.cafeapp.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Long id;
    private OrderStatus status;
    private String customerFirstName;
    private String customerLastName;
    private List<OrderItemResponseDto> items;
    private BigDecimal totalPrice;
}
