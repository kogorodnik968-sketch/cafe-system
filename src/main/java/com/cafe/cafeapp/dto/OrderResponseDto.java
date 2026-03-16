package com.cafe.cafeapp.dto;

import com.cafe.cafeapp.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Long id;
    private OrderStatus status;
    private String customerName;
    private List<OrderItemResponseDto> items;
}
