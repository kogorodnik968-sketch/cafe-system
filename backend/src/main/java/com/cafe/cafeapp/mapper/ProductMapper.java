package com.cafe.cafeapp.mapper;

import com.cafe.cafeapp.dto.ProductRequestDto;
import com.cafe.cafeapp.dto.ProductResponseDto;
import com.cafe.cafeapp.model.Ingredient;
import com.cafe.cafeapp.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring", uses = {CategoryMapper.class, IngredientMapper.class, TagMapper.class})
public interface ProductMapper {

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "ingredients", target = "ingredientsName")
    @Mapping(source = "tag.name", target = "tagName")
    ProductResponseDto toResponseDto(Product product);

    List<ProductResponseDto> toResponseDto(List<Product> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "ingredients", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "tag", ignore = true)
    Product toEntity(ProductRequestDto dto);

    default String mapingredientToString (Ingredient ingredient) {
        return ingredient != null ? ingredient.getName() : null;
    }



}