package com.cafe.cafeapp.controller;

import com.cafe.cafeapp.dto.IngredientDto;
import com.cafe.cafeapp.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public ResponseEntity<List<IngredientDto>> getAll() {
        return ResponseEntity.ok(ingredientService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.getById(id));
    }

    @PostMapping
    public ResponseEntity<IngredientDto> create(@RequestBody IngredientDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ingredientService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDto> update(
            @PathVariable Long id,
            @RequestBody IngredientDto dto) {

        return ResponseEntity.ok(ingredientService.update(id, dto));
    }

}

