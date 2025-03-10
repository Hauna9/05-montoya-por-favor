package com.example.MiniProject1;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import com.example.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest
class MiniProject1ProductServiceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;



    @Test
    void testAddProduct_CorrectUUID() {
        // Arrange
        UUID productId = UUID.randomUUID();
        Product product = new Product(productId, "Valid Product", 20.0);

        // Act
        Product savedProduct = productService.addProduct(product);

        // Assert
        assertNotNull(savedProduct, "Product should be added.");
        assertEquals(productId, savedProduct.getId(), "Product ID should match the provided UUID.");
        assertEquals("Valid Product", savedProduct.getName(), "Product name should be correctly saved.");
        assertEquals(20.0, savedProduct.getPrice(), "Product price should be correctly saved.");
    }



    @Test
    void testAddProduct_WithoutID_ShouldGenerateUUID() {
        // Arrange
        Product productWithoutId = new Product("Generated ID Product", 15.0);

        // Act
        Product savedProduct = productService.addProduct(productWithoutId);

        // Assert
        assertNotNull(savedProduct, "Product should be added.");
        assertNotNull(savedProduct.getId(), "Product ID should be auto-generated.");
        assertEquals("Generated ID Product", savedProduct.getName(), "Product name should be correctly saved.");
        assertEquals(15.0, savedProduct.getPrice(), "Product price should be correctly saved.");
    }

    @Test
    void testAddProduct_WithNullUUID_ShouldGenerateUUID() {
        // Arrange: Create a product with a null ID
        Product product = new Product("Generated Product", 25.0);

        // Act: Add the product
        Product savedProduct = productService.addProduct(product);

        // Assert: The product should have a valid UUID assigned
        assertNotNull(savedProduct.getId(), "Product ID should be auto-generated.");
    }





    @Test
    void testGetProducts_WhenProductsExist() {
        // Arrange
        Product product = new Product("Existing Product", 50.0);
        productService.addProduct(product);

        // Act
        List<Product> products = productService.getProducts();

        // Assert
        assertFalse(products.isEmpty(), "Products should exist in the database.");
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Existing Product")),
                "The added product should be in the retrieved list.");
    }


    @Test
    void testGetProducts_WhenNoProductsExist() {
        // Arrange: Clear all products from the repository
        productRepository.overrideData(new ArrayList<>()); // Reset DB

        // Act
        List<Product> products = productService.getProducts();

        // Assert
        assertNotNull(products, "Returned product list should not be null.");
        assertTrue(products.isEmpty(), "No products should be present in the database.");
    }



    @Test
    void testGetProducts_WithDifferentConstructors() {
        // Arrange
        Product productWithId = new Product(UUID.randomUUID(), "Product With ID", 40.0);
        Product productWithoutId = new Product("Product Without ID", 30.0);

        // Act
        productService.addProduct(productWithId);
        productService.addProduct(productWithoutId);
        List<Product> products = productService.getProducts();

        // Debugging: Print retrieved product names
        products.forEach(p -> System.out.println("Retrieved Product: " + p.getName()));

        // Check if both products exist in the retrieved list
        boolean foundProductWithId = products.stream().anyMatch(p -> p.getName().equals("Product With ID"));
        boolean foundProductWithoutId = products.stream().anyMatch(p -> p.getName().equals("Product Without ID"));

        // Assert
        assertTrue(foundProductWithId, "Product with explicit ID should exist in the database.");
        assertTrue(foundProductWithoutId, "Product without explicit ID should exist in the database.");
    }



    @Test
    void testGetProductById_WhenExists() {
        // Arrange
        Product product = new Product("Product Found", 60.0);
        Product savedProduct = productService.addProduct(product);

        // Act
        Product retrievedProduct = productService.getProductById(savedProduct.getId());

        // Assert
        assertNotNull(retrievedProduct, "Retrieved product should not be null.");
        assertEquals(savedProduct.getId(), retrievedProduct.getId(), "Product ID should match.");
        assertEquals("Product Found", retrievedProduct.getName(), "Product name should match.");
        assertEquals(60.0, retrievedProduct.getPrice(), "Product price should match.");
    }

    @Test
    void testGetProductById_WhenDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        Exception exception = assertThrows(ResponseStatusException.class, () -> productService.getProductById(nonExistentId),
                "Expected ResponseStatusException when product is not found.");

        assertTrue(exception.getMessage().contains("Product not found"),
                "Exception message should indicate that the product was not found.");
    }

    @Test
    void testGetProductById_WhenInvalidUUID() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            UUID invalidUuid = UUID.fromString("invalid-uuid"); // This should throw an exception
            productService.getProductById(invalidUuid);
        });

        assertTrue(exception.getMessage().contains("Invalid UUID"), "Exception message should indicate UUID format issue.");
    }


    @Test
    void testUpdateProduct_WhenExists() {
        // Arrange: Add a product
        Product product = new Product("Old Name", 50.0);
        Product savedProduct = productService.addProduct(product);

        // Act: Update product
        Product updatedProduct = productService.updateProduct(savedProduct.getId(), "Updated Name", 70.0);

        // Assert
        assertNotNull(updatedProduct, "Updated product should not be null.");
        assertEquals("Updated Name", updatedProduct.getName(), "Product name should be updated.");
        assertEquals(70.0, updatedProduct.getPrice(), "Product price should be updated.");
    }
    @Test
    void testUpdateProduct_WhenDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                        productService.updateProduct(nonExistentId, "Updated Name", 70.0),
                "Expected IllegalArgumentException when updating a non-existent product."
        );

        assertTrue(exception.getMessage().contains("Product not found"),
                "Exception message should indicate that the product was not found.");
    }

    // @Test
