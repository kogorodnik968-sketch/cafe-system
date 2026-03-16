package com.cafe.cafeapp.mapper;

import com.cafe.cafeapp.dto.OrderRequestDto;
import com.cafe.cafeapp.dto.OrderResponseDto;
import com.cafe.cafeapp.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, CustomerMapper.class})
public interface OrderMapper {

    @Mapping(source = "customer.fullName", target = "customerName")
    @Mapping(source = "orderItems", target = "items")
    OrderResponseDto toResponseDto (Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping (target = "employee", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(source = "items", target = "orderItems")
    Order toEntity (OrderRequestDto dto);
}
