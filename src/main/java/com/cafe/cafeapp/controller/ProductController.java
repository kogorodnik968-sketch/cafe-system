package com.cafe.cafeapp.controller;

import com.cafe.cafeapp.dto.ProductDto;
import com.cafe.cafeapp.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<ProductDto> getProductsByName(@RequestParam(required = false) String name) {
        if (name != null) {
            return service.getByName(name);
        }
        return service.getAll();
    }

}
