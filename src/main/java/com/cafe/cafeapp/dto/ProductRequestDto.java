package com.cafe.cafeapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDto {
    private String name;
    private BigDecimal price;
    private Long categoryId;
    private Set<Long> ingredientsId;
    private Long tagId;
}
