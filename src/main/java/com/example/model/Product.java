package com.example.model;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class Product {
    private UUID id;
    private String name;
    private double price;

    // Constructors
    public Product() {}

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
        id=UUID.randomUUID();
    }

    public Product(UUID id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
