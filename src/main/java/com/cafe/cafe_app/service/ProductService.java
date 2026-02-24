package com.cafe.cafe_app.service;

import com.cafe.cafe_app.dto.ProductDto;
import com.cafe.cafe_app.mapper.ProductMapper;
import com.cafe.cafe_app.model.Product;
import com.cafe.cafe_app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public ProductDto getById(Long id) {
        Product product = repository.findById(id);
        return ProductMapper.toDtoElement(product);
    }

    public List<ProductDto> getAll() {
        return repository.findAll().stream().map(ProductMapper::toDtoElement).toList();
    }

    public List<ProductDto> getByName(String name) {
        List<Product> product = repository.findByName(name);
        return product.stream().map(ProductMapper::toDtoElement).toList();
    }
}
