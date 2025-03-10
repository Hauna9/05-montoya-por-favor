package com.example.service;
import com.example.model.Cart;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

        Product product=  productRepository.getProductById(productId);
        if(product == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return product;
    }

    public Product updateProduct(UUID productId, String newName, double newPrice) {
        Product product = productRepository.getProductById(productId);
        if (product== null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return productRepository.updateProduct(productId, newName, newPrice);
    }

    public void applyDiscount(double discount, List<UUID> productIds) {
        productRepository.applyDiscount(discount, productIds);
    }

    public void deleteProductById(UUID productId) {
        Product product = productRepository.getProductById(productId);
        if (product!= null) {
            productRepository.deleteProductById(productId);
        }

        else
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }
}
