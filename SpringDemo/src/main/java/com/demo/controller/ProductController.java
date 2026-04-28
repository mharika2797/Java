package com.demo.controller;

import com.demo.dto.ProductDTO;
import com.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ─────────────────────────────────────────────────────────────────────────────
//  LAYER 1 — CONTROLLER  (HTTP boundary — no business logic here)
//
//  Responsibility:
//    • Parse HTTP request (path vars, query params, request body)
//    • Validate the incoming DTO with @Valid before it reaches the service
//    • Delegate everything else to ProductService
//    • Return the right HTTP status code
//
//  Flow:
//    HTTP Request
//      → ProductController   (validate input, route to service)
//      → ProductService      (business rules, @Transactional)
//      → ProductRepository   (SQL via Spring Data JPA / H2)
//      → HTTP Response
// ─────────────────────────────────────────────────────────────────────────────
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // ── Basic CRUD ────────────────────────────────────────────────────────────

    // GET /api/products
    // → service.getAll() → repo.findAll() → SELECT * FROM products
    @GetMapping
    public List<ProductDTO> getAll() {
        return service.getAll();
    }

    // GET /api/products/1
    // → service.getById(1) → repo.findById(1) → SELECT * FROM products WHERE id = 1
    @GetMapping("/{id}")
    public ProductDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // POST /api/products   body: { "name": "Laptop", "description": "...", "price": 999.99 }
    // @Valid triggers bean validation on ProductDTO before the method body runs
    // → service.create() → repo.save() → INSERT INTO products ...
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO create(@Valid @RequestBody ProductDTO dto) {
        return service.create(dto);
    }

    // PUT /api/products/1   body: { "name": "Laptop Pro", "description": "...", "price": 1299.99 }
    // → service.update() → repo.findById() + repo.save() → UPDATE products SET ... WHERE id = 1
    @PutMapping("/{id}")
    public ProductDTO update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        return service.update(id, dto);
    }

    // DELETE /api/products/1
    // → service.delete() → repo.deleteById(1) → DELETE FROM products WHERE id = 1
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // ── Query endpoints ───────────────────────────────────────────────────────

    // GET /api/products/search?keyword=laptop
    // → service.searchByName() → repo.findByNameContainingIgnoreCase()
    //   → SELECT * FROM products WHERE LOWER(name) LIKE '%laptop%'
    @GetMapping("/search")
    public List<ProductDTO> search(@RequestParam String keyword) {
        return service.searchByName(keyword);
    }

    // GET /api/products/cheaper-than?maxPrice=500
    // → service.findCheaperThan() → repo.findByPriceLessThan()
    //   → SELECT * FROM products WHERE price < 500
    @GetMapping("/cheaper-than")
    public List<ProductDTO> cheaperThan(@RequestParam double maxPrice) {
        return service.findCheaperThan(maxPrice);
    }

    // GET /api/products/price-range?min=100&max=500
    // → service.findInPriceRange() → repo.findInPriceRange() (JPQL @Query)
    //   → SELECT p FROM Product p WHERE p.price BETWEEN 100 AND 500 ORDER BY p.price ASC
    @GetMapping("/price-range")
    public List<ProductDTO> priceRange(@RequestParam double min, @RequestParam double max) {
        return service.findInPriceRange(min, max);
    }

    // GET /api/products/sorted-by-price
    // → service.getAllSortedByPrice() → repo.findAllSortedByPrice() (native SQL @Query)
    //   → SELECT * FROM products ORDER BY price ASC
    @GetMapping("/sorted-by-price")
    public List<ProductDTO> sortedByPrice() {
        return service.getAllSortedByPrice();
    }
}
