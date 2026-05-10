package com.cafe.cafeapp.service;

import com.cafe.cafeapp.cache.QueryCacheService;
import com.cafe.cafeapp.dto.OrderItemRequestDto;
import com.cafe.cafeapp.dto.OrderRequestDto;
import com.cafe.cafeapp.dto.OrderResponseDto;
import com.cafe.cafeapp.exception.NotFoundException;
import com.cafe.cafeapp.mapper.OrderMapper;
import com.cafe.cafeapp.model.*;
import com.cafe.cafeapp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private QueryCacheService queryCacheService;

    @InjectMocks
    private OrderService orderService;


    @Test
    void getById_success() {
        Order order = new Order();
        OrderResponseDto dto = new OrderResponseDto();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toResponseDto(order)).thenReturn(dto);

        assertEquals(dto, orderService.getById(1L));
    }

    @Test
    void getById_notFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> orderService.getById(1L));
    }

    @Test
    void getAll_success() {
        Order order = new Order();
        OrderResponseDto dto = new OrderResponseDto();

        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.toResponseDto(order)).thenReturn(dto);

        assertEquals(1, orderService.getAll().size());
    }

    @Test
    void create_success() {
        Customer customer = new Customer();
        Employee employee = new Employee();
        Product product = new Product();
        product.setPrice(BigDecimal.TEN);
        product.setName("Coffee");

        OrderRequestDto dto = new OrderRequestDto(
                1L,
                List.of(new OrderItemRequestDto(2, 1L))
        );

        Order savedOrder = new Order();
        savedOrder.setOrderItems(new ArrayList<>());

        OrderResponseDto responseDto = new OrderResponseDto();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(orderRepository.save(any())).thenReturn(savedOrder);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderMapper.toResponseDto(any())).thenReturn(responseDto);

        OrderResponseDto result = orderService.create(dto);

        assertEquals(responseDto, result);
        verify(queryCacheService).invalidateByProductId("Coffee");
    }

    @Test
    void create_customerNotFound() {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of());

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> orderService.create(dto));
    }

    @Test
    void create_noEmployees() {
        OrderRequestDto dto = new OrderRequestDto(1L, List.of());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
        when(employeeRepository.findAll()).thenReturn(List.of());

        assertThrows(NotFoundException.class,
                () -> orderService.create(dto));
    }

    @Test
    void create_productNotFound() {
        Customer customer = new Customer();
        Employee employee = new Employee();

        OrderRequestDto dto = new OrderRequestDto(
                1L,
                List.of(new OrderItemRequestDto(2, 1L))
        );

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(orderRepository.save(any())).thenReturn(new Order());
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> orderService.create(dto));
    }

    @Test
    void update_success() {
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());

        Customer customer = new Customer();
        Product product = new Product();
        product.setPrice(BigDecimal.TEN);

        OrderRequestDto dto = new OrderRequestDto(
                1L,
                List.of(new OrderItemRequestDto(2, 1L))
        );

        OrderResponseDto responseDto = new OrderResponseDto();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponseDto(order)).thenReturn(responseDto);

        OrderResponseDto result = orderService.update(1L, dto);

        assertEquals(responseDto, result);
    }

    @Test
    void update_notFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> orderService.update(1L, new OrderRequestDto()));
    }

    @Test
    void update_customerNotFound() {
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        OrderRequestDto dto = new OrderRequestDto(1L, List.of());

        assertThrows(NotFoundException.class,
                () -> orderService.update(1L, dto));
    }

    @Test
    void update_productNotFound() {
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());

        Customer customer = new Customer();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        OrderRequestDto dto = new OrderRequestDto(
                1L,
                List.of(new OrderItemRequestDto(2, 1L))
        );

        assertThrows(NotFoundException.class,
                () -> orderService.update(1L, dto));
    }

    @Test
    void delete_notFound() {
        when(orderRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> orderService.delete(1L));
    }

    @Test
    void delete_success() {
        Order order = new Order();

        Product product = new Product();
        product.setName("Coffee");

        OrderItem item = new OrderItem();
        item.setProduct(product);

        order.setOrderItems(List.of(item));

        when(orderRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.delete(1L);

        verify(orderRepository).deleteById(1L);
        verify(queryCacheService).invalidateByProductId("Coffee");
    }

    @Test
    void findWithJpql_fromCache() {
        Pageable pageable = mock(Pageable.class);
        Page<OrderResponseDto> page = mock(Page.class);

        when(pageable.getPageNumber()).thenReturn(0);
        when(pageable.getPageSize()).thenReturn(10);
        when(pageable.getSort()).thenReturn(mock(org.springframework.data.domain.Sort.class));

        when(queryCacheService.get(any())).thenReturn(page);

        assertEquals(page, orderService.findWithJpql("coffee", BigDecimal.TEN, pageable));
    }

    @Test
    void findWithJpql_noCache() {
        Pageable pageable = mock(Pageable.class);
        Page<Order> orderPage = mock(Page.class);
        Page<OrderResponseDto> dtoPage = mock(Page.class);

        when(pageable.getPageNumber()).thenReturn(0);
        when(pageable.getPageSize()).thenReturn(10);
        when(pageable.getSort()).thenReturn(mock(org.springframework.data.domain.Sort.class));

        when(queryCacheService.get(any())).thenReturn(null);
        when(orderRepository.findOrdersByProductAndMinTotal(any(), any(), any()))
                .thenReturn(orderPage);
        when(orderPage.map(any(Function.class))).thenReturn(dtoPage);

        assertEquals(dtoPage, orderService.findWithJpql("coffee", BigDecimal.TEN, pageable));
        verify(queryCacheService).put(any(), eq(dtoPage));
    }

    @Test
    void findWithNative_fromCache() {
        Pageable pageable = mock(Pageable.class);
        Page<OrderResponseDto> page = mock(Page.class);

        when(pageable.getPageNumber()).thenReturn(0);
        when(pageable.getPageSize()).thenReturn(10);
        when(pageable.getSort()).thenReturn(mock(org.springframework.data.domain.Sort.class));

        when(queryCacheService.get(any())).thenReturn(page);

        assertEquals(page, orderService.findWithNative("coffee", BigDecimal.TEN, pageable));
    }

    @Test
    void findWithNative_noCache() {
        Pageable pageable = mock(Pageable.class);
        Page<Order> orderPage = mock(Page.class);
        Page<OrderResponseDto> dtoPage = mock(Page.class);

        when(pageable.getPageNumber()).thenReturn(0);
        when(pageable.getPageSize()).thenReturn(10);
        when(pageable.getSort()).thenReturn(mock(org.springframework.data.domain.Sort.class));

        when(queryCacheService.get(any())).thenReturn(null);
        when(orderRepository.findOrdersByProductAndMinTotalNative(any(), any(), any()))
                .thenReturn(orderPage);
        when(orderPage.map(any(Function.class))).thenReturn(dtoPage);

        assertEquals(dtoPage, orderService.findWithNative("coffee", BigDecimal.TEN, pageable));
        verify(queryCacheService).put(any(), eq(dtoPage));
    }

}
