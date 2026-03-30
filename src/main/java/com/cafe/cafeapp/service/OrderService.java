package com.cafe.cafeapp.service;

import com.cafe.cafeapp.cache.CacheKey;
import com.cafe.cafeapp.dto.OrderItemRequestDto;
import com.cafe.cafeapp.dto.OrderRequestDto;
import com.cafe.cafeapp.dto.OrderResponseDto;
import com.cafe.cafeapp.enums.OrderStatus;
import com.cafe.cafeapp.exception.NotFoundException;
import com.cafe.cafeapp.mapper.OrderMapper;
import com.cafe.cafeapp.model.*;
import com.cafe.cafeapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
    private static final String CUSTOMER_NOT_FOUND_MESSAGE = "Клиент не найден";
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Товар не найден";
    private final QueryCacheService queryCacheService;

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
                .orElseThrow(() -> new NotFoundException(CUSTOMER_NOT_FOUND_MESSAGE));

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
                    .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND_MESSAGE));

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPriceAtPurchase(product.getPrice());

            savedOrder.getOrderItems().add(item);

            total = total.add(
                    product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()))
            );

            queryCacheService.invalidateByProductId(item.getProduct().getId());
        }

        savedOrder.setTotalPrice(total);

        Order finalOrder = orderRepository.save(savedOrder);

        return orderMapper.toResponseDto(finalOrder);
    }

    @Transactional
    public OrderResponseDto update(Long id, OrderRequestDto dto) {

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Заказ с id " + id + " не найден"));

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new NotFoundException(CUSTOMER_NOT_FOUND_MESSAGE));

        existingOrder.setCustomer(customer);

        orderItemRepository.deleteAll(existingOrder.getOrderItems());
        existingOrder.getOrderItems().clear();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDto itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND_MESSAGE));

            OrderItem item = new OrderItem();
            item.setOrder(existingOrder);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPriceAtPurchase(product.getPrice());

            existingOrder.getOrderItems().add(item);

            total = total.add(
                    product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()))
            );
        }

        existingOrder.setTotalPrice(total);

        Order updatedOrder = orderRepository.save(existingOrder);

        return orderMapper.toResponseDto(updatedOrder);
    }

    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException(id);
        }

        Order order = orderRepository.findById(id).get();
        Set<Long> productIds = order.getOrderItems().stream()
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toSet());
        orderRepository.deleteById(id);

        for (Long productId : productIds) {
            queryCacheService.invalidateByProductId(productId);
        }
    }

    public void createWithoutTransaction(OrderRequestDto dto) {

        Order order = new Order();
        order.setStatus(OrderStatus.ACCEPTED);

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new NotFoundException(CUSTOMER_NOT_FOUND_MESSAGE));

        order.setCustomer(customer);

        Order savedOrder = orderRepository.save(order);
        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < dto.getItems().size(); i++) {

            OrderItemRequestDto itemDto = dto.getItems().get(i);

            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND_MESSAGE));

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
                .orElseThrow(() -> new NotFoundException(CUSTOMER_NOT_FOUND_MESSAGE));

        order.setCustomer(customer);

        Order savedOrder = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < dto.getItems().size(); i++) {

            OrderItemRequestDto itemDto = dto.getItems().get(i);

            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND_MESSAGE));

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


    public Page<OrderResponseDto> findWithJpql(String productName, BigDecimal minTotal, Pageable pageable) {
        CacheKey key = new CacheKey(productName, minTotal, pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSort().toString()
        );

        Page<OrderResponseDto> cached = queryCacheService.get(key);
        if (cached != null) {
            return cached;
        }

        Page<OrderResponseDto> result = orderRepository
                .findOrdersByProductAndMinTotal(productName, minTotal, pageable)
                .map(orderMapper::toResponseDto);

        queryCacheService.put(key, result);
        return result;
    }

    public Page<OrderResponseDto> findWithNative(String productName, BigDecimal minTotal, Pageable pageable) {
        CacheKey key = new CacheKey(productName, minTotal, pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSort().toString()
        );

        Page<OrderResponseDto> cached = queryCacheService.get(key);
        if (cached != null) {
            return cached;
        }

        Page<OrderResponseDto> result = orderRepository
                .findOrdersByProductAndMinTotalNative(productName, minTotal, pageable)
                .map(orderMapper::toResponseDto);


        queryCacheService.put(key, result);
        return result;
    }
}

