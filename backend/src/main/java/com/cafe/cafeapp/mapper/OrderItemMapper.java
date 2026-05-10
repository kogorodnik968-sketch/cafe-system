package com.cafe.cafeapp.mapper;

import com.cafe.cafeapp.dto.OrderItemRequestDto;
import com.cafe.cafeapp.dto.OrderItemResponseDto;
import com.cafe.cafeapp.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface OrderItemMapper {

    @Mapping(source = "priceAtPurchase", target = "price")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponseDto toResponseDto (OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "priceAtPurchase", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    OrderItem toEntity (OrderItemRequestDto dto);
}
