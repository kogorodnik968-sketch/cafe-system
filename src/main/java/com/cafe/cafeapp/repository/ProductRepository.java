package com.cafe.cafeapp.repository;

import com.cafe.cafeapp.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepository {

    private final List<Product> products = List.of(
            new Product(1L, "Coffee", 5.2, 7.0),
            new Product(2L, "Tea", 3.0, 4.1),
            new Product(3L, "Lemonade", 4.7, 5.2),
            new Product(4L, "Cheesecake", 6.0, 8.4),
            new Product(5L, "Pie", 5.2, 6.3),
            new Product(6L, "Muffin", 3.4, 5.0)
    );

    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    public Product findById(Long id) {
        for (Product product:products) {
            if (product.getIdPr().equals(id)) {
                return product;
            }
        }
        return null;
    }

    public List<Product> findByName(String name) {
        List<Product> findProduct = new ArrayList<>();

        for (Product product:products) {
            if (product.getNamePr().equals(name)) {
                findProduct.add(product);
            }
        }
        return findProduct;
    }
}
