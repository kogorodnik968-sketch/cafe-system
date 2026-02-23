package com.cafe.cafe_app.controller;

import com.cafe.cafe_app.dto.ProductDto;
import com.cafe.cafe_app.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
   private final ProductService service;

   @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id)
   {
       return service.getById(id);
   }

   @GetMapping
    public List<ProductDto> getProductsByName(@RequestParam(required = false) String name) {
       if (name != null){
           return service.getByName(name);
       }
       return service.getAll();
   }

}
