package com.cafe.cafeapp.service;

import com.cafe.cafeapp.dto.CustomerRequestDto;
import com.cafe.cafeapp.dto.CustomerResponseDto;
import com.cafe.cafeapp.exception.AlreadyExistsException;
import com.cafe.cafeapp.exception.NotFoundException;
import com.cafe.cafeapp.mapper.CustomerMapper;
import com.cafe.cafeapp.model.Customer;
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
        if (customerRepository.existsByFullName(dto.getFullName())) {
            throw new AlreadyExistsException("Customer with this name already exists");
        }
        Customer customer = customerMapper.toEntity(dto);

        Customer saved = customerRepository.save(customer);

        return customerMapper.toResponseDto(saved);
    }

    @Transactional
    public CustomerResponseDto update(Long id, CustomerRequestDto dto) {

        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));

        existing.setFullName(dto.getFullName());
        existing.setPhoneNumber(dto.getPhoneNumber());

        return customerMapper.toResponseDto(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new NotFoundException(id);
        }

        customerRepository.deleteById(id);
    }
}

