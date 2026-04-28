package com.demo.repository;

import com.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// JpaRepository<Product, Long> gives findAll / findById / save / deleteById / existsById for free.
// Spring Data generates the SQL from the method name or from @Query at startup — no boilerplate needed.
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ── Derived queries (Spring Data generates SQL from the method name) ──────

    // SELECT * FROM products WHERE LOWER(name) LIKE LOWER('%keyword%')
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // SELECT * FROM products WHERE price < maxPrice
    List<Product> findByPriceLessThan(double maxPrice);

    // ── JPQL queries (object-oriented SQL — uses class/field names, not table/column names) ──

    // Finds products whose price falls within [min, max]
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max ORDER BY p.price ASC")
    List<Product> findInPriceRange(@Param("min") double min, @Param("max") double max);

    // ── Native SQL query (raw SQL sent directly to the database) ─────────────

    // Useful when JPQL can't express the query (e.g. DB-specific functions, complex joins)
    @Query(value = "SELECT * FROM products ORDER BY price ASC", nativeQuery = true)
    List<Product> findAllSortedByPrice();
}
