package com.cafe.cafeapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long idPr;
    private String namePr;
    private double costPrice;
    private double finalPr;
}


