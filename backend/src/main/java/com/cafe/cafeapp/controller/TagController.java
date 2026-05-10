package com.cafe.cafeapp.controller;

import com.cafe.cafeapp.dto.TagDto;
import com.cafe.cafeapp.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Теги", description = "Управление тегами товаров")
@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {
    private  final TagService tagService;

    @GetMapping("/{id}")
    @Operation( summary = "Получить тег по ID",
            description = "Возвращает информацию о теге по его уникальному идентификатору")
    public ResponseEntity<TagDto> getTagById (@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Получить теги",
            description = "Возвращает список всех тегов. Если указан параметр name, возвращает теги с указанным именем")
    public List<TagDto> getTagByName (@RequestParam(required = false) String name) {
        if (name != null) {
            return tagService.getByName(name);
        }

        return tagService.getAll();
    }

    @PostMapping
    @Operation(summary = "Создать новый тег",
            description = "Принимает данные тега и сохраняет его в базу данных")
    public ResponseEntity<TagDto> createTag (@RequestBody @Valid TagDto dto) {
        TagDto createObj = tagService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createObj);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить тег",
            description = "Обновляет существующий тег по ID. Принимает новые данные тега")
    public ResponseEntity<TagDto> updateTag (@PathVariable Long id, @RequestBody @Valid TagDto dto) {

        TagDto updateObj = tagService.update(id, dto);

        return ResponseEntity.ok(updateObj);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить тег",
            description = "Удаляет тег из базы данных по ID. Не возвращает ничего")
    public ResponseEntity<Void> deleteTag (@PathVariable Long id) {
        tagService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
