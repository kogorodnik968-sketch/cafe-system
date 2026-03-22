package com.cafe.cafeapp.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequestDto {
    private String firstName;
    private String lastName;
    private String middleName;
    @Size(min = 13, max = 13, message = "Телефон должен бфть в формате +XXXXXXXXXXXX")
    @Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Некорректный формат телефона")
    private String phoneNumber;
}
