package com.demo.transformer;

import com.demo.dto.ProductDTO;
import com.demo.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductTransformer {

    public ProductDTO toDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }

    // id is null on create — DB auto-generates it via IDENTITY strategy
    public Product toEntity(ProductDTO dto) {
        return new Product(null, dto.name(), dto.description(), dto.price());
    }
}
