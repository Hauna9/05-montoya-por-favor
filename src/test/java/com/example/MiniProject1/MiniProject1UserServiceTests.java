package com.example.MiniProject1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.model.Cart;
import com.example.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.model.Order;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

@ComponentScan(basePackages = "com.example.*")
@WebMvcTest
public class MiniProject1UserServiceTests {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser, userWithIdConstructor, userWithoutIdConstructor;

    @BeforeEach
    void setUp() {

        }

    // ------------------------ User Tests -------------------------


    @Test
    void testAddUserAndCheckSameName() throws Exception {
        User testUser = new User(UUID.randomUUID(), "Test User", new ArrayList<>());

        User testUser1 = new User(UUID.randomUUID(), "Test User 1", new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(testUser1)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<User> users = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<User>>() {
        });
        assertTrue(users.stream().anyMatch(user -> user.getName().equals("Test User 1")), "User with same name should exist.");
    }

    @Test
    void testAddUserWithOrders() throws Exception {
        User testUser = new User(UUID.randomUUID(), "Test User", new ArrayList<>());

        List<Order> orders = new ArrayList<>();
        orders.add(new Order(UUID.randomUUID(), testUser.getId(), 100.0, new ArrayList<>()));
        orders.add(new Order(UUID.randomUUID(), testUser.getId(), 200.0, new ArrayList<>()));

        User userWithOrders = new User(UUID.randomUUID(), "User With Orders", orders);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userWithOrders)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}/orders", userWithOrders.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Order> retrievedOrders = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Order>>() {
        });
        assertEquals(2, retrievedOrders.size(), "User should have 2 orders.");
    }

    @Test
    void testAddUserWithoutProvidedID() throws Exception {
        User testUser = new User(UUID.randomUUID(), "Test User", new ArrayList<>());

        User userWithoutId = new User("User Without ID", new ArrayList<>()); // No ID provided

        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userWithoutId)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<User> users = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<User>>() {
        });
        User savedUser = users.stream().filter(user -> user.getName().equals("User Without ID")).findFirst().orElse(null);

        assertNotNull(savedUser, "User should be saved successfully.");
        assertNotNull(savedUser.getId(), "User ID should be auto-generated.");
    }

    @Test
    void testGetUsersWhenUsersExist() throws Exception {

        // User initialized with the constructor that sets ID explicitly
        User userWithIdConstructor = new User(UUID.randomUUID(), "User With ID", new ArrayList<>());

        // User initialized with the constructor that generates ID automatically
        User userWithoutIdConstructor = new User("User Without ID", new ArrayList<>());

        // Add users to the system
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userWithIdConstructor)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userWithoutIdConstructor)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Retrieve all users
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Parse response
        List<User> users = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<User>>() {
        });

        // Ensure the two users are present
        assertTrue(users.size() >= 2, "At least two users should exist.");
        assertTrue(users.stream().anyMatch(user -> user.getName().equals("User With ID")), "User With ID should exist.");
        assertTrue(users.stream().anyMatch(user -> user.getName().equals("User Without ID")), "User Without ID should exist.");
    }

    @Test
    void testGetUsersWhenNoUsersExist() throws Exception {

        // User initialized with the constructor that sets ID explicitly
        User userWithIdConstructor = new User(UUID.randomUUID(), "User With ID", new ArrayList<>());

        // User initialized with the constructor that generates ID automatically
        User userWithoutIdConstructor = new User("User Without ID", new ArrayList<>());


        //delete all users if they exist
        userRepository.overrideData(new ArrayList<>()); // Clears the file by saving an empty list

        // Retrieve users when no users have been added
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Parse response
        List<User> users = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<User>>() {
        });

        // Ensure the list is empty
        assertEquals(0, users.size(), "No users should be returned.");
    }

    @Test
    void testGetUsersInitializedWithBothConstructors() throws Exception {
        // User initialized with the constructor that sets ID explicitly
        User userWithIdConstructor = new User(UUID.randomUUID(), "User With ID", new ArrayList<>());

        // User initialized with the constructor that generates ID automatically
        User userWithoutIdConstructor = new User("User Without ID", new ArrayList<>());

        // Add users with both constructors
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userWithIdConstructor)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userWithoutIdConstructor)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Retrieve all users
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Parse response
        List<User> users = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<User>>() {
        });

        // Find users initialized with both constructors
        User retrievedUserWithId = users.stream().filter(user -> user.getName().equals("User With ID")).findFirst().orElse(null);
        User retrievedUserWithoutId = users.stream().filter(user -> user.getName().equals("User Without ID")).findFirst().orElse(null);

        // Validate users exist
        assertNotNull(retrievedUserWithId, "User initialized with explicit ID should exist.");
        assertNotNull(retrievedUserWithoutId, "User initialized without explicit ID should exist.");
        assertNotNull(retrievedUserWithoutId.getId(), "User created without explicit ID should have a generated UUID.");


    }

    @Test
    void testGetUserById_WhenUserExists() throws Exception {

        // Create a test user with a UUID
        User testUser = new User(UUID.randomUUID(), "Existing User", new ArrayList<>());
        // Add user
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Retrieve user by ID
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}", testUser.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Parse response
        User retrievedUser = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);

        // Validate retrieved user
        assertEquals(testUser.getId(), retrievedUser.getId(), "User ID should match.");
        assertEquals("Existing User", retrievedUser.getName(), "User name should be correct.");
    }

    @Test
    void testGetUserById_WhenUserDoesNotExist() throws Exception {
        // Create a test user with a UUID
        testUser = new User(UUID.randomUUID(), "Existing User", new ArrayList<>());

        UUID nonExistentUserId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}", nonExistentUserId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testGetUserById_WhenInvalidUUID() throws Exception {
        // Create a test user with a UUID
        testUser = new User(UUID.randomUUID(), "Existing User", new ArrayList<>());
        mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}", "invalid-uuid"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    void testGetOrdersByUserId_WhenUserDoesNotExist() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}/orders", nonExistentUserId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testGetOrdersByUserId_WhenUserHasOrders() throws Exception {
        // Create user with an order
        Order testOrder = new Order(UUID.randomUUID(), UUID.randomUUID(), 99.99, new ArrayList<>());
        List<Order> orders = List.of(testOrder);
        User userWithOrders = new User(UUID.randomUUID(), "User With Orders", orders);

        // Add user with orders
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userWithOrders)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Retrieve orders
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}/orders", userWithOrders.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Parse response
        List<Order> retrievedOrders = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Order>>() {});

        // Validate order list
        assertFalse(retrievedOrders.isEmpty(), "Orders should not be empty.");
        assertEquals(1, retrievedOrders.size(), "User should have exactly 1 order.");
        assertEquals(testOrder.getId(), retrievedOrders.get(0).getId(), "Order ID should match.");
    }

    @Test
    void testGetOrdersByUserId_WhenUserHasNoOrders() throws Exception {
        // Create user with no orders
        User userWithoutOrders = new User(UUID.randomUUID(), "User Without Orders", new ArrayList<>());

        // Add user without orders
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userWithoutOrders)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Retrieve orders
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}/orders", userWithoutOrders.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Parse response
        List<Order> retrievedOrders = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Order>>() {});

        // Validate that the list is empty
        assertTrue(retrievedOrders.isEmpty(), "Orders should be empty for this user.");
    }

    @Test
    void testAddOrderToUser_FirstTime() throws Exception {
        // Create user without orders
        User user = new User(UUID.randomUUID(), "User Without Orders", new ArrayList<>());

        // Add user
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Create new order
        Order newOrder = new Order(UUID.randomUUID(), user.getId(), 100.0, new ArrayList<>());

        // Add order to user
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/user/{userId}/checkout", user.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newOrder)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseMessage = result.getResponse().getContentAsString();
        assertEquals("Order added successfully", responseMessage, "Order should be added for the first time.");

        // Verify order was added
        MvcResult userResult = mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        User retrievedUser = objectMapper.readValue(userResult.getResponse().getContentAsString(), User.class);
        assertEquals(1, retrievedUser.getOrders().size(), "User should have 1 order.");
    }

    @Test
    void testAddOrderToUser_WhenExistingOrdersPresent() throws Exception {
        // Create user with an existing order
        Order existingOrder = new Order(UUID.randomUUID(), UUID.randomUUID(), 50.0, new ArrayList<>());
        List<Order> orders = new ArrayList<>(List.of(existingOrder));
        User user = new User(UUID.randomUUID(), "User With Orders", orders);

        // Add user with an existing order
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Create a new order
        Order newOrder = new Order(UUID.randomUUID(), user.getId(), 75.0, new ArrayList<>());

        // Add order to user
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/user/{userId}/checkout", user.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newOrder)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseMessage = result.getResponse().getContentAsString();
        assertEquals("Order added successfully", responseMessage, "New order should be added even if existing orders are present.");

        // Verify order was added
        MvcResult userResult = mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        User retrievedUser = objectMapper.readValue(userResult.getResponse().getContentAsString(), User.class);
        assertEquals(2, retrievedUser.getOrders().size(), "User should have 2 orders.");
    }

    @Test
    void testAddOrderToNonExistentUser() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();

        Order newOrder = new Order(UUID.randomUUID(), nonExistentUserId, 99.99, new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.post("/user/{userId}/checkout", nonExistentUserId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newOrder)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testEmptyCart_MultipleTimes() throws Exception {
        // Create user and cart with products
        UUID userId = UUID.randomUUID();
        Product product = new Product(UUID.randomUUID(), "Product A", 10.0);
        List<Product> products = List.of(product);
        Cart cart = new Cart(UUID.randomUUID(), userId, new ArrayList<>(products));

        // Add user
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new User(userId, "User With Cart", new ArrayList<>()))))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Add cart with products
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(cart)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // **First Call**: Empty the cart (should remove products)
        MvcResult firstResult = mockMvc.perform(MockMvcRequestBuilders.delete("/user/{userId}/emptyCart", userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String firstResponse = firstResult.getResponse().getContentAsString();
        assertEquals("Cart emptied successfully", firstResponse, "First request should empty the cart.");

        // **Second Call**: Try to empty the already empty cart
        MvcResult secondResult = mockMvc.perform(MockMvcRequestBuilders.delete("/user/{userId}/emptyCart", userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String secondResponse = secondResult.getResponse().getContentAsString();
        assertEquals("Cart emptied successfully", secondResponse, "Second request should detect that cart is already empty.");
    }


    @Test
    void testEmptyCart_WhenCartExistsButNoProducts() throws Exception {
        // Create user with an empty cart
        User user = new User(UUID.randomUUID(), "User With Empty Cart", new ArrayList<>());
        Cart emptyCart = new Cart(UUID.randomUUID(), user.getId(), new ArrayList<>());

        // Add user
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Add empty cart
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(emptyCart)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Empty cart
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/user/{userId}/emptyCart", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseMessage = result.getResponse().getContentAsString();
        assertEquals("Cart emptied successfully", responseMessage, "Should return message that cart is empty.");
    }

    @Test
    void testEmptyCart_WhenCartHasProducts() throws Exception {
        // Create user and cart with products
        Product product = new Product(UUID.randomUUID(), "Product A", 10.0);
        List<Product> products = List.of(product);
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>(products));
        User user = new User(cart.getUserId(), "User With Cart", new ArrayList<>());

        // Add user
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Add cart with products
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(cart)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // **First Call**: Empty the cart
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/user/{userId}/emptyCart", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseMessage = result.getResponse().getContentAsString();
        assertEquals("Cart emptied successfully", responseMessage, "Cart should be emptied successfully.");

        // **Ensure latest data is retrieved (force fresh read)**
        Thread.sleep(500); // **Small delay to ensure transaction is committed**

        // **Retrieve cart again to verify it's empty**
        MvcResult cartResult = mockMvc.perform(MockMvcRequestBuilders.get("/cart/{id}", cart.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Cart retrievedCart = objectMapper.readValue(cartResult.getResponse().getContentAsString(), Cart.class);

        // Debugging prints
        System.out.println("Test retrieved cart size: " + retrievedCart.getProducts().size());
        System.out.println("Test cart is empty: " + retrievedCart.getProducts().isEmpty());

        // Final assertion
        assertTrue(retrievedCart.getProducts().isEmpty(), "Cart should be empty after calling emptyCart.");
    }

    @Test
    void testRemoveOrder_WhenOrderExists() throws Exception {
        // Create user with an order
        User user = new User(UUID.randomUUID(), "User With Order", new ArrayList<>());
        Order order = new Order(UUID.randomUUID(), user.getId(), 50.0, new ArrayList<>());
        user.getOrders().add(order);

        // Add user with an order
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Remove the order
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/user/{userId}/removeOrder", user.getId())
                        .param("orderId", order.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseMessage = result.getResponse().getContentAsString();
        assertEquals("Order removed successfully", responseMessage, "Order should be removed.");

        // Verify order is removed
        MvcResult userResult = mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        User updatedUser = objectMapper.readValue(userResult.getResponse().getContentAsString(), User.class);
        assertTrue(updatedUser.getOrders().isEmpty(), "User should have no orders after removal.");
    }

    @Test
    void testRemoveOrder_WhenOrderDoesNotExist() throws Exception {
        // Create user without any orders
        User user = new User(UUID.randomUUID(), "User Without Order", new ArrayList<>());

        // Add user
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        UUID nonExistentOrderId = UUID.randomUUID();

        // Try to remove a non-existent order
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/user/{userId}/removeOrder", user.getId())
                        .param("orderId", nonExistentOrderId.toString()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())  // Ensure correct status
                .andReturn();

        // Extract actual response body
        String actualResponse = result.getResponse().getErrorMessage();

        // Verify error message (Spring may wrap it in an error response)
        assertTrue(actualResponse.contains("Order not found"), "Expected 'Order not found' but got: " + actualResponse);
    }

    @Test
    void testRemoveOrder_WhenUserDoesNotExist() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        // Try to remove an order from a non-existent user
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/user/{userId}/removeOrder", nonExistentUserId)
                        .param("orderId", orderId.toString()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())  // Ensure correct status
                .andReturn();

        // Extract actual response body
        String actualResponse = result.getResponse().getErrorMessage();

        // Verify error message (Spring may wrap it in an error response)
        assertTrue(actualResponse.contains("User not found"), "Expected 'User not found' but got: " + actualResponse);
    }

    @Test
    void testDeleteUser_WhenUserExists() throws Exception {
        // Create a user
        User user = new User(UUID.randomUUID(), "Test User", new ArrayList<>());

        // Add user
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Delete the user
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/user/delete/{userId}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseMessage = result.getResponse().getContentAsString();
        assertEquals("User deleted successfully", responseMessage, "User should be deleted.");

        // Verify user no longer exists
        mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testDeleteUser_WhenUserDoesNotExist() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();

        // Try to delete a non-existent user
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/user/delete/{userId}", nonExistentUserId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())  // Ensure correct status
                .andReturn();

        // Extract actual response body
        String actualResponse = result.getResponse().getErrorMessage();

        // Verify error message
        assertTrue(actualResponse.contains("User not found"), "Expected 'User not found' but got: " + actualResponse);
    }


    @Test
    void testDeleteUser_WhenUserHasOrders() throws Exception {
        // Create a user with orders
        User user = new User(UUID.randomUUID(), "User With Orders", new ArrayList<>());
        Order order = new Order(UUID.randomUUID(), user.getId(), 100.0, new ArrayList<>());
        user.getOrders().add(order);

        // Add user with orders
        mockMvc.perform(MockMvcRequestBuilders.post("/user/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Delete the user
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/user/delete/{userId}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseMessage = result.getResponse().getContentAsString();
        assertEquals("User deleted successfully", responseMessage, "User with orders should still be deleted.");

        // Verify user no longer exists
        mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}", user.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }






}




