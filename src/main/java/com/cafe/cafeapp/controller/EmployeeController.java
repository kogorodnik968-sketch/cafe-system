package com.cafe.cafeapp.controller;

import com.cafe.cafeapp.dto.EmployeeRequestDto;
import com.cafe.cafeapp.dto.EmployeeResponseDto;
import com.cafe.cafeapp.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Работники", description = "Управление работниками кафе")
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "Получить работников",
            description = "Возвращает список пользователей")
    public ResponseEntity<List<EmployeeResponseDto>> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить работника по id",
            description = "Возвращает полную информацию о работнике по его уникальному идентификатору")
    public ResponseEntity<EmployeeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Создать работника",
            description = "Принимает данные работника и сохраняет в базу данных")
    public ResponseEntity<EmployeeResponseDto> create(
            @RequestBody @Valid EmployeeRequestDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить работника",
            description = "Принимает данные работника и сохраняет в базу данных")
    public ResponseEntity<EmployeeResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid EmployeeRequestDto dto) {

        return ResponseEntity.ok(employeeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить работника",
            description = "Удаляет работника из базы данных по ID. Не возвращает ничего")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

