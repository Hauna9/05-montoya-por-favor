package com.example.controller;


import com.example.model.Product;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    ProductService productService;

    @Autowired
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
    public Product updateProduct(@PathVariable UUID productId,  @RequestBody Map<String,Object> updates) {
        String newName = (String) updates.get("newName");
        System.out.println(newName);

        double newPrice = Double.parseDouble(updates.get("newPrice").toString());
        System.out.println(newPrice);
        return productService.updateProduct(productId, newName, newPrice);
    }

    @PutMapping("/applyDiscount")
    public String applyDiscount(@RequestParam double discount, @RequestBody List<UUID> productIds) {
        productService.applyDiscount(discount, productIds);
        return "Discount applied successfully";
    }

    @DeleteMapping("/delete/{productId}")
    public String deleteProductById(@PathVariable UUID productId) {
        productService.deleteProductById(productId);
        return "Product deleted successfully";
    }
}
