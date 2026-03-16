package com.cafe.cafeapp.dto;

import com.cafe.cafeapp.enums.EmployeeRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestDto {
    private String fullName;
    private EmployeeRole role;
}
