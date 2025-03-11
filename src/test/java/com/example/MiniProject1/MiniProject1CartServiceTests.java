package com.example.MiniProject1;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.repository.CartRepository;
import com.example.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import com.example.model.Order;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

@ComponentScan(basePackages = "com.example.*")
@WebMvcTest
public class MiniProject1CartServiceTests {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;


    @BeforeEach
    void setUp() {

    }

    @Test
    void testAddCart_Success() {
        // Arrange: Create a cart with an empty product list
        UUID userId = UUID.randomUUID();
        Cart cart = new Cart(UUID.randomUUID(), userId, new ArrayList<>());

        // Act: Add the cart through the service
        Cart createdCart = cartService.addCart(cart); // Assuming `addCart` is a method in the service

        // Assert: Ensure the cart was created successfully
        assertNotNull(createdCart, "Cart should be created successfully.");
        assertEquals(userId, createdCart.getUserId(), "User ID should match the request.");
    }

    @Test
    void testAddCart_WithoutProducts() {
        // Arrange: Create a cart with an empty product list
        UUID userId = UUID.randomUUID();
        Cart cart = new Cart(UUID.randomUUID(), userId, new ArrayList<>());

        // Act: Add the cart through the service
        Cart createdCart = cartService.addCart(cart); // Assuming `addCart` adds the cart to the repository

        // Assert: Ensure cart exists and has no products
        assertNotNull(createdCart, "Cart should not be null.");
        assertTrue(createdCart.getProducts().isEmpty(), "Cart should have no products.");
    }

    @Test
    void testAddCart_WithProducts() {
        // Arrange: Create a cart with a product
        UUID userId = UUID.randomUUID();
        List<Product> products = List.of(new Product(UUID.randomUUID(), "Product A", 50.0));
        Cart cart = new Cart(UUID.randomUUID(), userId, products);

        // Act: Add the cart through the service
        Cart createdCart = cartService.addCart(cart); // Assuming `addCart` method is responsible for creating the cart

        // Assert: Validate the cart was created correctly
        assertNotNull(createdCart, "Created cart should not be null.");
        assertEquals(1, createdCart.getProducts().size(), "Cart should contain exactly 1 product.");
        assertEquals("Product A", createdCart.getProducts().get(0).getName(), "Product name should match.");
    }


    @Test
    void testGetCarts_WhenCartsExist() {
        cartRepository.overrideData(new ArrayList<>()); // Assuming this resets the carts

        // Arrange: Ensure at least one cart exists
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        cartService.addCart(cart); // Directly add cart to the service or repository

        // Act: Retrieve carts
        List<Cart> carts = cartService.getCarts(); // Direct service call

        // Assert: Ensure carts are present
        assertFalse(carts.isEmpty(), "Carts should exist in the system.");
        assertEquals(1, carts.size(), "There should be at least one cart.");
    }
    @Test
    void testGetCarts_WhenNoCartsExist() {
        cartRepository.overrideData(new ArrayList<>()); // Assuming this resets the carts

        // Arrange: Ensure no carts exist (reset or clear repository if needed)
        cartRepository.overrideData(new ArrayList<>()); // Reset the repository to an empty state

        // Act: Retrieve carts
        List<Cart> carts = cartService.getCarts(); // Direct service call

        // Assert: Response should be an empty list
        assertTrue(carts.isEmpty(), "No carts should exist in the system.");
    }
    @Test
    void testGetCarts_MultipleCarts() {
        cartRepository.overrideData(new ArrayList<>()); // Assuming this resets the carts

        // Arrange: Create multiple carts
        Cart cart1 = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        Cart cart2 = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());

        cartService.addCart(cart1); // Add first cart
        cartService.addCart(cart2); // Add second cart

        // Act: Retrieve all carts
        List<Cart> carts = cartService.getCarts(); // Direct service call

