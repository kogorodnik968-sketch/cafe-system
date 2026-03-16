package com.cafe.cafeapp.controller;

import com.cafe.cafeapp.dto.ProductRequestDto;
import com.cafe.cafeapp.dto.ProductResponseDto;
//import com.cafe.cafeapp.dto.ProductRequestDto;
import com.cafe.cafeapp.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id)
    {
        return ResponseEntity.ok(productService.getById(id)) ;
    }

    @GetMapping
    public List<ProductResponseDto>  getProductsByName (@RequestParam(required = false) String name) {
        if (name != null) {
            return productService.getByName(name);
        }

        return productService.getAllProducts();
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct (@RequestBody ProductRequestDto dto) {
        ProductResponseDto response = productService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct (@PathVariable Long id, @RequestBody ProductRequestDto dto)
    {
        ProductResponseDto response = productService.update(id, dto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct (@PathVariable Long id) {
        productService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
