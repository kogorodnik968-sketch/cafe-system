package com.cafe.cafe_app.mapper;

import com.cafe.cafe_app.dto.ProductDto;
import com.cafe.cafe_app.model.Product;

public class ProductMapper {

    private ProductMapper(){}

    public static ProductDto toDtoElement(Product product){
        return new ProductDto(
                product.getIdPr(),
                product.getNamePr(),
                product.getFinalPr());
    }
}