        // Assert: Validate multiple carts exist
        assertFalse(carts.isEmpty(), "Carts should be returned.");
        assertEquals(2, carts.size(), "There should be exactly 2 carts.");
    }



    @Test
    void testGetCartById_WhenExists() {
        // Arrange: Create a cart and add it to the repository
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Cart cart = new Cart(cartId, userId, new ArrayList<>());

        cartService.addCart(cart); // Directly add to service

        // Act: Retrieve the cart
        Cart retrievedCart = cartService.getCartById(cartId);

        // Assert: Ensure the cart exists and matches
        assertNotNull(retrievedCart, "Retrieved cart should not be null.");
        assertEquals(cartId, retrievedCart.getId(), "Cart ID should match.");
    }
    @Test
    void testGetCartById_WhenNotExists() {
        // Arrange: Generate a non-existent cart ID
        UUID nonExistentCartId = UUID.randomUUID();

        // Act & Assert: Ensure service returns null or throws an exception
        Exception exception = assertThrows(NoSuchElementException.class,
                () -> cartService.getCartById(nonExistentCartId),
                "Expected NoSuchElementException when retrieving a non-existent cart.");

        assertTrue(exception.getMessage().contains("Cart not found"),
                "Exception message should indicate missing cart.");
    }
    @Test
    void testGetCartById_WithProducts() {
        // Arrange: Create a cart with products
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        List<Product> products = List.of(new Product(UUID.randomUUID(), "Product A", 50.0));
        Cart cart = new Cart(cartId, userId, products);

        cartService.addCart(cart); // Directly add to service

        // Act: Retrieve the cart
        Cart retrievedCart = cartService.getCartById(cartId);

        // Assert: Ensure the cart contains products
        assertNotNull(retrievedCart, "Retrieved cart should not be null.");
        assertFalse(retrievedCart.getProducts().isEmpty(), "Cart should contain products.");
        assertEquals(1, retrievedCart.getProducts().size(), "Cart should contain exactly one product.");
        assertEquals("Product A", retrievedCart.getProducts().get(0).getName(), "Product name should match.");
    }




    @Test
    void testGetCartByUserId_WhenExists() {
        // Arrange: Create a cart and associate it with a user
        UUID userId = UUID.randomUUID();
        Cart cart = new Cart(UUID.randomUUID(), userId, new ArrayList<>());

        // Directly add the cart to the service
        cartService.addCart(cart);

        // Act: Retrieve the cart by user ID
        Cart retrievedCart = cartService.getCartByUserId(userId);

        // Assert: Ensure the cart exists and matches the user ID
        assertNotNull(retrievedCart, "Cart should be found.");
        assertEquals(userId, retrievedCart.getUserId(), "Cart should belong to the correct user.");
    }

    @Test
    void testGetCartByUserId_WhenUserHasNoCart() {
        // Arrange: Generate a non-existent user ID
        UUID nonExistentUserId = UUID.randomUUID();

        // Act & Assert: Ensure service returns null or throws an exception
        Exception exception = assertThrows(NoSuchElementException.class,
                () -> cartService.getCartByUserId(nonExistentUserId),
                "Expected NoSuchElementException when retrieving a cart for a user with no cart.");

        assertTrue(exception.getMessage().contains("No cart found for user"),
                "Exception message should indicate that no cart exists for the user.");
    }

    @Test
    void testGetCartByUserId_WithProducts() {
        // Arrange: Create a cart with products for a user
        UUID userId = UUID.randomUUID();
        List<Product> products = List.of(new Product(UUID.randomUUID(), "Product A", 50.0));
        Cart cart = new Cart(UUID.randomUUID(), userId, products);

        // Directly add the cart to the service
        cartService.addCart(cart);

        // Act: Retrieve the cart by user ID
        Cart retrievedCart = cartService.getCartByUserId(userId);

        // Assert: Ensure the cart contains products
        assertNotNull(retrievedCart, "Retrieved cart should not be null.");
        assertFalse(retrievedCart.getProducts().isEmpty(), "Cart should contain products.");
        assertEquals(1, retrievedCart.getProducts().size(), "Cart should contain exactly one product.");
        assertEquals("Product A", retrievedCart.getProducts().get(0).getName(), "Product name should match.");
    }

    @Test
    void testAddProductToCart_Success() {
        // Arrange: Create a cart
        UUID userId = UUID.randomUUID();
        Cart cart = new Cart(UUID.randomUUID(), userId, new ArrayList<>());
        cartRepository.addCart(cart);

        Product product = new Product(UUID.randomUUID(), "New Product", 25.0);

        // Act: Add product to the cart
        System.out.println("Before adding: " + cart.getProducts().size());
        cartService.addProductToCart(cart.getId(),product);
        System.out.println("After adding: " + cart.getProducts().size());


        // Assert: Verify the product was added successfully
        Cart updatedCart = cartRepository.getCartById(cart.getId());
        assertNotNull(updatedCart, "Cart should exist.");
        assertEquals(1, updatedCart.getProducts().size(), "Cart should contain exactly 1 product.");
        assertEquals("New Product", updatedCart.getProducts().get(0).getName(), "Product name should match.");
    }


    @Test
    void testAddProductToCart_WhenCartDoesNotExist() {
        // Arrange: Non-existent cart
        UUID nonExistentCartId = UUID.randomUUID();
        Product product = new Product(UUID.randomUUID(), "Invalid Product", 30.0);

        // Act & Assert: Expect exception when adding to a non-existent cart
        Exception exception = assertThrows(NoSuchElementException.class, () -> cartService.addProductToCart(nonExistentCartId, product));
        assertTrue(exception.getMessage().contains("Cart not found"), "Should indicate cart was not found.");
    }

    @Test
    void testAddProductToCart_MultipleProducts() {
        // Arrange: Create a cart with an initial product
        UUID userId = UUID.randomUUID();
        Cart cart = new Cart(UUID.randomUUID(), userId, new ArrayList<>());
        cartRepository.addCart(cart);

        Product product1 = new Product(UUID.randomUUID(), "Product 1", 50.0);
        Product product2 = new Product(UUID.randomUUID(), "Product 2", 75.0);

        // Act: Add multiple products
        cartService.addProductToCart(cart.getId(), product1);
        cartService.addProductToCart(cart.getId(), product2);

        // Assert: Cart should contain 2 products
        assertEquals(2, cartRepository.getCartById(cart.getId()).getProducts().size(), "Cart should contain 2 products.");
    }

    @Test
    void testDeleteProductFromCart_Success() {
        // Arrange: Create a cart with a product
        UUID userId = UUID.randomUUID();
        Product product = new Product(UUID.randomUUID(), "Product to Remove", 40.0);
        Cart cart = new Cart(UUID.randomUUID(), userId, new ArrayList<>(List.of(product)));
        cartRepository.addCart(cart);

        // Act: Remove product from cart
        cartService.deleteProductFromCart(cart.getId(), product);

        // Assert: Cart should be empty
        assertTrue(cartRepository.getCartById(cart.getId()).getProducts().isEmpty(), "Cart should be empty after product removal.");
    }

    @Test
    void testDeleteProductFromCart_WhenCartDoesNotExist() {
        // Arrange: Non-existent cart
        UUID nonExistentCartId = UUID.randomUUID();
        Product product = new Product(UUID.randomUUID(), "Non-existent Product", 30.0);

        // Act & Assert: Expect exception when deleting from a non-existent cart
        Exception exception = assertThrows(NoSuchElementException.class, () -> cartService.deleteProductFromCart(nonExistentCartId, product));
        assertTrue(exception.getMessage().contains("Cart not found"), "Should indicate cart was not found.");
    }

    @Test
    void testDeleteProductFromCart_WhenProductNotInCart() {
        // Arrange: Create a cart without the target product
        UUID userId = UUID.randomUUID();
        Product existingProduct = new Product(UUID.randomUUID(), "Existing Product", 50.0);
        Product nonExistentProduct = new Product(UUID.randomUUID(), "Non-existent Product", 75.0);
        Cart cart = new Cart(UUID.randomUUID(), userId, new ArrayList<>(List.of(existingProduct)));
        cartRepository.addCart(cart);

        // Act: Attempt to remove a product that doesn't exist
        cartService.deleteProductFromCart(cart.getId(), nonExistentProduct);

        // Assert: Cart should still contain the original product
        assertEquals(1, cartRepository.getCartById(cart.getId()).getProducts().size(), "Cart should still contain 1 product.");
    }

    @Test
    void testDeleteCartById_Success() {
        // Arrange: Create a cart
        UUID userId = UUID.randomUUID();
        Cart cart = new Cart(UUID.randomUUID(), userId, new ArrayList<>());
        cartRepository.addCart(cart);

        // Act: Delete the cart
        cartService.deleteCartById(cart.getId());

        // Assert: Cart should be removed
        Exception exception = assertThrows(NoSuchElementException.class, () -> cartRepository.getCartById(cart.getId()));
        assertTrue(exception.getMessage().contains("Cart not found"), "Deleted cart should not exist.");
    }

    @Test
    void testDeleteCartById_WhenCartDoesNotExist() {
        // Arrange: Non-existent cart
        UUID nonExistentCartId = UUID.randomUUID();

        // Act & Assert: Expect exception when deleting a non-existent cart
        Exception exception = assertThrows(NoSuchElementException.class, () -> cartService.deleteCartById(nonExistentCartId));
        assertTrue(exception.getMessage().contains("Cart not found"), "Should indicate cart was not found.");
    }

    @Test
    void testDeleteCartById_WhenCartHasProducts() {
        // Arrange: Create a cart with products
        UUID userId = UUID.randomUUID();
        List<Product> products = List.of(new Product(UUID.randomUUID(), "Product X", 60.0));
        Cart cart = new Cart(UUID.randomUUID(), userId, products);
        cartRepository.addCart(cart);

        // Act: Delete the cart
        cartService.deleteCartById(cart.getId());

        // Assert: Cart should be deleted
        Exception exception = assertThrows(NoSuchElementException.class, () -> cartRepository.getCartById(cart.getId()));
        assertTrue(exception.getMessage().contains("Cart not found"), "Deleted cart should not exist.");
    }






}