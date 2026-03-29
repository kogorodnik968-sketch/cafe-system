package com.cafe.cafeapp.repository;

import com.cafe.cafeapp.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph( attributePaths = {"customer", "orderItems",
        "orderItems.product"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Order> findAll();

    @Query("""
       SELECT DISTINCT o
       FROM Order o
       JOIN FETCH o.customer       
       JOIN FETCH o.orderItems oi
       JOIN FETCH oi.product p
       WHERE p.id = :productId
         AND o.totalPrice >= :minTotal
        """)
    Page<Order> findOrdersByProductAndMinTotal(
            @Param("productId") Long productId,
            @Param("minTotal") BigDecimal minTotal,
            Pageable pageable
    );

    @Query(value = """
        SELECT DISTINCT o.*
        FROM orders o
        JOIN order_item oi ON o.id = oi.order_id
        JOIN product p ON oi.product_id = p.id
        WHERE p.id = :productId
        AND o.total_price > :minTotal
        """,
            countQuery = """
        SELECT COUNT(DISTINCT o.id)
        FROM orders o
        JOIN order_item oi ON o.id = oi.order_id
        JOIN product p ON oi.product_id = p.id
        WHERE p.id = :productId
        AND o.total_price >= :minTotal
        """, nativeQuery = true)
    Page<Order> findOrdersByProductAndMinTotalNative(
            @Param("productId") Long productId,
            @Param("minTotal") BigDecimal minTotal,
            Pageable pageable
    );
}
