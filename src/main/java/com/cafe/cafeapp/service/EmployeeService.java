package com.cafe.cafeapp.service;

import com.cafe.cafeapp.dto.EmployeeRequestDto;
import com.cafe.cafeapp.dto.EmployeeResponseDto;
import com.cafe.cafeapp.exception.AlreadyExistsException;
import com.cafe.cafeapp.exception.BusinessException;
import com.cafe.cafeapp.exception.NotFoundException;
import com.cafe.cafeapp.mapper.EmployeeMapper;
import com.cafe.cafeapp.model.Employee;
import com.cafe.cafeapp.model.Order;
import com.cafe.cafeapp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getAll() {
        return employeeMapper.toDtoList(employeeRepository.findAll());
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDto getById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));

        return employeeMapper.toResponseDto(employee);
    }

    @Transactional
    public EmployeeResponseDto create(EmployeeRequestDto dto) {
        if (employeeRepository.existsByFirstName(dto.getFirstName()) &&
            employeeRepository.existsByLastName(dto.getLastName()) &&
            employeeRepository.existsByMiddleName(dto.getMiddleName())) {
            throw new AlreadyExistsException("Employee with this name already exists");
        }
        Employee employee = employeeMapper.toEntity(dto);

        Employee saved = employeeRepository.save(employee);

        return employeeMapper.toResponseDto(saved);
    }

    @Transactional
    public EmployeeResponseDto update(Long id, EmployeeRequestDto dto) {

        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setMiddleName(dto.getMiddleName());
        existing.setRole(dto.getRole());

        return employeeMapper.toResponseDto(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new NotFoundException(id);
        }

        Employee employee = employeeRepository.findById(id).get();
        List<Order> orders = employee.getOrders();

        if (!orders.isEmpty()) {
            throw new BusinessException(id);
        }

        employeeRepository.deleteById(id);
    }
}

