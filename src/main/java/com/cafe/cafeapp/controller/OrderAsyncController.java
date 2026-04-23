package com.cafe.cafeapp.controller;

import com.cafe.cafeapp.dto.TaskStatusResponseDto;
import com.cafe.cafeapp.enums.StatusTask;
import com.cafe.cafeapp.service.OrderAsyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders/async")
@RequiredArgsConstructor
public class OrderAsyncController {

    private final OrderAsyncService orderAsyncService;

    @PostMapping("/{orderId}/process")
    public ResponseEntity<Long> startProcessing(@PathVariable Long orderId) {

        Long taskId = orderAsyncService.startProcessing(orderId);

        return ResponseEntity.ok(taskId);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskStatusResponseDto> getStatus(@PathVariable Long taskId) {

        StatusTask status = orderAsyncService.getStatus(taskId);

        TaskStatusResponseDto response = new TaskStatusResponseDto(taskId, status);

        return ResponseEntity.ok(response);
    }
}
