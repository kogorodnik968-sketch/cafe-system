package com.cafe.cafeapp.controller;

import com.cafe.cafeapp.dto.OrderRequestDto;
import com.cafe.cafeapp.dto.OrderResponseDto;
import com.cafe.cafeapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> create(
            @RequestBody OrderRequestDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> update(@PathVariable Long id, @RequestBody OrderRequestDto dto) {
        return ResponseEntity.ok(orderService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test/no-transaction")
    public ResponseEntity<Void> testWithoutTransaction(
            @RequestBody OrderRequestDto dto) {

        orderService.createWithoutTransaction(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test/with-transaction")
    public ResponseEntity<Void> testWithTransaction(
            @RequestBody OrderRequestDto dto) {

        orderService.createWithTransaction(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/jpql")
    public Page<OrderResponseDto> findJpql(@RequestParam String productName,
            @RequestParam BigDecimal minTotal, Pageable pageable) {
        return orderService.findWithJpql(productName, minTotal, pageable);
    }

    @GetMapping("/native")
    public Page<OrderResponseDto> findNative(@RequestParam String productName,
            @RequestParam BigDecimal minTotal, Pageable pageable) {
        return orderService.findWithNative(productName, minTotal, pageable);
    }

}
