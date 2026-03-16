package com.cafe.cafeapp.controller;


import com.cafe.cafeapp.dto.CategoryDto;
import com.cafe.cafeapp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById (@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @GetMapping
    public List<CategoryDto> getCategoryByName (@RequestParam(required = false) String name) {
        if (name != null) {
            return categoryService.getByName(name);
        }

        return categoryService.getAll();
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory (@RequestBody CategoryDto dto) {
        CategoryDto createObj = categoryService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createObj);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory (@PathVariable Long id, @RequestBody CategoryDto dto) {

        CategoryDto updateObj = categoryService.update(id, dto);

        return ResponseEntity.ok(updateObj);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory (@PathVariable Long id) {
        categoryService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
