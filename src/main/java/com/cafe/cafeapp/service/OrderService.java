package com.cafe.cafeapp.service;

import com.cafe.cafeapp.dto.OrderItemRequestDto;
import com.cafe.cafeapp.dto.OrderRequestDto;
import com.cafe.cafeapp.dto.OrderResponseDto;
import com.cafe.cafeapp.enums.OrderStatus;
import com.cafe.cafeapp.exception.NotFoundException;
import com.cafe.cafeapp.mapper.OrderMapper;
import com.cafe.cafeapp.model.*;
import com.cafe.cafeapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderItemRepository orderItemRepository;
    private final EmployeeRepository employeeRepository;
    private final Random random = new Random();

    private Employee getRandomEmployee () {
        List<Employee> employees = employeeRepository.findAll();

        if (employees.isEmpty()) {
            throw new NotFoundException("There are no employees in the database");
        }

        int randomIndex = random.nextInt(employees.size());

        return employees.get(randomIndex);
    }

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

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        Employee randomEmployee = getRandomEmployee();

        Order order = new Order();
        order.setStatus(OrderStatus.ACCEPTED);
        order.setCustomer(customer);
        order.setEmployee(randomEmployee);
        order.setTotalPrice(BigDecimal.ZERO);
        order.setOrderItems(new ArrayList<>());

        Order savedOrder = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;


        for (OrderItemRequestDto itemDto : dto.getItems()) {

            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found"));

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPriceAtPurchase(product.getPrice());

            savedOrder.getOrderItems().add(item);

            total = total.add(
                    product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()))
            );
        }

        savedOrder.setTotalPrice(total);

        Order finalOrder = orderRepository.save(savedOrder);

        return orderMapper.toResponseDto(finalOrder);
    }

    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException(id);
        }
        orderRepository.deleteById(id);
    }

    public void createWithoutTransaction(OrderRequestDto dto) {

        Order order = new Order();
        order.setStatus(OrderStatus.ACCEPTED);

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        order.setCustomer(customer);

        Order savedOrder = orderRepository.save(order);
        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < dto.getItems().size(); i++) {

            OrderItemRequestDto itemDto = dto.getItems().get(i);

            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found"));

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPriceAtPurchase(product.getPrice());

            savedOrder.getOrderItems().add(item);

            BigDecimal itemTotal =
                    product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));

            total = total.add(itemTotal);

            orderItemRepository.save(item);


            if (i == 1) {
                throw new IllegalStateException("Intentional error");
            }
        }
        savedOrder.setTotalPrice(total);
        orderRepository.save(savedOrder);
    }

    @Transactional
    public void createWithTransaction(OrderRequestDto dto) {

        Order order = new Order();
        order.setStatus(OrderStatus.ACCEPTED);
        order.setTotalPrice(BigDecimal.ZERO);
        order.setOrderItems(new ArrayList<>());

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        order.setCustomer(customer);

        Order savedOrder = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < dto.getItems().size(); i++) {

            OrderItemRequestDto itemDto = dto.getItems().get(i);

            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new NotFoundException("product not found"));

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPriceAtPurchase(product.getPrice());

            savedOrder.getOrderItems().add(item);

            BigDecimal itemTotal =
                    product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));

            total = total.add(itemTotal);

            orderItemRepository.save(item);

            if (i == 1) {
                throw new IllegalStateException("Intentional error");
            }
        }

        savedOrder.setTotalPrice(total);
        orderRepository.save(savedOrder);
    }
}