//    void testUpdateProduct_WhenDoesNotExist() {
//        // Arrange
//        UUID nonExistentId = UUID.randomUUID();
//
//        // Act & Assert
//        Exception exception = assertThrows(ResponseStatusException.class, () ->
//                        productService.updateProduct(nonExistentId, "Updated Name", 70.0),
//                "Expected ResponseStatusException when updating a non-existent product."
//        );
//
//        assertTrue(exception.getMessage().contains("Product not found"),
//                "Exception message should indicate that the product was not found.");
//    }

    //FIXME unify all to throw illegal..? above n below
    @Test
    void testUpdateProduct_WithNegativePrice() {
        // Arrange: Add a product
        Product product = new Product("Negative Price Product", 50.0);
        Product savedProduct = productService.addProduct(product);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                        productService.updateProduct(savedProduct.getId(), "Still Negative", -10.0),
                "Expected IllegalArgumentException when setting a negative price."
        );

        assertTrue(exception.getMessage().contains("Price cannot be negative"),
                "Exception message should indicate invalid pricing.");
    }



    @Test
    void testApplyDiscount_SingleProduct() {
        // Arrange: Add a product
        Product product = new Product("Discounted Product", 100.0);
        Product savedProduct = productService.addProduct(product);

        // Act: Apply a 10% discount
        List<UUID> productIds = List.of(savedProduct.getId());
        productService.applyDiscount(10.0, productIds);

        // Assert: Verify price reduction
        Product updatedProduct = productService.getProductById(savedProduct.getId());
        assertEquals(90.0, updatedProduct.getPrice(), "Price should be reduced by discount.");
    }

    @Test
    void testApplyDiscount_MultipleProducts() {
        // Arrange: Add multiple products
        Product productA = new Product("Product A", 200.0);
        Product productB = new Product("Product B", 300.0);

        Product savedProductA = productService.addProduct(productA);
        Product savedProductB = productService.addProduct(productB);

        // Act: Apply a 20% discount to both products
        List<UUID> productIds = List.of(savedProductA.getId(), savedProductB.getId());
        productService.applyDiscount(20.0, productIds);

        // Assert: Verify correct discount application
        Product updatedProductA = productService.getProductById(savedProductA.getId());
        Product updatedProductB = productService.getProductById(savedProductB.getId());

        assertEquals(160.0, updatedProductA.getPrice(), "Product A price should be discounted.");
        assertEquals(240.0, updatedProductB.getPrice(), "Product B price should be discounted.");
    }

    @Test
    void testApplyDiscount_NoExistingProducts() {
        // Arrange: Create a list of non-existent product IDs
        List<UUID> nonExistentProductIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        // Act & Assert: Expect an exception
        Exception exception = assertThrows(ResponseStatusException.class, () ->
                        productService.applyDiscount(10.0, nonExistentProductIds),
                "Expected ResponseStatusException when applying discount to non-existent products."
        );

        // Verify exception message
        assertTrue(exception.getMessage().contains("No products found for given IDs"),
                "Expected error message to indicate no products found.");
    }




    @Test
    void testDeleteProduct_WhenExists() {
        // Arrange: Add a product
        Product product = new Product("Product To Delete", 30.0);
        Product savedProduct = productService.addProduct(product);

        // Act: Delete the product
        productService.deleteProductById(savedProduct.getId());

        // Assert: Verify that the product no longer exists
        Exception exception = assertThrows(ResponseStatusException.class, () ->
                        productService.getProductById(savedProduct.getId()),
                "Expected ResponseStatusException when retrieving a deleted product.");

        assertTrue(exception.getMessage().contains("Product not found"),
                "Expected error message to indicate product not found.");
    }
    @Test
    void testDeleteProduct_WhenDoesNotExist() {
        // Arrange: Generate a random product ID that doesn't exist
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert: Expect an exception when trying to delete a non-existent product
        Exception exception = assertThrows(ResponseStatusException.class, () ->
                        productService.deleteProductById(nonExistentId),
                "Expected ResponseStatusException when deleting a non-existent product.");

        assertTrue(exception.getMessage().contains("Product not found"),
                "Expected error message to indicate product not found.");
    }
    @Test
    void testAddAndDeleteMultipleProducts() {
        // Arrange: Add two products
        Product product1 = new Product("Product X", 50.0);
        Product product2 = new Product("Product Y", 60.0);

        Product savedProduct1 = productService.addProduct(product1);
        Product savedProduct2 = productService.addProduct(product2);

        // Act: Delete both products
        productService.deleteProductById(savedProduct1.getId());
        productService.deleteProductById(savedProduct2.getId());

        // Assert: Ensure both products are deleted
        assertThrows(ResponseStatusException.class, () ->
                        productService.getProductById(savedProduct1.getId()),
                "Product X should be deleted.");

        assertThrows(ResponseStatusException.class, () ->
                        productService.getProductById(savedProduct2.getId()),
                "Product Y should be deleted.");
    }

}
