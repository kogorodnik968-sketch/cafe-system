package com.cafe.cafe_app.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long idPr;
    private String namePr;
    private double costPrice;
    private double finalPr;
}


