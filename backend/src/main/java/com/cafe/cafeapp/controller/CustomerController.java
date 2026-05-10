package com.cafe.cafeapp.controller;

import com.cafe.cafeapp.dto.CustomerRequestDto;
import com.cafe.cafeapp.dto.CustomerResponseDto;
import com.cafe.cafeapp.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Клиенты", description = "Управление клиентами кафе")
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Получить пользователей",
            description = "Возвращает список пользователей")
    public ResponseEntity<List<CustomerResponseDto>> getAll() {
        return ResponseEntity.ok(customerService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по id",
            description = "Возвращает полную информацию о пользователе по его уникальному идентификатору")
    public ResponseEntity<CustomerResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Создать пользователя",
            description = "Принимает данные пользователя и сохраняет в базу данных")
    public ResponseEntity<CustomerResponseDto> create(
            @Valid @RequestBody CustomerRequestDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя",
            description = "Принимает данные пользователя и сохраняет в базу данных")
    public ResponseEntity<CustomerResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid CustomerRequestDto dto) {

        return ResponseEntity.ok(customerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя",
            description = "Удаляет пользователя из базы данных по ID. Не возвращает ничего")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
