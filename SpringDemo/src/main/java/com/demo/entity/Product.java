package com.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;

    public Product() {}

    public Product(Long id, String name, String description, double price) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.price       = price;
    }

    public Long   getId()          { return id; }
    public String getName()        { return name; }
    public String getDescription() { return description; }
    public double getPrice()       { return price; }

    public void setId(Long id)                { this.id          = id; }
    public void setName(String name)          { this.name        = name; }
    public void setDescription(String desc)   { this.description = desc; }
    public void setPrice(double price)        { this.price       = price; }
}
