package com.cafe.cafeapp.controller;

import com.cafe.cafeapp.dto.AuthResponseDto;
import com.cafe.cafeapp.dto.LoginRequestDto;
import com.cafe.cafeapp.dto.RegisterRequestDto;
import com.cafe.cafeapp.model.Customer;
import com.cafe.cafeapp.model.Employee;
import com.cafe.cafeapp.repository.CustomerRepository;
import com.cafe.cafeapp.repository.EmployeeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto dto) {

        Customer customer = customerRepository.findByEmail(dto.getEmail());
        if (customer != null && customer.getPassword().equals(dto.getPassword())) {
            return ResponseEntity.ok(new AuthResponseDto(customer.getId(), "CUSTOMER", customer.getFirstName()));
        }

        Employee employee = employeeRepository.findByEmail(dto.getEmail());
        if (employee != null && employee.getPassword().equals(dto.getPassword())) {
            return ResponseEntity.ok(new AuthResponseDto(employee.getId(), "EMPLOYEE", employee.getFirstName()));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный email или пароль");
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid RegisterRequestDto dto) {
        if (customerRepository.findByEmail(dto.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email уже занят");
        }
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPassword(dto.getPassword());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customerRepository.save(customer);
        return ResponseEntity.ok(new AuthResponseDto(customer.getId(), "CUSTOMER", customer.getFirstName()));
    }
}