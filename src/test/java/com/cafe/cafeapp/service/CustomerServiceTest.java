package com.cafe.cafeapp.service;

import com.cafe.cafeapp.dto.CustomerRequestDto;
import com.cafe.cafeapp.dto.CustomerResponseDto;
import com.cafe.cafeapp.exception.AlreadyExistsException;
import com.cafe.cafeapp.exception.DeleteNotAllowedException;
import com.cafe.cafeapp.exception.NotFoundException;
import com.cafe.cafeapp.mapper.CustomerMapper;
import com.cafe.cafeapp.model.Customer;
import com.cafe.cafeapp.model.Order;
import com.cafe.cafeapp.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void getAll_shouldReturnDtoList() {
        Customer customer = new Customer();
        CustomerResponseDto dto = new CustomerResponseDto();

        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(customerMapper.toResponseDto(customer)).thenReturn(dto);

        List<CustomerResponseDto> result = customerService.getAll();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void getById_shouldReturnDto_whenFound() {
        Customer customer = new Customer();
        CustomerResponseDto dto = new CustomerResponseDto();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponseDto(customer)).thenReturn(dto);

        CustomerResponseDto result = customerService.getById(1L);

        assertEquals(dto, result);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> customerService.getById(1L));
    }

    @Test
    void create_shouldSaveAndReturnDto() {
        CustomerRequestDto dto = new CustomerRequestDto("Ivan", "Ivanov", "Ivanovich", "+123456789012");
        Customer entity = new Customer();
        Customer saved = new Customer();
        CustomerResponseDto responseDto = new CustomerResponseDto();

        when(customerRepository.existsByFirstName("Ivan")).thenReturn(false);

        when(customerMapper.toEntity(dto)).thenReturn(entity);
        when(customerRepository.save(entity)).thenReturn(saved);
        when(customerMapper.toResponseDto(saved)).thenReturn(responseDto);

        CustomerResponseDto result = customerService.create(dto);

        assertEquals(responseDto, result);
    }

    @Test
    void create_shouldThrow_whenExists() {
        CustomerRequestDto dto = new CustomerRequestDto("A", "B", "C", "+123456789012");

        when(customerRepository.existsByFirstName("A")).thenReturn(true);
        when(customerRepository.existsByLastName("B")).thenReturn(true);
        when(customerRepository.existsByMiddleName("C")).thenReturn(true);

        assertThrows(AlreadyExistsException.class,
                () -> customerService.create(dto));
    }

    @Test
    void update_shouldUpdateAndReturnDto() {
        Customer existing = new Customer();
        CustomerRequestDto dto = new CustomerRequestDto("A", "B", "C", "+123456789012");
        CustomerResponseDto responseDto = new CustomerResponseDto();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerMapper.toResponseDto(existing)).thenReturn(responseDto);

        CustomerResponseDto result = customerService.update(1L, dto);

        assertEquals(responseDto, result);
        assertEquals("A", existing.getFirstName());
        assertEquals("B", existing.getLastName());
        assertEquals("C", existing.getMiddleName());
        assertEquals("+123456789012", existing.getPhoneNumber());
    }

    @Test
    void update_shouldThrow_whenNotFound() {
        CustomerRequestDto dto = new CustomerRequestDto();

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> customerService.update(1L, dto));
    }

    @Test
    void delete_shouldThrow_whenNotExists() {
        when(customerRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> customerService.delete(1L));
    }

    @Test
    void delete_shouldThrow_whenHasOrders() {
        Customer customer = new Customer();
        customer.setOrders(List.of(new Order()));

        when(customerRepository.existsById(1L)).thenReturn(true);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(DeleteNotAllowedException.class,
                () -> customerService.delete(1L));
    }

    @Test
    void delete_shouldDelete_whenValid() {
        Customer customer = new Customer();
        customer.setOrders(List.of());

        when(customerRepository.existsById(1L)).thenReturn(true);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.delete(1L);

        verify(customerRepository).deleteById(1L);
    }
}

