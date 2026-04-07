package com.cafe.cafeapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequestDto {
    @Schema(description = "Количесвто товара", example = "2")
    @NotNull(message = "Количество не должно быть пустым")
    @Min(value = 1, message = "Количество должно быть больше 0")
    private Integer quantity;

    @Schema(description = "Id заказываемого продукта", example = "12")
    @NotNull(message = "productId не должен быть пустым")
    private Long productId;
}
