package com.example.service;

import com.example.model.Order;
import com.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class OrderService extends MainService<Order> {

    OrderRepository orderRepository;
    @Autowired
    public OrderService(OrderRepository orderRepository) {
        super(orderRepository);
        this.orderRepository = orderRepository;
    }

    public void addOrder(Order order){
        orderRepository.addOrder(order);
    }

    public ArrayList<Order> getOrders(){
        return orderRepository.getOrders();
    }

    public Order getOrderById(UUID orderId){
        Order order = orderRepository.getOrderById(orderId);
        if(order == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return order;
    }

    public void deleteOrderById(UUID orderId) throws IllegalArgumentException{
        Order order = orderRepository.getOrderById(orderId);
        if(order == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        orderRepository.deleteOrderById(orderId);
    }

}
