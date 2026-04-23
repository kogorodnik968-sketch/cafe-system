package com.cafe.cafeapp.service;

import com.cafe.cafeapp.enums.OrderStatus;
import com.cafe.cafeapp.enums.StatusTask;
import com.cafe.cafeapp.exception.NotFoundException;
import com.cafe.cafeapp.model.Order;
import com.cafe.cafeapp.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderAsyncService {

    private final OrderRepository orderRepository;
    private final AsyncOrderProcessor asyncOrderProcessor;

    private final Map<Long, StatusTask> tasks = new ConcurrentHashMap<>();
    private final AtomicLong taskIdGenerator = new AtomicLong();

    public Long startProcessing(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Заказ не найден"));

        if (order.getStatus() == OrderStatus.READY) {
            throw new IllegalStateException("Заказ уже обработан");
        }

        Long taskId = taskIdGenerator.incrementAndGet();

        tasks.put(taskId, StatusTask.IN_PROGRESS);

        log.info("Запущена async-задача {} для заказа {}", taskId, orderId);

        asyncOrderProcessor.processOrder(orderId, taskId, tasks);
        log.info("Метод startProcessing завершил работу, задача {} выполняется в фоне", taskId);

        return taskId;
    }

    public StatusTask getStatus(Long taskId) {
        return tasks.getOrDefault(taskId, StatusTask.FAILED);
    }
}