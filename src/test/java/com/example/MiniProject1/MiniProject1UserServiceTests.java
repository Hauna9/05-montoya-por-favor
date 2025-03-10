package com.example.MiniProject1;

import java.util.*;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static jdk.internal.org.objectweb.asm.util.CheckClassAdapter.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.model.Order;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


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

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired

    private OrderRepository orderRepository;



    private User testUser, userWithIdConstructor, userWithoutIdConstructor;

    //TODO must i do this and in the others too?


    // ------------------------ User Tests -------------------------


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User(UUID.randomUUID(), "Test User", new ArrayList<>());
    }

    @Test
    void testAddUserAndCheckSameName() {
        // Arrange
        User testUser1 = new User(UUID.randomUUID(), "Test User 1", new ArrayList<>());

        // Act
        userService.addUser(testUser1);
        List<User> users = userService.getUsers();

        // Assert
        assertNotNull(users, "User list should not be null.");
        assertTrue(users.stream().anyMatch(user -> user.getName().equals("Test User 1")),
                "User with same name should exist.");
    }

    @Test
    void testAddUserWithOrders() {
        // Arrange
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(UUID.randomUUID(), UUID.randomUUID(), 100.0, new ArrayList<>()));
        orders.add(new Order(UUID.randomUUID(), UUID.randomUUID(), 200.0, new ArrayList<>()));

        User userWithOrders = new User(UUID.randomUUID(), "User With Orders", orders);

        // Act
        userService.addUser(userWithOrders);
        List<Order> retrievedOrders = userService.getOrdersByUserId(userWithOrders.getId());

        // Assert
        assertNotNull(retrievedOrders, "Retrieved orders list should not be null.");
        assertEquals(2, retrievedOrders.size(), "User should have 2 orders.");
        assertEquals(orders.size(), retrievedOrders.size(), "Order list size should match.");

        for (int i = 0; i < orders.size(); i++) {
            assertEquals(orders.get(i).getId(), retrievedOrders.get(i).getId(), "Order ID should match.");
            assertEquals(orders.get(i).getUserId(), retrievedOrders.get(i).getUserId(), "User ID should match.");
            assertEquals(orders.get(i).getTotalPrice(), retrievedOrders.get(i).getTotalPrice(), "Total price should match.");
        }
    }



    @Test
    void testAddUserWithoutProvidedID() {
        // Arrange
        User userWithoutId = new User("User Without ID", new ArrayList<>()); // No ID provided

        // Act
        userService.addUser(userWithoutId);

        // Assert
        assertNotNull(userWithoutId.getId(), "User ID should be auto-generated.");
    }








    @Test
    void testGetUserById_WhenUserExists() {
        // Arrange
        User user = new User(UUID.randomUUID(), "Existing User", new ArrayList<>());
        userRepository.addUser(user);

        // Act
        User retrievedUser = userRepository.getUserById(user.getId());

        // Assert
        assertNotNull(retrievedUser, "User should exist.");
        assertEquals(user.getId(), retrievedUser.getId(), "User ID should match.");
        assertEquals("Existing User", retrievedUser.getName(), "User name should match.");
    }

    @Test
    void testGetUserById_WhenUserDoesNotExist() {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();

        // Act
        User retrievedUser = userService.getUserById(nonExistentUserId);

        // Assert
        assertNull(retrievedUser, "User should be null when not found.");
    }

    @Test
    void testGetUserById_WhenInvalidUUID() {
        // Arrange
        User testUser = new User(UUID.randomUUID(), "Existing User", new ArrayList<>());
        userService.addUser(testUser);

        // Act & Assert (This test might not be needed if UUIDs are always valid in Java)
        assertThrows(IllegalArgumentException.class, () -> {
            UUID invalidUuid = UUID.fromString("invalid-uuid"); // This will throw an exception
            userService.getUserById(invalidUuid);
        });
    }

    @Test
    void testGetUsers_WhenUsersExist() {
        // Arrange
        User user1 = new User(UUID.randomUUID(), "User One", new ArrayList<>());
        User user2 = new User(UUID.randomUUID(), "User Two", new ArrayList<>());

        userService.addUser(user1);
        userService.addUser(user2);

        // Act
        List<User> retrievedUsers = userService.getUsers();

        // Assert
        assertNotNull(retrievedUsers, "User list should not be null.");

        // Check that both users exist in the retrieved list
        boolean user1Exists = retrievedUsers.stream().anyMatch(user -> user.getId().equals(user1.getId()));
        boolean user2Exists = retrievedUsers.stream().anyMatch(user -> user.getId().equals(user2.getId()));

        assertTrue(user1Exists, "User One should exist in the retrieved users list.");
        assertTrue(user2Exists, "User Two should exist in the retrieved users list.");
    }



    @Test
    void testGetUsersInitializedWithBothConstructors() {
        // Arrange
        User userWithId = new User(UUID.randomUUID(), "User With ID", new ArrayList<>());
        User userWithoutId = new User("User Without ID", new ArrayList<>()); // Should auto-generate an ID

        userService.addUser(userWithId);
        userService.addUser(userWithoutId);

        // Act
        List<User> users = userService.getUsers();

        // Assert
        assertNotNull(users, "User list should not be null.");
        assertFalse(users.isEmpty(), "User list should not be empty.");

        // Ensure users with the expected IDs exist
        boolean userWithIdExists = users.stream()
                .anyMatch(user -> user.getId().equals(userWithId.getId()));

        boolean userWithoutIdExists = users.stream()
                .anyMatch(user -> user.getName().equals("User Without ID") && user.getId() != null);

        assertTrue(userWithIdExists, "User initialized with explicit ID should exist.");
        assertTrue(userWithoutIdExists, "User initialized without explicit ID should have a generated UUID.");
    }


    @Test
    void testGetUsers_WhenNoUsersExist() {
        // Arrange: Ensure the repository is empty
        userRepository.saveAll(new ArrayList<>()); // Clears all users from repository

        // Act
        List<User> retrievedUsers = userService.getUsers();

        // Assert
        assertNotNull(retrievedUsers, "User list should not be null.");
        assertTrue(retrievedUsers.isEmpty(), "User list should be empty when no users exist.");
    }







