package com.cafe.cafeapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;
}
