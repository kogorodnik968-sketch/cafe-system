package com.cafe.cafeapp.mapper;

import com.cafe.cafeapp.dto.IngredientDto;
import com.cafe.cafeapp.model.Ingredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IngredientMapper  {

    IngredientDto toDto(Ingredient ingredient);

    List<IngredientDto> toDtoList (List<Ingredient> ingredients);

    @Mapping(target = "id", ignore = true)
    Ingredient toEntity (IngredientDto dto);
}
