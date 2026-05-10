package com.cafe.cafeapp.model;

import com.cafe.cafeapp.enums.EmployeeRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    @Enumerated(EnumType.STRING)
    private EmployeeRole role;
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<Order> orders;
    private String imageUrl;

    @Column(unique = true)
    private String email;
    private String password;
}
