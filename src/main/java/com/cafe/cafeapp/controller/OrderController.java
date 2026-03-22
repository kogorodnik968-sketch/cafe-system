package com.cafe.cafeapp.controller;

import com.cafe.cafeapp.dto.OrderRequestDto;
import com.cafe.cafeapp.dto.OrderResponseDto;
import com.cafe.cafeapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
}
