package com.cafe.cafeapp.repository;

import com.cafe.cafeapp.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByFirstName(String firstName);
    boolean existsByLastName(String lastName);
    boolean existsByMiddleName(String middleName);

}
