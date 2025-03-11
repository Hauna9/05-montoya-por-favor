package com.example.MiniProject1;

import com.example.model.Order;
import com.example.model.Product;
import com.example.repository.OrderRepository;
import com.example.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MiniProject1OrderServiceTests {



    private OrderService orderService;
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new OrderRepository();
        orderService = new OrderService(orderRepository);

        // Clear orders before running each test
        orderRepository.overrideData(new ArrayList<>());
    }


    @Test
    void testAddOrder_Success() {
        UUID userId = UUID.randomUUID();
        List<Product> products = List.of(new Product(UUID.randomUUID(), "Product A", 50.0));
        Order order = new Order(userId, 50.0, products);

        orderService.addOrder(order);

        assertEquals(1, orderService.getOrders().size(), "Order should be added successfully.");
    }

    @Test
    void testAddOrder_WithEmptyProducts() {
        UUID userId = UUID.randomUUID();
        Order order = new Order(userId, 0.0, new ArrayList<>());

        orderService.addOrder(order);

        assertEquals(1, orderService.getOrders().size(), "Order should be added even with no products.");
    }

    @Test
    void testAddOrder_MultipleOrders() {
        Order order1 = new Order(UUID.randomUUID(), 100.0, new ArrayList<>());
        Order order2 = new Order(UUID.randomUUID(), 200.0, new ArrayList<>());

        orderService.addOrder(order1);
        orderService.addOrder(order2);

        assertEquals(2, orderService.getOrders().size(), "There should be exactly 2 orders.");
    }



    @Test
    void testGetOrders_WhenOrdersExist() {
        orderService.addOrder(new Order(UUID.randomUUID(), 100.0, new ArrayList<>()));

        List<Order> orders = orderService.getOrders();

        assertFalse(orders.isEmpty(), "Orders should be returned.");
        assertEquals(1, orders.size(), "There should be at least one order.");
    }

    @Test
    void testGetOrders_WhenNoOrdersExist() {
        List<Order> orders = orderService.getOrders();

        assertTrue(orders.isEmpty(), "No orders should exist.");
    }

    @Test
    void testGetOrders_MultipleOrders() {
        orderService.addOrder(new Order(UUID.randomUUID(), 150.0, new ArrayList<>()));
        orderService.addOrder(new Order(UUID.randomUUID(), 250.0, new ArrayList<>()));

        List<Order> orders = orderService.getOrders();

        assertEquals(2, orders.size(), "There should be exactly 2 orders.");
    }



    @Test
    void testGetOrderById_WhenExists() {
        Order order = new Order(UUID.randomUUID(), 100.0, new ArrayList<>());
        orderService.addOrder(order);

        Order retrievedOrder = orderService.getOrderById(order.getId());

        assertNotNull(retrievedOrder, "Order should be found.");
        assertEquals(order.getId(), retrievedOrder.getId(), "Order ID should match.");
    }

    @Test
    void testGetOrderById_WhenNotExists() {
        UUID nonExistentOrderId = UUID.randomUUID();

        // Act & Assert: Expect NoSuchElementException when fetching a non-existent order
        assertThrows(NoSuchElementException.class, () ->
                        orderService.getOrderById(nonExistentOrderId),
                "Fetching a non-existent order should throw NoSuchElementException."
        );
    }


    @Test
    void testGetOrderById_WithMultipleOrders() {
        Order order1 = new Order(UUID.randomUUID(), 100.0, new ArrayList<>());
        Order order2 = new Order(UUID.randomUUID(), 200.0, new ArrayList<>());

        orderService.addOrder(order1);
        orderService.addOrder(order2);

        Order retrievedOrder = orderService.getOrderById(order1.getId());

        assertNotNull(retrievedOrder, "Order should be found.");
        assertEquals(order1.getId(), retrievedOrder.getId(), "Correct order should be retrieved.");
    }



    @Test
    void testDeleteOrderById_Success() {
        // Arrange: Create and add an order
        Order order = new Order(UUID.randomUUID(), 100.0, new ArrayList<>());
        orderService.addOrder(order);

        // Act: Delete the order
        orderService.deleteOrderById(order.getId());

        // Assert: Verify that retrieving the order now throws NoSuchElementException
        assertThrows(NoSuchElementException.class, () ->
                        orderService.getOrderById(order.getId()),
                "Fetching a deleted order should throw NoSuchElementException."
        );
    }




    @Test
    void testDeleteOrderById_WhenNotExists() {
        UUID nonExistentOrderId = UUID.randomUUID();

        Exception exception = assertThrows(NoSuchElementException.class, () ->
                        orderService.deleteOrderById(nonExistentOrderId),
                "Deleting a non-existent order should throw NoSuchElementException."
        );

        assertTrue(exception.getMessage().contains("Order not found"),
                "Exception message should indicate that the order was not found.");
    }

    @Test
    void testDeleteOrderById_WhenOrderHasProducts() {
        // Arrange: Create an order with products
        UUID userId = UUID.randomUUID();
        List<Product> products = List.of(new Product(UUID.randomUUID(), "Product A", 50.0));
        Order order = new Order(UUID.randomUUID(), userId, 50.0, products);
        orderService.addOrder(order);

        // Act: Delete the order
        orderService.deleteOrderById(order.getId());

        // Assert: Order should be deleted
        Exception exception = assertThrows(NoSuchElementException.class, () -> orderRepository.getOrderById(order.getId()));
        assertTrue(exception.getMessage().contains("Order not found"), "Deleted order should not exist.");
    }



}
