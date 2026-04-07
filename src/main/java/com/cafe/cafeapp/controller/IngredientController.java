package com.cafe.cafeapp.controller;

import com.cafe.cafeapp.dto.IngredientDto;
import com.cafe.cafeapp.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Ингридиенты", description = "Управление ингридиентами")
@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    @Operation(summary = "Получить ингридиенты",
            description = "Возвращает список ингридиентов")
    public ResponseEntity<List<IngredientDto>> getAll() {
        return ResponseEntity.ok(ingredientService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить ингридиент по id",
            description = "Возвращает полную информацию о ингридиенте по его уникальному идентификатору")
    public ResponseEntity<IngredientDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Создать ингридиент",
            description = "Принимает данные ингридиента и сохраняет в базу данных")
    public ResponseEntity<IngredientDto> create(@RequestBody @Valid IngredientDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ingredientService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить ингридиент",
            description = "Принимает данные ингридиента и сохраняет в базу данных")
    public ResponseEntity<IngredientDto> update(
            @PathVariable Long id,
            @RequestBody @Valid IngredientDto dto) {

        return ResponseEntity.ok(ingredientService.update(id, dto));
    }

}

