package com.cafe.cafeapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    @Schema(description = "Id пользователя", example = "4")
    @NotNull(message = "customerId не должен быть пустым")
    private Long customerId;
    @Schema(description = "Список элементов заказа")
    private List<@Valid OrderItemRequestDto> items;
}
