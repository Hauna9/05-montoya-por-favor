//package com.example.MiniProject1;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import com.example.model.Cart;
//import com.example.model.Product;
//import com.example.repository.CartRepository;
//import com.example.service.CartService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//import com.example.model.Order;
//import com.example.model.User;
//import com.example.repository.UserRepository;
//import com.example.service.UserService;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ComponentScan(basePackages = "com.example.*")
//@WebMvcTest
//public class MiniProject1CartServiceTests {
//
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private CartService cartService;
//
//    @Autowired
//    private CartRepository cartRepository;
//
//
//    @BeforeEach
//    void setUp() {
//
//    }
//
//    @Test
//    void testAddCart_Success() throws Exception {
//        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/cart/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(cart)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void testAddCart_WithoutProducts() throws Exception {
//        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/cart/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(cart)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String response = result.getResponse().getContentAsString();
//        assertFalse(response.isEmpty(), "Cart should be added even without products.");
//    }
//
//    @Test
//    void testAddCart_WithProducts() throws Exception {
//        List<Product> products = List.of(new Product(UUID.randomUUID(), "Product A", 50.0));
//        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), products);
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/cart/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(cart)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String response = result.getResponse().getContentAsString();
//        assertFalse(response.isEmpty(), "Cart with products should be added successfully.");
//    }
//
//    @Test
//    void testGetCarts_WhenCartsExist() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/cart/"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("[]"));
//    }
//
//    @Test
//    void testGetCarts_WhenNoCartsExist() throws Exception {
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/cart/"))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String response = result.getResponse().getContentAsString();
//        assertEquals("[]", response, "No carts should exist.");
//    }
//
//    @Test
//    void testGetCarts_MultipleCarts() throws Exception {
//        Cart cart1 = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
//        Cart cart2 = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/cart/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(cart1)))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/cart/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(cart2)))
//                .andExpect(status().isOk());
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/cart/"))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String response = result.getResponse().getContentAsString();
//        assertFalse(response.isEmpty(), "Carts should be returned.");
//    }
//
//    @Test
//    void testGetCartById_WhenExists() throws Exception {
//        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/cart/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(cart)))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/cart/{cartId}", cart.getId()))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void testGetCartById_WhenNotExists() throws Exception {
//        UUID nonExistentCartId = UUID.randomUUID();
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/cart/{cartId}", nonExistentCartId))
//                .andExpect(status().isOk())  // Ensure the API does not throw an error
//                .andExpect(content().string(""));
//    }
//
//    @Test
//    void testGetCartById_WithProducts() throws Exception {
//        List<Product> products = List.of(new Product(UUID.randomUUID(), "Product A", 50.0));
//        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), products);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/cart/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(cart)))
//                .andExpect(status().isOk());
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/cart/{cartId}", cart.getId()))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        Cart retrievedCart = objectMapper.readValue(result.getResponse().getContentAsString(), Cart.class);
//        assertFalse(retrievedCart.getProducts().isEmpty(), "Cart should contain products.");
//    }
//
//
//
//    @Test
//    void testGetCartByUserId_WhenExists() throws Exception {
//        UUID userId = UUID.randomUUID();
//        Cart cart = new Cart(UUID.randomUUID(), userId, new ArrayList<>());
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/cart/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(cart)))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/cart/{userId}", userId))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void testGetCartByUserId_WhenUserHasNoCart() throws Exception {
//        UUID nonExistentUserId = UUID.randomUUID();
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/cart/{userId}", nonExistentUserId))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String response = result.getResponse().getContentAsString();
//        assertTrue(response.isEmpty(), "Expected an empty response, but got: " + response);
//    }
//
//    @Test
//    void testGetCartByUserId_WithProducts() throws Exception {
//        UUID userId = UUID.randomUUID();
//        List<Product> products = List.of(new Product(UUID.randomUUID(), "Product A", 50.0));
//        Cart cart = new Cart(UUID.randomUUID(), userId, products);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/cart/")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(cart)))
//                .andExpect(status().isOk());
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/cart/{userId}", userId))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        Cart retrievedCart = objectMapper.readValue(result.getResponse().getContentAsString(), Cart.class);
//        assertFalse(retrievedCart.getProducts().isEmpty(), "Cart should contain products.");
//    }
//
//
//
//
//
//}