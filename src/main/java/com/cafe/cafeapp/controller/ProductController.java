package com.cafe.cafeapp.controller;

import com.cafe.cafeapp.dto.ProductRequestDto;
import com.cafe.cafeapp.dto.ProductResponseDto;
import com.cafe.cafeapp.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Товары", description = "Управление товарами")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по id",
            description = "Возвращает полную информацию о товаре по его уникальному идентификатору")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id)
    {
        return ResponseEntity.ok(productService.getById(id)) ;
    }

    @GetMapping
    @Operation(summary = "Получить товары",
            description = "Возвращает список товаров")
    public List<ProductResponseDto>  getProductsByName (@RequestParam(required = false) String name) {
        if (name != null) {
            return productService.getByName(name);
        }

        return productService.getAllProducts();
    }

    @PostMapping
    @Operation(summary = "Создать товар",
            description = "Принимает данные товара и сохраняет в базу данных")
    public ResponseEntity<ProductResponseDto> createProduct (@RequestBody @Valid ProductRequestDto dto) {
        ProductResponseDto response = productService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить товар",
            description = "Принимает данные товара и сохраняет в базу данных")
    public ResponseEntity<ProductResponseDto> updateProduct (@PathVariable Long id,
                                                             @RequestBody @Valid ProductRequestDto dto)
    {
        ProductResponseDto response = productService.update(id, dto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить товар",
            description = "Удаляет товар из базы данных по ID. Не возвращает ничего")
    public ResponseEntity<Void> deleteProduct (@PathVariable Long id) {
        productService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping ("/bulk")
    @Operation(summary = "Создать список товаров",
            description = "Принимает список товаров и сохраняет в базу данных")
    public List<ProductResponseDto> createBulk(@RequestBody List<ProductRequestDto> dtos) {
        return productService.createBulk(dtos);
    }

    @PostMapping("/bulk/without-transaction")
    @Operation(summary = "Массовое создание продуктов БЕЗ транзакции")
    public ResponseEntity<List<ProductResponseDto>> createBulkDemoWithoutTransaction(
            @RequestBody List<ProductRequestDto> dtos) {
        return ResponseEntity.ok(productService.createBulkWithoutTransaction(dtos));
    }

    @PostMapping("/bulk/with-transaction")
    @Operation(summary = "Массовое создание продуктов С транзакцией")
    public ResponseEntity<List<ProductResponseDto>> createBulkDemoWithTransaction(
            @RequestBody List<ProductRequestDto> dtos) {
        return ResponseEntity.ok(productService.createBulkWithTransaction(dtos));
    }
}
