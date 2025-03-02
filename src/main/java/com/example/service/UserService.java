package com.example.service;

import com.example.model.Order;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService extends MainService<User>  {
    UserRepository userRepository;
    @Autowired
    public UserService(UserRepository repository) {
        super(repository);
        this.userRepository = repository;
    }

    public User addUser(User user){
        return userRepository.addUser(user);
    }

    public ArrayList<User> getUsers(){
        return getAll();
    }

    public User getUserById(UUID userId){
        User user = userRepository.getUserById(userId);
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    public List<Order> getOrdersByUserId(UUID userId){
        return userRepository.getOrdersByUserId(userId);
    }
    public void addOrderToUser(UUID userId){}

    public void emptyCart(UUID userId){}

    public void removeOrderFromUser(UUID userId, UUID orderId){
        userRepository.removeOrderFromUser(userId, orderId);
    }

    public void deleteUserById(UUID userId){
        userRepository.deleteUserById(userId);
    }


}
