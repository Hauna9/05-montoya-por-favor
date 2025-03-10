//package com.example.MiniProject1;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import java.util.*;
//
//import com.example.repository.ProductRepository;
//import com.example.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import com.example.model.Product;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//@WebMvcTest
//class MiniProject1ProductServiceTests {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    ProductRepository productRepository;
//
//
//
//    @Test
//    void testAddProduct_CorrectUUID() throws Exception {
//        Product product = new Product(UUID.randomUUID(), "Valid Product", 20.0);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/product/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(product)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/product/"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//
//        List<Product> products = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
//        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Valid Product")), "Product should exist.");
//    }
//
//    @Test
//    void testAddProduct_WithoutID_ShouldGenerateUUID() throws Exception {
//        Product productWithoutId = new Product("Generated ID Product", 15.0);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/product/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(productWithoutId)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/product/"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//
//        List<Product> products = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
//        Product savedProduct = products.stream().filter(p -> p.getName().equals("Generated ID Product")).findFirst().orElse(null);
//
//        assertNotNull(savedProduct, "Product should be saved.");
//        assertNotNull(savedProduct.getId(), "Product ID should be auto-generated.");
//    }
//
//    @Test
//    void testAddProduct_WithInvalidUUID() throws Exception {
//        String invalidProductJson = "{ \"id\": \"invalid-uuid\", \"name\": \"Invalid UUID Product\", \"price\": 25.0 }";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/product/")
//                        .contentType("application/json")
//                        .content(invalidProductJson))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());
//    }
//
//
//
//    @Test
//    void testGetProducts_WhenProductsExist() throws Exception {
//        Product product = new Product("Existing Product", 50.0);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/product/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(product)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/product/"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//
//        List<Product> products = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
//        assertFalse(products.isEmpty(), "Products should exist.");
//    }
//
//    @Test
//    void testGetProducts_WhenNoProductsExist() throws Exception {
//        // Clear all products from the database before running the test
//        productRepository.overrideData(new ArrayList<>()); // Assuming `overrideData` saves an empty list to reset DB
//
//        // Retrieve products after clearing the database
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/product/"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//
//        // Deserialize response
//        List<Product> products = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
//
//        // Ensure no products are returned
//        assertTrue(products.isEmpty(), "No products should be present.");
//    }
//
//
//    @Test
//    void testGetProducts_WithDifferentConstructors() throws Exception {
//        Product productWithId = new Product(UUID.randomUUID(), "Product With ID", 40.0);
//        Product productWithoutId = new Product("Product Without ID", 30.0);
//
//        // Add product with explicitly provided ID
//        mockMvc.perform(MockMvcRequestBuilders.post("/product/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(productWithId)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        // Add product without explicitly providing ID (UUID should be generated)
//        mockMvc.perform(MockMvcRequestBuilders.post("/product/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(productWithoutId)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        // Retrieve all products
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/product/"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//
//        // Deserialize response
//        List<Product> products = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
//
//        // Debugging: Print product names
//        for (Product product : products) {
//            System.out.println("Product in DB: " + product.getName());
//        }
//
//        // Check if both specific products exist in the retrieved list
//        boolean foundProductWithId = products.stream().anyMatch(p -> p.getName().equals("Product With ID"));
//        boolean foundProductWithoutId = products.stream().anyMatch(p -> p.getName().equals("Product Without ID"));
//
//        // Assertions
//        assertTrue(foundProductWithId, "Product with explicit ID should exist.");
//        assertTrue(foundProductWithoutId, "Product without explicit ID should exist.");
//    }
//
//
//
//    @Test
//    void testGetProductById_WhenExists() throws Exception {
//        Product product = new Product(UUID.randomUUID(), "Product Found", 60.0);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/product/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(product)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/product/{productId}", product.getId()))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//
//        Product retrievedProduct = objectMapper.readValue(result.getResponse().getContentAsString(), Product.class);
//        assertEquals(product.getId(), retrievedProduct.getId(), "Product ID should match.");
//    }
//
//    @Test
//    void testGetProductById_WhenDoesNotExist() throws Exception {
//        UUID nonExistentId = UUID.randomUUID();
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/product/{productId}", nonExistentId))
//                .andExpect(MockMvcResultMatchers.status().isNotFound());
//    }
//
//    @Test
//    void testGetProductById_WhenInvalidUUID() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/product/{productId}", "invalid-uuid"))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());
//    }
//
//    @Test
//    void testUpdateProduct_WhenExists() throws Exception {
//        // Add a product
//        Product product = new Product(UUID.randomUUID(), "Old Name", 50.0);
//        mockMvc.perform(MockMvcRequestBuilders.post("/product/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(product)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        // Update product
//        Map<String, Object> updateBody = new HashMap<>();
//        updateBody.put("newName", "Updated Name");
//        updateBody.put("newPrice", 70.0);
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/product/update/{id}", product.getId())
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(updateBody)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//
//        Product updatedProduct = objectMapper.readValue(result.getResponse().getContentAsString(), Product.class);
//
//        assertEquals("Updated Name", updatedProduct.getName(), "Product name should be updated.");
//        assertEquals(70.0, updatedProduct.getPrice(), "Product price should be updated.");
//    }
//
//    @Test
//    void testUpdateProduct_WhenDoesNotExist() throws Exception {
//        UUID nonExistentId = UUID.randomUUID();
//
//        Map<String, Object> updateBody = new HashMap<>();
//        updateBody.put("newName", "Updated Name");
//        updateBody.put("newPrice", 70.0);
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/product/update/{id}", nonExistentId)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(updateBody)))
//                .andExpect(MockMvcResultMatchers.status().isNotFound());
//    }
//
//    @Test
//    void testUpdateProduct_WithNegativePrice() throws Exception {
//        Product product = new Product(UUID.randomUUID(), "Negative Price Product", 50.0);
//        mockMvc.perform(MockMvcRequestBuilders.post("/product/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(product)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        Map<String, Object> updateBody = new HashMap<>();
//        updateBody.put("newName", "Still Negative");
//        updateBody.put("newPrice", -10.0);
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/product/update/{id}", product.getId())
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(updateBody)))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());
//    }
//
//
//
//    @Test
//    void testApplyDiscount_SingleProduct() throws Exception {
//        Product product = new Product(UUID.randomUUID(), "Discounted Product", 100.0);
//        mockMvc.perform(MockMvcRequestBuilders.post("/product/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(product)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        List<UUID> productIds = List.of(product.getId());
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/product/applyDiscount")
//                        .contentType("application/json")
//                        .param("discount", "10.0")
//                        .content(objectMapper.writeValueAsString(productIds)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/product/{productId}", product.getId()))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//
//        Product updatedProduct = objectMapper.readValue(result.getResponse().getContentAsString(), Product.class);
//        assertEquals(90.0, updatedProduct.getPrice(), "Price should be reduced by discount.");
//    }
//
//    @Test
//    void testApplyDiscount_MultipleProducts() throws Exception {
//        // Add products
//        Product productA = new Product(UUID.randomUUID(), "Product A", 200.0);
//        Product productB = new Product(UUID.randomUUID(), "Product B", 300.0);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/product/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(productA)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/product/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(productB)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        // Apply 20% discount
//        List<UUID> productIds = List.of(productA.getId(), productB.getId());
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/product/applyDiscount")
//                        .param("discount", "20.0")  // Ensure this matches expected discount
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(productIds)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        // Retrieve products again to check new prices
//        MvcResult productAResult = mockMvc.perform(MockMvcRequestBuilders.get("/product/{id}", productA.getId()))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//
//        MvcResult productBResult = mockMvc.perform(MockMvcRequestBuilders.get("/product/{id}", productB.getId()))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//
//        Product updatedProductA = objectMapper.readValue(productAResult.getResponse().getContentAsString(), Product.class);
//        Product updatedProductB = objectMapper.readValue(productBResult.getResponse().getContentAsString(), Product.class);
//
//        // Correct expected values for 20% discount
//        assertEquals(160.0, updatedProductA.getPrice(), "Product A price should be discounted.");
//        assertEquals(240.0, updatedProductB.getPrice(), "Product B price should be discounted.");
//    }
//
//
//    @Test
//    void testApplyDiscount_NoExistingProducts() throws Exception {
//        // Create a list of non-existent product IDs
//        List<UUID> nonExistentProductIds = List.of(UUID.randomUUID(), UUID.randomUUID());
//
//        // Apply discount to non-existing products
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/product/applyDiscount")
//                        .param("discount", "10.0")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(nonExistentProductIds)))
//                .andExpect(MockMvcResultMatchers.status().isNotFound()) // Expect 404
//                .andReturn();
//
//        // Extract actual error message
//        String actualErrorMessage = result.getResponse().getErrorMessage(); // Get the error message
//
//        // Verify error message
//        assertEquals("No products found for given IDs", actualErrorMessage, "Expected error message to be returned");
//    }
//
//
//
//
//
//    @Test
//    void testDeleteProduct_WhenExists() throws Exception {
//        Product product = new Product(UUID.randomUUID(), "Product To Delete", 30.0);
//        mockMvc.perform(MockMvcRequestBuilders.post("/product/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(product)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/product/delete/{id}", product.getId()))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/product/{productId}", product.getId()))
//                .andExpect(MockMvcResultMatchers.status().isNotFound());
//    }
//
//    @Test
//    void testDeleteProduct_WhenDoesNotExist() throws Exception {
//        UUID nonExistentId = UUID.randomUUID();
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/product/delete/{id}", nonExistentId))
//                .andExpect(MockMvcResultMatchers.status().isNotFound());
//    }
//
//    @Test
//    void testAddAndDeleteMultipleProducts() throws Exception {
//        Product product1 = new Product(UUID.randomUUID(), "Product X", 50.0);
//        Product product2 = new Product(UUID.randomUUID(), "Product Y", 60.0);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/product/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(product1)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/product/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(product2)))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/product/delete/{id}", product1.getId()))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/product/delete/{id}", product2.getId()))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//}
