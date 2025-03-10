package com.example.repository;


import com.example.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class ProductRepository extends MainRepository<Product> {


    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/products.json";
    }

    @Override
    protected Class<Product[]> getArrayType() {
        return Product[].class;
    }

    public Product addProduct(Product product) {
        ArrayList<Product> products = getProducts();
        products.add(product);
        saveAll(products);
        return product;
    }

    public ArrayList<Product> getProducts() {
        return findAll();
    }

    public Product getProductById(UUID productId) {
        return getProducts().stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    public Product updateProduct(UUID productId, String newName, double newPrice) {
        ArrayList<Product> products = getProducts();
        for (Product product : products) {
            if (product.getId().equals(productId)) {
                product.setName(newName);
                if(newPrice < 0){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price cannot be negative");
                }
                product.setPrice(newPrice);
                saveAll(products);
                return product;
            }
        }
        return null;
    }

    public void applyDiscount(double discount, List<UUID> productIds) { //FIXME shouldnt the logic be in the service class and we just save here?
        ArrayList<Product> products = getProducts();

        // Filter products that match the given IDs
        List<Product> productsToUpdate = products.stream()
                .filter(product -> productIds.contains(product.getId()))
                .toList();

        // If no matching products exist, throw 404
        if (productsToUpdate.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found for given IDs");
        }

        // Apply discount
        for (Product product : productsToUpdate) {
            double newPrice = product.getPrice() - (product.getPrice() * (discount / 100));
            product.setPrice(newPrice);
        }

        saveAll(products); // Save updated products
    }

    public void deleteProductById(UUID productId) {
        ArrayList<Product> products = getProducts();
        products.removeIf(product -> product.getId().equals(productId));
        saveAll(products);
    }
}
