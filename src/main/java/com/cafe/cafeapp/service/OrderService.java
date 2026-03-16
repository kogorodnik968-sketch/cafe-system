package com.cafe.cafeapp.service;

import com.cafe.cafeapp.dto.OrderRequestDto;
import com.cafe.cafeapp.dto.OrderResponseDto;
import com.cafe.cafeapp.enums.OrderStatus;
import com.cafe.cafeapp.exception.NotFoundException;
import com.cafe.cafeapp.mapper.OrderItemMapper;
import com.cafe.cafeapp.mapper.OrderMapper;
import com.cafe.cafeapp.model.Customer;
import com.cafe.cafeapp.model.Order;
import com.cafe.cafeapp.model.OrderItem;
import com.cafe.cafeapp.model.Product;
import com.cafe.cafeapp.repository.CustomerRepository;
import com.cafe.cafeapp.repository.OrderRepository;
import com.cafe.cafeapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional(readOnly = true)
    public OrderResponseDto getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));

        return orderMapper.toResponseDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public OrderResponseDto create(OrderRequestDto dto) {

        Order order = orderMapper.toEntity(dto);

        order.setStatus(OrderStatus.CREATED);


        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        order.setCustomer(customer);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : order.getOrderItems()) {

            Product product = productRepository.findById(
                    item.getProduct().getId()
            ).orElseThrow(() -> new NotFoundException("Product not found"));

            item.setProduct(product);
            item.setOrder(order);

            BigDecimal itemTotal =
                    product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            item.setPriceAtPurchase(product.getPrice());
            total = total.add(itemTotal);
        }

        order.setTotalPrice(total);

        Order saved = orderRepository.save(order);

        return orderMapper.toResponseDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException(id);
        }
        orderRepository.deleteById(id);
    }
}

