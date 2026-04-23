package com.cafe.cafeapp.service;

import com.cafe.cafeapp.enums.OrderStatus;
import com.cafe.cafeapp.enums.StatusTask;
import com.cafe.cafeapp.exception.NotFoundException;
import com.cafe.cafeapp.model.Order;
import com.cafe.cafeapp.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncOrderProcessor {

    private final OrderRepository orderRepository;

    @Async
    public CompletableFuture<Void> processOrder (Long orderId, Long taskId, Map<Long, StatusTask> tasks) {

        log.info("Асинхронная задача {} начала выполнение в потоке {}", taskId, Thread.currentThread().getName());

        try {
            Thread.sleep(10000);

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("Заказ не найден"));

            order.setStatus(OrderStatus.READY);
            orderRepository.save(order);

            tasks.put(taskId, StatusTask.DONE);
            log.info("Асинхронная задача {} завершена", taskId);

        } catch (Exception ex) {

            tasks.put(taskId, StatusTask.FAILED);
            log.error("Асинхронная задача {} провалилась", taskId, ex);
        }

        return CompletableFuture.completedFuture(null);
    }
}
