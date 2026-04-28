package com.demo;

import com.demo.entity.Product;
import com.demo.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDemoApplication.class, args);
    }

    // Seed the H2 database with sample products so GET /api/products works immediately
    @Bean
    CommandLineRunner seedProducts(ProductRepository repo) {
        return args -> {
            repo.save(new Product(null, "Laptop",      "15-inch dev laptop",  1299.99));
            repo.save(new Product(null, "Mechanical Keyboard", "TKL RGB keyboard", 89.99));
            repo.save(new Product(null, "Monitor",     "27-inch 4K display",  499.99));
        };
    }
}
