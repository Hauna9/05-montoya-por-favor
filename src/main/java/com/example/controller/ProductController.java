package com.example.controller;


import com.example.model.Product;
import com.example.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @GetMapping("/")
    public ArrayList<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{productId}")
    public Product getProductById(@PathVariable UUID productId) {
        return productService.getProductById(productId);
    }

    @PutMapping("/update/{productId}")
    public Product updateProduct(@PathVariable UUID productId, @RequestBody Product updatedProduct) {
        return productService.updateProduct(productId, updatedProduct.getName(), updatedProduct.getPrice());
    }

    @PutMapping("/applyDiscount")
    public void applyDiscount(@RequestParam double discount, @RequestBody List<UUID> productIds) {
        productService.applyDiscount(discount, productIds);
    }

    @DeleteMapping("/delete/{productId}")
    public void deleteProductById(@PathVariable UUID productId) {
        productService.deleteProductById(productId);
    }
}
