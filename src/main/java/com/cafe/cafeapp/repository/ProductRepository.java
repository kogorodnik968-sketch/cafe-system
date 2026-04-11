package com.cafe.cafeapp.repository;

import com.cafe.cafeapp.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"category", "tag", "ingredients"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Product> findAll();

    @EntityGraph(attributePaths = {"category", "tag", "ingredients"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Product> findByName(String productName);

    boolean existsByName(String name);

    @EntityGraph(attributePaths = {"category", "tag", "ingredients"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Product> findAllByOrderByIdDesc();
}

