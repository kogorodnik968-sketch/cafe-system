package com.cafe.cafeapp.controller;


import com.cafe.cafeapp.dto.CategoryDto;
import com.cafe.cafeapp.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Категория", description = "Управление категориями товаров")
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{id}")
    @Operation(summary = "Получить категорию по id",
            description = "Возвращает полную информацию о категории по его уникальному идентификатору")
    public ResponseEntity<CategoryDto> getCategoryById (@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Получить категрии",
            description = "Возвращает список категорий")
    public List<CategoryDto> getCategoryByName (@RequestParam(required = false) String name) {
        if (name != null) {
            return categoryService.getByName(name);
        }

        return categoryService.getAll();
    }

    @PostMapping
    @Operation(summary = "Создать категрию",
            description = "Принимает данные товара и сохраняет в базу данных")
    public ResponseEntity<CategoryDto> createCategory (@RequestBody @Valid CategoryDto dto) {
        CategoryDto createObj = categoryService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createObj);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить категорию",
            description = "Принимает данные товара и сохраняет в базу данных")
    public ResponseEntity<CategoryDto> updateCategory (@PathVariable Long id, @RequestBody @Valid CategoryDto dto) {

        CategoryDto updateObj = categoryService.update(id, dto);

        return ResponseEntity.ok(updateObj);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить категорию",
            description = "Удаляет категорию из базы данных по ID. Не возвращает ничего")
    public ResponseEntity<Void> deleteCategory (@PathVariable Long id) {
        categoryService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
