package com.example.service;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.repository.ProductRepository;
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
    private OrderService orderService;

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
        //FIXME i uncommented the below code so should i comment?
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    public List<Order> getOrdersByUserId(UUID userId){
        return userRepository.getOrdersByUserId(userId);
    }

    @Autowired
    CartService cartService;
    //FIXME make this a cart service not cart repo
    public void addOrderToUser(UUID userId){
        //FIXME for the test cases incase no products?
    //FIXME add checking user exists etc before everything
        if(userRepository.getUserById(userId) == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        //create cart if it doesnt exist
        if(cartRepository.getCartByUserId(userId) == null){ //FIXME this is handled in getcartbyuserid
            System.out.println("cart not found");
            List <Product> products = new ArrayList<>();
            Cart cart = new Cart(userId,products);
            cartRepository.addCart(cart);
        }
        //get the cart by user id
        Cart cart = cartService.getCartByUserId(userId);

        //get the products from the cart
        List<Product> products = cart.getProducts();
        //get the total price of the products
        double totalPrice = 0;
        for (Product product : products){
            totalPrice += product.getPrice();
        }
        //create a new order
        Order order = new Order(userId,totalPrice, products);

        //add the order to the user
        userRepository.addOrderToUser(userId, order);

//        check cart services keda to retreive products and use it to initialise an order
//        call the cart by user ID to get the products
//        initialise an order with total price and the new arraylist (use the prices)




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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        //check if order exists
        if(userRepository.getOrdersByUserId(userId).isEmpty()){
            System.out.println("No orders found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }

        userRepository.removeOrderFromUser(userId, orderId);
    }

    public void deleteUserById(UUID userId){
        //ensure it is not null
        User user = userRepository.getUserById(userId);
        if (user!=null)
        {
            userRepository.deleteUserById(userId);
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
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
