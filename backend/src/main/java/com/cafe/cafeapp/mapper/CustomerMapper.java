package com.cafe.cafeapp.mapper;

import com.cafe.cafeapp.dto.CustomerRequestDto;
import com.cafe.cafeapp.dto.CustomerResponseDto;
import com.cafe.cafeapp.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponseDto toResponseDto (Customer customer);

    @Mapping(target = "id", ignore = true)
    //@Mapping(target = "orders", ignore = true)
    Customer toEntity (CustomerRequestDto dto);
}
