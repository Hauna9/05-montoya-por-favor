package com.example.service;
import com.example.model.Cart;
import com.example.model.Product;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class ProductService extends MainService<Product> {

    ProductRepository productRepository;
    @Autowired
    public ProductService(ProductRepository productRepository) {
        super(productRepository);
        this.productRepository = productRepository;
    }



    public Product addProduct(Product product) {
        return productRepository.addProduct(product);
    }

    public ArrayList<Product> getProducts() {
        return productRepository.getProducts(); //FIXME call getAll from MainService?
    }

    public Product getProductById(UUID productId) {
        return productRepository.getProductById(productId);
    }

    public Product updateProduct(UUID productId, String newName, double newPrice) {
        Product product = productRepository.getProductById(productId);
        if (product!= null) {
            return productRepository.updateProduct(productId, newName, newPrice);
        }
        return null;
    }

    public void applyDiscount(double discount, List<UUID> productIds) {
        productRepository.applyDiscount(discount, productIds);
    }

    public void deleteProductById(UUID productId) {
        Product product = productRepository.getProductById(productId);
        if (product!= null) {
            productRepository.deleteProductById(productId);
        }
    }
}
