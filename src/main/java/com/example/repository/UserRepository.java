package com.example.repository;

import com.example.model.Order;
import com.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class UserRepository extends MainRepository<User>{

    private final OrderRepository orderRepository;
    @Autowired
    public UserRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    @Override
    protected String getDataPath() {
        return "src/main/java/com.example/data/users.json";
    }

    @Override
    protected Class<User[]> getArrayType() {
        return User[].class;
    }

    public ArrayList<User> getUsers() {
        return findAll();
    }

    public User getUserById(UUID userId){
        ArrayList<User> users = getUsers();
        for (User user : users) {
            if(user.getId().equals(userId)){
                return user;
            }
        }
        return null;
    }

    public User addUser(User user){
        user.setId(UUID.randomUUID());
        save(user);
        return user;
    }

    public List<Order> getOrdersByUserId(UUID userId){
        ArrayList<Order> orders = new ArrayList<>();
        for (Order order : orderRepository.getOrders()) {
            if(order.getUserId().equals(userId)){
                orders.add(order);
            }
        }
        return orders;
    }

    public void addOrderToUser(UUID userId, Order order){
        User user = getUserById(userId);
        order.setId(UUID.randomUUID());
        order.setUserId(userId);
        orderRepository.addOrder(order);
        List<Order> userOrders = user.getOrders();
        userOrders.add(order);
        user.setOrders(userOrders);

        ArrayList<User> users = getUsers();
        users.removeIf(u -> u.getId().equals(userId));
        users.add(user);
        saveAll(users);
    }

    public void removeOrderFromUser(UUID userId, UUID orderId){
        User user = getUserById(userId);
        List<Order> userOrders = user.getOrders();
        userOrders.removeIf(order -> order.getId().equals(orderId));
        user.setOrders(userOrders);
        orderRepository.deleteOrderById(orderId);

        ArrayList<User> users = getUsers();
        users.removeIf(u -> u.getId().equals(userId));
        users.add(user);
        saveAll(users);
    }

    public void deleteUserById(UUID userId){
        ArrayList<User> users = getUsers();
        users.removeIf(user -> user.getId().equals(userId));
        saveAll(users);
    }

}