//FIXME fix creating a cart in the tests and have it created for the user and it being empty

    @Test
    void testGetOrdersByUserId_UserDoesNotExist() {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();

        // Act
        List<Order> retrievedOrders = userService.getOrdersByUserId(nonExistentUserId);

        // Assert
        assertNotNull(retrievedOrders, "Orders list should not be null.");
        assertTrue(retrievedOrders.isEmpty(), "Orders list should be empty for non-existent user.");
    }
    @Test
    void testGetOrdersByUserId_UserHasOrders() {
        // Arrange
        UUID userId = UUID.randomUUID();
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(UUID.randomUUID(), userId, 100.0, new ArrayList<>()));

        User userWithOrders = new User(userId, "User With Orders", orders);
        userService.addUser(userWithOrders);

        // Act
        List<Order> retrievedOrders = userService.getOrdersByUserId(userId);

        // Assert
        assertNotNull(retrievedOrders, "Orders list should not be null.");
        assertEquals(1, retrievedOrders.size(), "User should have 1 order.");
    }

    @Test
    void testGetOrdersByUserId_UserHasNoOrders() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User userWithoutOrders = new User(userId, "User Without Orders", new ArrayList<>());
        userService.addUser(userWithoutOrders);

        // Act
        List<Order> retrievedOrders = userService.getOrdersByUserId(userId);

        // Assert
        assertNotNull(retrievedOrders, "Orders list should not be null.");
        assertTrue(retrievedOrders.isEmpty(), "User should have no orders.");
    }












    @Test
    void testAddOrderToUser_FirstTime() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "User Without Orders", new ArrayList<>());
        userRepository.addUser(user); // Add user to repository

        // 🔥 Ensure a cart exists with products
        List<Product> products = List.of(new Product(UUID.randomUUID(), "Test Product", 10.0));
        Cart cart = new Cart(UUID.randomUUID(), userId, new ArrayList<>(products));
        cartRepository.addCart(cart);

        // Act: Add order
        userService.addOrderToUser(userId);

        // Assert
        User updatedUser = userRepository.getUserById(userId);
        assertNotNull(updatedUser, "User should exist.");
        assertEquals(1, updatedUser.getOrders().size(), "User should have 1 order.");

        // Instead of comparing a manually created Order ID, retrieve the actual stored order
        Order addedOrder = updatedUser.getOrders().get(0);
        assertNotNull(addedOrder.getId(), "Added order should have a valid UUID.");
        assertEquals(10.0, addedOrder.getTotalPrice(), "Total price should match the product price.");
    }


    @Test
    void testAddOrderToUser_WhenExistingOrdersPresent() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Create user with an existing order
        Order existingOrder = new Order(UUID.randomUUID(), userId, 50.0, new ArrayList<>());
        List<Order> orders = new ArrayList<>(List.of(existingOrder));
        User user = new User(userId, "User With Orders", orders);
        userRepository.addUser(user); // Add user with an existing order

        // Ensure a cart exists for the user
        Product product = new Product(UUID.randomUUID(), "Product A", 25.0);
        List<Product> cartProducts = new ArrayList<>(List.of(product));
        Cart cart = new Cart(UUID.randomUUID(), userId, cartProducts);
        cartRepository.addCart(cart); // Ensure cart exists before calling addOrderToUser()

        // Expected total price
        double expectedTotalPrice = 25.0; // Price of product in cart

        // Debug: Print user orders before adding new order
        User debugUserBefore = userRepository.getUserById(userId);
        System.out.println("Before adding order, user orders: " + debugUserBefore.getOrders());

        // Act: Add order to user
        userService.addOrderToUser(userId);

        // Retrieve updated user after order is added
        User updatedUser = userRepository.getUserById(userId);

        // Debug: Print retrieved user orders after addition
        System.out.println("After adding order, user orders: " + updatedUser.getOrders());

        // Assert: Verify the order was added
        assertNotNull(updatedUser, "User should exist.");
        assertEquals(2, updatedUser.getOrders().size(), "User should have 2 orders.");

        // Check if the order exists by matching total price and product count
        boolean orderExists = updatedUser.getOrders().stream()
                .anyMatch(order -> order.getTotalPrice() == expectedTotalPrice &&
                        order.getProducts().size() == cartProducts.size());

        assertTrue(orderExists, "New order should be in user's order list.");
    }





    @Test
    void testAddOrderToNonExistentUser() {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();

        // Act & Assert
        Exception exception = assertThrows(NoSuchElementException.class,
                () -> userService.addOrderToUser(nonExistentUserId),
                "Adding an order to a non-existent user should throw a NoSuchElementException.");

        assertEquals("User not found", exception.getMessage(),
                "Expected 'User not found' message when adding an order to a non-existent user.");
    }



    @Test
    void testEmptyCart_MultipleTimes() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Product product = new Product(UUID.randomUUID(), "Product A", 10.0);
        List<Product> products = List.of(product);
        Cart cart = new Cart(UUID.randomUUID(), userId, new ArrayList<>(products));

        User user = new User(userId, "User With Cart", new ArrayList<>());
        userRepository.addUser(user);
        cartRepository.save(cart); // Save cart with products

        // Act: First Call - Empty the cart
        userService.emptyCart(userId);

        // Assert: Cart should be empty after first call
        Cart retrievedCart = cartRepository.getCartById(cart.getId());
        assertNotNull(retrievedCart, "Cart should still exist.");
        assertTrue(retrievedCart.getProducts().isEmpty(), "Cart should be empty after first emptyCart call.");

        // Act: Second Call - Try to empty the already empty cart
        userService.emptyCart(userId);

        // Assert: Cart should still be empty
        retrievedCart = cartRepository.getCartById(cart.getId());
        assertTrue(retrievedCart.getProducts().isEmpty(), "Cart should remain empty after second emptyCart call.");
    }
    @Test
    void testEmptyCart_WhenCartExistsButNoProducts() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "User With Empty Cart", new ArrayList<>());
        Cart emptyCart = new Cart(UUID.randomUUID(), userId, new ArrayList<>()); // Cart with no products

        userRepository.addUser(user);
        cartRepository.save(emptyCart); // Save empty cart

        // Act: Empty the cart
        userService.emptyCart(userId);

        // Assert: Cart should remain empty
        Cart retrievedCart = cartRepository.getCartById(emptyCart.getId());
        assertNotNull(retrievedCart, "Cart should still exist.");
        assertTrue(retrievedCart.getProducts().isEmpty(), "Cart should still be empty after calling emptyCart.");
    }
    @Test
    void testEmptyCart_WhenCartHasProducts() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Product product = new Product(UUID.randomUUID(), "Product A", 10.0);
        List<Product> products = List.of(product);
        Cart cart = new Cart(UUID.randomUUID(), userId, new ArrayList<>(products));

        User user = new User(userId, "User With Cart", new ArrayList<>());
        userRepository.addUser(user);
        cartRepository.save(cart); // Save cart with products

        // Act: Empty the cart
        userService.emptyCart(userId);

        // Assert: Cart should now be empty
        Cart retrievedCart = cartRepository.getCartById(cart.getId());
        assertNotNull(retrievedCart, "Cart should still exist.");
        assertTrue(retrievedCart.getProducts().isEmpty(), "Cart should be empty after calling emptyCart.");
    }


    @Test
    void testRemoveOrder_WhenOrderExists() {
        // Arrange: Create user with an order
        User user = new User(UUID.randomUUID(), "User With Order", new ArrayList<>());
        Order order = new Order(UUID.randomUUID(), user.getId(), 50.0, new ArrayList<>());

        user.getOrders().add(order);
        userRepository.addUser(user);
        orderRepository.addOrder(order);

        // Act: Remove the order
        userService.removeOrderFromUser(user.getId(), order.getId());

        // Assert: Order should be removed
        User updatedUser = userRepository.getUserById(user.getId());
        assertNotNull(updatedUser, "User should still exist.");
        assertTrue(updatedUser.getOrders().isEmpty(), "User should have no orders after removal.");

        // Ensure the order does not exist in the order repository
        assertNull(orderRepository.getOrderById(order.getId()), "Order should be removed from order repository.");
    }

    @Test
    void testRemoveOrder_WhenOrderDoesNotExist() {
        // Arrange: Create user without any orders
        User user = new User(UUID.randomUUID(), "User Without Order", new ArrayList<>());
        userRepository.addUser(user);

        UUID nonExistentOrderId = UUID.randomUUID();

        // Act & Assert: Try to remove a non-existent order
        Exception exception = assertThrows(NoSuchElementException.class, () ->
                userService.removeOrderFromUser(user.getId(), nonExistentOrderId)
        );

        assertEquals("Order not found", exception.getMessage(), "Expected 'Order not found' exception.");
    }
    @Test
    void testRemoveOrder_WhenUserDoesNotExist() {
        UUID nonExistentUserId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        // Act & Assert: Try to remove an order from a non-existent user
        Exception exception = assertThrows(NoSuchElementException.class, () ->
                userService.removeOrderFromUser(nonExistentUserId, orderId)
        );

        assertEquals("User not found", exception.getMessage(), "Expected 'User not found' exception.");
    }



    @Test
    void testDeleteUser_WhenUserExists() {
        // Arrange: Create and add a user
        User user = new User(UUID.randomUUID(), "Test User", new ArrayList<>());
        userRepository.addUser(user);

        // Act: Delete the user
        userService.deleteUserById(user.getId());

        // Assert: User should be deleted
        User deletedUser = userRepository.getUserById(user.getId());
        assertNull(deletedUser, "User should no longer exist after deletion.");
    }
    @Test
    void testDeleteUser_WhenUserDoesNotExist() {
        // Arrange: Generate a random UUID for a non-existent user
        UUID nonExistentUserId = UUID.randomUUID();

        // Act & Assert: Ensure the correct exception is thrown
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            userService.deleteUserById(nonExistentUserId);
        });

        // ✅ Check if the message contains the expected part
        assertTrue(exception.getMessage().contains("User not found"),
                "Expected 'User not found' message but got: " + exception.getMessage());
    }

    //TODO revise this idt it tests correctly
    //FIXME get this to work    and deleting user with orders//
//    @Test
//    void testDeleteUser_WhenUserHasNoOrdersOrCart() {
//        // Arrange: Create a user with no orders or cart
//        UUID userId = UUID.randomUUID();
//        User user = new User(userId, "User No Orders", new ArrayList<>());
//        userService.addUser(user);
//
//        // Act: Delete the user
//        userService.deleteUserById(userId);
//
//        // Assert: User should no longer exist
//        assertThrows(NoSuchElementException.class, () -> userRepository.getUserById(userId),
//                "Expected NoSuchElementException when retrieving a deleted user.");
//    }

    @Test
    void testDeleteUser_Twice() {
        // Arrange: Create and add a user
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "User", new ArrayList<>());
        userService.addUser(user);

        // Act: Delete the user twice
        userService.deleteUserById(userId);
        assertThrows(NoSuchElementException.class, () -> userService.deleteUserById(userId),
                "Expected NoSuchElementException when deleting an already deleted user.");
    }









}




