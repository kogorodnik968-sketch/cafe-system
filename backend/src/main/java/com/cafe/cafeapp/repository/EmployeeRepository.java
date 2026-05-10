package com.cafe.cafeapp.repository;

import com.cafe.cafeapp.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByFirstName(String firstName);
    boolean existsByLastName(String lastName);
    boolean existsByMiddleName (String middleName);

    Employee findByEmail(String email);
}
