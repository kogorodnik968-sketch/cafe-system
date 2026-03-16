package com.cafe.cafeapp.mapper;

import com.cafe.cafeapp.dto.CategoryDto;
import com.cafe.cafeapp.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    List<CategoryDto> toDtoList(List<Category> category);

    @Mapping(target = "id", ignore = true)
    Category toEntity (CategoryDto dto);
}
