package com.example.service;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class UserService extends MainService<User>  {
    UserRepository userRepository;
    @Autowired
    private OrderService orderService;

    @Autowired
    public UserService(UserRepository repository) {
        super(repository);
        this.userRepository = repository;
    }

    @Autowired
    OrderRepository orderRepository;


    public User addUser(User user){
        return userRepository.addUser(user);
    }

    public ArrayList<User> getUsers(){
        return getAll();
    }

    public User getUserById(UUID userId){
       return  userRepository.getUserById(userId);
//        //FIXME i uncommented the below code so should i comment?
//        if(user == null){
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
//        }
//        return user;
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        try {
            return userRepository.getOrdersByUserId(userId);
        } catch (NoSuchElementException e) {
            return Collections.emptyList(); // Return empty list instead of throwing
        }
    }


    @Autowired
    CartService cartService;
    //FIXME make this a cart service not cart repo

    public void addOrderToUser(UUID userId) {
        // Check if user exists
        User user = getUserById(userId);
        if (user == null) {
            throw new NoSuchElementException("User not found");
        }

        // Ensure the user has a cart
        Cart cart = cartRepository.getCartByUserId(userId);
        if (cart == null || cart.getProducts().isEmpty()) {
            throw new IllegalStateException("User's cart is empty or does not exist");
        }

        // Get products from the cart
        List<Product> products = new ArrayList<>(cart.getProducts());

        // Calculate total price
        double totalPrice = products.stream().mapToDouble(Product::getPrice).sum();

        // Create and add the new order
        Order newOrder = new Order(UUID.randomUUID(), userId, totalPrice, products);
        userRepository.addOrderToUser(userId, newOrder);

        // Empty the cart after order is placed
        cartRepository.getCartByUserId(userId).getProducts().clear();
    }




    public void emptyCart(UUID userId){
        Cart cart = cartService.getCartByUserId(userId);


        if (cart.getProducts().isEmpty()) {
            return ;
        }
        List<Product> products = new ArrayList<>();

        // delete cart and create a new one with the same cartID
        cartService.deleteCartById(cart.getId());
        Cart cartNew = new Cart(cart.getId(),userId, products);
        cartService.addCart(cartNew);


        // Fetch it again after saving to check if it is really updated
        Cart updatedCart = cartRepository.getCartByUserId(userId);

        // Debugging prints
        System.out.println("Cart products after emptying: " + updatedCart.getProducts().size()); // Should be 0
        System.out.println("Cart is empty: " + updatedCart.getProducts().isEmpty()); // Should be true

    }

    public void removeOrderFromUser(UUID userId, UUID orderId){
       //check if user exists
        if(userRepository.getUserById(userId) == null){
            System.out.println("User not found");
            throw new NoSuchElementException("User not found");
        }
        //check if order exists
        if(userRepository.getOrdersByUserId(userId).isEmpty()){
            System.out.println("No orders found");
            throw new NoSuchElementException("Order not found");
        }


        userRepository.removeOrderFromUser(userId, orderId);
    }



    public void deleteUserById(UUID userId) {
        // Ensure user exists
        User user = userRepository.getUserById(userId);

        // Delete user's orders
        List<Order> userOrders = userRepository.getOrdersByUserId(userId);
        for (Order order : userOrders) {
            orderRepository.deleteOrderById(order.getId());
        }

        // Check if user has a cart and delete it
        Cart userCart = cartRepository.getCartByUserId(userId);
        if (userCart != null) {  // ✅ Use a null check if it doesn't return Optional<Cart>
            cartRepository.deleteCartById(userCart.getId());
        }

        // Now delete the user
        userRepository.deleteUserById(userId);
    }


    //FIXME make this a service
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartRepository cartRepository;
    public String addProductToCart(UUID userId, UUID productId) {
        // Fetch user and product from the database
        User user = userRepository.getUserById(userId);
        System.out.println("User name: " + user.getName());  // should be Test User
        Product product = productRepository.getProductById(productId);
        System.out.println("Product: using getProductById " + product.getName());
        // creates a cart incase it does not exist
        if(cartRepository.getCartByUserId(userId) == null){
            System.out.println("cart not found");
            List <Product> products = new ArrayList<>();
            products.add(product);
            Cart cart = new Cart(userId,products);
            System.out.println("cart created successfully");
            cartRepository.addCart(cart);
            System.out.println("cart added to cartrepo successfully");
        }
        else {
            Cart cart = cartRepository.getCartByUserId(userId);
            System.out.println("Cart: " + cart);
            System.out.println("cart id using getId:" + cart.getId());
            System.out.println("Cart products before adding" + cart.getProducts().get(0).getName()); // should be empty
            cartRepository.addProductToCart(cart.getId(), product); //FIXME cart or cartid
            System.out.println("Cart products after adding" + cart.getProducts().get(0).getName()); // should be Test Product
        }



        return "Product added to cart";
    }

    //FIXME mention cartservice not cartrepository

    public String deleteProductFromCart(UUID userId, UUID productId) {
        // check cart exists
        //check if empty --> return "Cart is empty"
        //check if product exists in cart
        // delete product from cart --> return "Product deleted from cart"

        // Fetch user and product from the database
        User user = userRepository.getUserById(userId);
        System.out.println("User name: " + user.getName());  // should be Test User
        // Fetch product from the product repo
        Product product = productRepository.getProductById(productId);
        System.out.println("Product: using getProductById " + product.getName());
        // check if cart exists
        if(cartRepository.getCartByUserId(userId) == null) {
            System.out.println("cart doesnt exist ");
            List<Product> products = new ArrayList<>();
            Cart cart = new Cart(userId, products);
            System.out.println("cart created successfully");
            return "Cart is empty";
        }
        // check if empty
        Cart cart = cartRepository.getCartByUserId(userId);
        if(cart.getProducts().isEmpty()){
            System.out.println("cart is exists but empty");
            return "Cart is empty";
        }
        // check if product exists in cart
        System.out.println("Product id:" + product.getId());
        System.out.println("Products in cart: " + cart.getProducts().get(0).getId());
        List<Product> products = cart.getProducts();
        for (Product p : products){
            if(p.getId().equals(product.getId())){
                System.out.println("product exists in cart");
                cartRepository.deleteProductFromCart(cart.getId(), product);
                System.out.println("product deleted from cart");
                return "Product deleted from cart";
            }
        }
        // Product does not exist in cart

        { //TODO make this a test case
            System.out.println("product does not exist in cart");
            return "Product does not exist in cart";
        }



    }









}
