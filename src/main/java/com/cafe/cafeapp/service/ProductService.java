package com.cafe.cafeapp.service;

import com.cafe.cafeapp.dto.ProductDto;
import com.cafe.cafeapp.mapper.ProductMapper;
import com.cafe.cafeapp.model.Product;
import com.cafe.cafeapp.repository.ProductRepository;
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
