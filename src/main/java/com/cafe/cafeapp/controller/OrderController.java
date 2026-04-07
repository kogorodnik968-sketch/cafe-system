package com.cafe.cafeapp.controller;

import com.cafe.cafeapp.dto.OrderRequestDto;
import com.cafe.cafeapp.dto.OrderResponseDto;
import com.cafe.cafeapp.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Заказы", description = "Управление заказами кафе")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Получить все заказы",
            description = "Возвращает список всех заказов в системе")
    public ResponseEntity<List<OrderResponseDto>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить заказ по ID",
            description = "Возвращает полную информацию о заказе по его уникальному идентификатору")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Создать новый заказ",
            description = "Принимает данные заказа и сохраняет его в базу данных")
    public ResponseEntity<OrderResponseDto> create(
            @RequestBody @Valid OrderRequestDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить заказ",
            description = "Обновляет существующий заказ по ID. Принимает новые данные заказа")
    public ResponseEntity<OrderResponseDto> update(@PathVariable Long id, @RequestBody @Valid OrderRequestDto dto) {
        return ResponseEntity.ok(orderService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить заказ",
            description = "Удаляет заказ из базы данных по ID. Не возвращает ничего")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/jpql")
    @Operation(summary = "Поиск заказов (JPQL)",
            description = "Ищет заказы по названию товара и минимальной сумме. " +
                    "Использует JPQL запрос. Поддерживает пагинацию")
    public Page<OrderResponseDto> findJpql(@RequestParam String productName,
            @RequestParam BigDecimal minTotal, Pageable pageable) {
        return orderService.findWithJpql(productName, minTotal, pageable);
    }

    @GetMapping("/native")
    @Operation(summary = "Поиск заказов (Native SQL)",
            description = "Ищет заказы по названию товара и минимальной сумме." +
                    "Использует нативный SQL запрос. Поддерживает пагинацию")
    public Page<OrderResponseDto> findNative(@RequestParam String productName,
            @RequestParam BigDecimal minTotal, Pageable pageable) {
        return orderService.findWithNative(productName, minTotal, pageable);
    }
}
