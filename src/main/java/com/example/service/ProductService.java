package com.example.service;
import com.scalable.app.model.Product;
import com.scalable.app.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class ProductService extends MainService<Product> {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        super(productRepository);
        this.productRepository = productRepository;
    }

    public Product addProduct(Product product) {
        return productRepository.addProduct(product);
    }

    public ArrayList<Product> getProducts() {
        return productRepository.getProducts();
    }

    public Product getProductById(UUID productId) {
        return productRepository.getProductById(productId);
    }

    public Product updateProduct(UUID productId, String newName, double newPrice) {
        return productRepository.updateProduct(productId, newName, newPrice);
    }

    public void applyDiscount(double discount, List<UUID> productIds) {
        productRepository.applyDiscount(discount, productIds);
    }

    public void deleteProductById(UUID productId) {
        productRepository.deleteProductById(productId);
    }
}
