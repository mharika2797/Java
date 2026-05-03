package com.demo.service;

import com.demo.dto.ProductDTO;
import com.demo.entity.Product;
import com.demo.exception.ResourceNotFoundException;
import com.demo.repository.ProductRepository;
import com.demo.transformer.ProductTransformer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ─────────────────────────────────────────────────────────────────────────────
//  LAYER 2 — SERVICE  (business logic lives here, never in the controller)
//
//  Rules enforced here:
//    • Price cannot be negative          (validated at DTO level too)
//    • A product must exist before update/delete  (throws 404 via exception)
//    • Entity ↔ DTO conversion stays in this layer — the controller never
//      touches the Product entity directly
//
//  @Transactional:
//    • Read methods use readOnly=true  → DB skips dirty-checking, faster reads
//    • Write methods use the default   → full transaction, auto-rollback on error
// ─────────────────────────────────────────────────────────────────────────────
@Service
public class ProductService {

    private final ProductRepository repo;
    private final ProductTransformer transformer;

    public ProductService(ProductRepository repo, ProductTransformer transformer) {
        this.repo = repo;
        this.transformer = transformer;
    }

    // ── READ operations ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ProductDTO> getAll() {
        // repo.findAll() → SELECT * FROM products
        return repo.findAll().stream()
                .map(transformer::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDTO getById(Long id) {
        // repo.findById() → SELECT * FROM products WHERE id = ?
        return repo.findById(id)
                .map(transformer::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> searchByName(String keyword) {
        // repo.findByNameContainingIgnoreCase() → SELECT * FROM products WHERE LOWER(name) LIKE ?
        return repo.findByNameContainingIgnoreCase(keyword).stream()
                .map(transformer::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findCheaperThan(double maxPrice) {
        // Business rule: maxPrice must be positive
        if (maxPrice <= 0) {
            throw new IllegalArgumentException("maxPrice must be greater than zero");
        }
        // repo.findByPriceLessThan() → SELECT * FROM products WHERE price < ?
        return repo.findByPriceLessThan(maxPrice).stream()
                .map(transformer::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findInPriceRange(double min, double max) {
        // Business rule: range must be valid
        if (min < 0 || max < 0 || min > max) {
            throw new IllegalArgumentException("Invalid price range: min=" + min + ", max=" + max);
        }
        // @Query JPQL → SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max ORDER BY p.price ASC
        return repo.findInPriceRange(min, max).stream()
                .map(transformer::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllSortedByPrice() {
        // Native SQL → SELECT * FROM products ORDER BY price ASC
        return repo.findAllSortedByPrice().stream()
                .map(transformer::toDTO)
                .toList();
    }

    // ── WRITE operations ──────────────────────────────────────────────────────

    @Transactional
    public ProductDTO create(ProductDTO dto) {
        // Map DTO → Entity, then repo.save() → INSERT INTO products (name, description, price) VALUES (...)
        Product p = transformer.toEntity(dto);
        return transformer.toDTO(repo.save(p));
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        // 1. Verify the product exists — throws 404 if not
        Product p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        // 2. Apply changes to the managed entity
        p.setName(dto.name());
        p.setDescription(dto.description());
        p.setPrice(dto.price());

        // 3. repo.save() on a managed entity → UPDATE products SET ... WHERE id = ?
        return transformer.toDTO(repo.save(p));
    }

    @Transactional
    public void delete(Long id) {
        // Verify exists before deleting so we return a meaningful 404
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        // repo.deleteById() → DELETE FROM products WHERE id = ?
        repo.deleteById(id);
    }

}
