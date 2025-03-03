package com.example.repository;


import com.scalable.app.model.Product;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class ProductRepository extends MainRepository<Product> {

    public ProductRepository() {
        super("products.json", Product.class);
    }

    public Product addProduct(Product product) {
        List<Product> products = getProducts();
        products.add(product);
        saveData(products);
        return product;
    }

    public ArrayList<Product> getProducts() {
        return loadData();
    }

    public Product getProductById(UUID productId) {
        return getProducts().stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    public Product updateProduct(UUID productId, String newName, double newPrice) {
        List<Product> products = getProducts();
        for (Product product : products) {
            if (product.getId().equals(productId)) {
                product.setName(newName);
                product.setPrice(newPrice);
                saveData(products);
                return product;
            }
        }
        return null;
    }

    public void applyDiscount(double discount, List<UUID> productIds) {
        List<Product> products = getProducts();
        for (Product product : products) {
            if (productIds.contains(product.getId())) {
                double newPrice = product.getPrice() * (1 - (discount / 100));
                product.setPrice(newPrice);
            }
        }
        saveData(products);
    }

    public void deleteProductById(UUID productId) {
        List<Product> products = getProducts();
        products.removeIf(product -> product.getId().equals(productId));
        saveData(products);
    }
}
