package com.cafe.cafeapp.mapper;

import com.cafe.cafeapp.dto.EmployeeRequestDto;
import com.cafe.cafeapp.dto.EmployeeResponseDto;
import com.cafe.cafeapp.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeResponseDto toResponseDto (Employee employee);
    List<EmployeeResponseDto> toDtoList (List<Employee> employees);

    @Mapping(target = "id", ignore = true)
    Employee toEntity (EmployeeRequestDto dto);
}
