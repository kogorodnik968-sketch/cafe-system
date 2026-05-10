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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAll() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponseDto getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        return customerMapper.toResponseDto(customer);
    }

    @Transactional
    public CustomerResponseDto create(CustomerRequestDto dto) {
        if (customerRepository.existsByFirstName(dto.getFirstName()) &&
            customerRepository.existsByLastName(dto.getLastName()) &&
            customerRepository.existsByMiddleName(dto.getMiddleName())) {
            throw new AlreadyExistsException("Customer with this name already exists");
        }
        Customer customer = customerMapper.toEntity(dto);
        customer.setEmail(dto.getEmail());
        customer.setPassword(dto.getPassword());

        Customer saved = customerRepository.save(customer);

        return customerMapper.toResponseDto(saved);
    }

    @Transactional
    public CustomerResponseDto update(Long id, CustomerRequestDto dto) {

        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setMiddleName(dto.getMiddleName());
        existing.setPhoneNumber(dto.getPhoneNumber());
        existing.setImageUrl(dto.getImageUrl());
        existing.setEmail(dto.getEmail());
        existing.setPassword(dto.getPassword());

        return customerMapper.toResponseDto(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new NotFoundException(id);
        }

        Customer customer = customerRepository.findById(id).get();

        List<Order> orders = customer.getOrders();

        if (!orders.isEmpty()) {
            throw new DeleteNotAllowedException(id);
        }
        customerRepository.deleteById(id);
    }
}

