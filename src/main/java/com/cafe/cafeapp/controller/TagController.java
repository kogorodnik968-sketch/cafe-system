package com.cafe.cafeapp.controller;

import com.cafe.cafeapp.dto.TagDto;
import com.cafe.cafeapp.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {
    private  final TagService tagService;

    @GetMapping("/{id}")
    public ResponseEntity<TagDto> getTagById (@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getById(id));
    }

    @GetMapping
    public List<TagDto> getTagByName (@RequestParam(required = false) String name) {
        if (name != null) {
            return tagService.getByName(name);
        }

        return tagService.getAll();
    }

    @PostMapping
    public ResponseEntity<TagDto> createTag (@RequestBody TagDto dto) {
        TagDto createObj = tagService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createObj);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDto> updateTag (@PathVariable Long id, @RequestBody TagDto dto) {

        TagDto updateObj = tagService.update(id, dto);

        return ResponseEntity.ok(updateObj);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag (@PathVariable Long id) {
        tagService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
