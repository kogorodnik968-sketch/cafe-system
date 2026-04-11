package com.cafe.cafeapp.service;

import com.cafe.cafeapp.dto.CustomerRequestDto;
import com.cafe.cafeapp.dto.EmployeeRequestDto;
import com.cafe.cafeapp.dto.EmployeeResponseDto;
import com.cafe.cafeapp.enums.EmployeeRole;
import com.cafe.cafeapp.exception.AlreadyExistsException;
import com.cafe.cafeapp.exception.DeleteNotAllowedException;
import com.cafe.cafeapp.exception.NotFoundException;
import com.cafe.cafeapp.mapper.EmployeeMapper;
import com.cafe.cafeapp.model.Customer;
import com.cafe.cafeapp.model.Employee;
import com.cafe.cafeapp.model.Order;
import com.cafe.cafeapp.repository.EmployeeRepository;
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
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void getAll_shouldReturnDtoList() {
        List<Employee> employees = List.of(new Employee());
        List<EmployeeResponseDto> dtos = List.of(new EmployeeResponseDto());

        when(employeeRepository.findAll()).thenReturn(employees);
        when(employeeMapper.toDtoList(employees)).thenReturn(dtos);

        List<EmployeeResponseDto> result = employeeService.getAll();

       assertEquals(dtos, result);
    }

    @Test
    void getById_shouldReturnDto_whenFound() {
        Employee employee = new Employee();
        EmployeeResponseDto dto = new EmployeeResponseDto();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeMapper.toResponseDto(employee)).thenReturn(dto);

        EmployeeResponseDto result = employeeService.getById(1L);

        assertEquals(dto, result);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> employeeService.getById(1L));
    }

    @Test
    void create_shouldSaveAndReturnDto() {
        EmployeeRequestDto dto = new EmployeeRequestDto("Ivan", "Ivanov", "Ivanovich", EmployeeRole.BARISTA);
        Employee entity = new Employee();
        Employee saved = new Employee();
        EmployeeResponseDto responseDto = new EmployeeResponseDto();

        when(employeeRepository.existsByFirstName("Ivan")).thenReturn(false);

        when(employeeMapper.toEntity(dto)).thenReturn(entity);
        when(employeeRepository.save(entity)).thenReturn(saved);
        when(employeeMapper.toResponseDto(saved)).thenReturn(responseDto);

        EmployeeResponseDto result = employeeService.create(dto);

        assertEquals(responseDto, result);
    }

    @Test
    void create_shouldThrow_whenExists() {
        EmployeeRequestDto dto = new EmployeeRequestDto("Ivan", "Ivanov", "Ivanovich", EmployeeRole.BARISTA);

        when(employeeRepository.existsByFirstName("Ivan")).thenReturn(true);
        when(employeeRepository.existsByLastName("Ivanov")).thenReturn(true);
        when(employeeRepository.existsByMiddleName("Ivanovich")).thenReturn(true);

        assertThrows(AlreadyExistsException.class,
                () -> employeeService.create(dto));
    }

    @Test
    void update_shouldUpdateAndReturnDto() {
        Employee existing = new Employee();
        EmployeeRequestDto dto = new EmployeeRequestDto("Ivan", "Ivanov", "Ivanovich", EmployeeRole.BARISTA);
        EmployeeResponseDto responseDto = new EmployeeResponseDto();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeMapper.toResponseDto(existing)).thenReturn(responseDto);

        EmployeeResponseDto result = employeeService.update(1L,dto);

        assertEquals(responseDto, result);
        assertEquals("Ivan",existing.getFirstName());
        assertEquals("Ivanov",existing.getLastName());
        assertEquals("Ivanovich",existing.getMiddleName());
    }

    @Test
    void update_shouldThrow_whenNotFound() {
        EmployeeRequestDto dto = new EmployeeRequestDto();

        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> employeeService.update(1L, dto));
    }

    @Test
    void delete_shouldThrow_whenNotExists() {
        when(employeeRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> employeeService.delete(1L));
    }

    @Test
    void delete_shouldThrow_whenHasOrders() {
        Employee employee = new Employee();
        employee.setOrders(List.of(new Order()));

        when(employeeRepository.existsById(1L)).thenReturn(true);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThrows(DeleteNotAllowedException.class,
                () -> employeeService.delete(1L));
    }

    @Test
    void delete_shouldDelete_whenValid() {
        Employee employee = new Employee();
        employee.setOrders(List.of());

        when(employeeRepository.existsById(1L)).thenReturn(true);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.delete(1L);

        verify(employeeRepository).deleteById(1L);
    }
}
