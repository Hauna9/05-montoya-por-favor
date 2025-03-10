package com.example.controller;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.User;
import com.example.repository.ProductRepository;
import com.example.service.CartService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }



    @GetMapping("/")
    public ArrayList<User> getUsers(){
        return userService.getUsers();
    }

    @PostMapping("/")
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }


    @GetMapping("/{userId}")
    public User getUserById(@PathVariable UUID userId){
        return userService.getUserById(userId);
    }

    @GetMapping("/{userId}/orders")
    public List<Order> getOrdersByUserId(@PathVariable UUID userId){
        //check user exists
        if(userService.getUserById(userId) != null){
            return userService.getOrdersByUserId(userId);
        }
         List<Order> orders= new ArrayList<>();
        return orders;
    }

    @PostMapping("/{userId}/checkout")
    public String addOrderToUser(@PathVariable UUID userId){
        userService.addOrderToUser(userId);
        return "Order added successfully";
    }

    @PostMapping("/{userId}/removeOrder")
    public String removeOrderFromUser(@PathVariable UUID userId, @RequestParam UUID orderId){
        userService.removeOrderFromUser(userId, orderId);
        return "Order removed successfully";
    }

    @DeleteMapping("/{userId}/emptyCart")
    public String emptyCart(@PathVariable UUID userId){
        userService.emptyCart(userId);
        return "Cart emptied successfully";
    }

    @DeleteMapping("/delete/{userId}")
    public String deleteUserById(@PathVariable UUID userId){

if(userService.getUserById(userId) != null) {
            userService.deleteUserById(userId);
            return "User deleted successfully";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }


    }


    @PutMapping("/addProductToCart")
    public String addProductToCart(@RequestParam UUID userId, @RequestParam UUID productId)
    {
        return userService.addProductToCart(userId, productId);
    }
    @PutMapping("/deleteProductFromCart")
    public String deleteProductFromCart(@RequestParam UUID userId, @RequestParam UUID productId)
    {
        return userService.deleteProductFromCart(userId, productId);
    }


}
