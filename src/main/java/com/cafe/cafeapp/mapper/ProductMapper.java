package com.cafe.cafeapp.mapper;

import com.cafe.cafeapp.dto.ProductDto;
import com.cafe.cafeapp.model.Product;

public class ProductMapper {

    private ProductMapper() { }

    public static ProductDto toDtoElement(Product product) {
        return new ProductDto(
                product.getIdPr(),
                product.getNamePr(),
                product.getFinalPr());
    }
}
