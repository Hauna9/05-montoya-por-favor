package com.example.service;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class CartService extends MainService<Cart> {

    CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        super(cartRepository);
        this.cartRepository = cartRepository;
    }

    // 7.4.2.1 Add Cart
    public Cart addCart(Cart cart) {
        return cartRepository.addCart(cart);
    }

    // 7.4.2.2 Get All Carts
    public ArrayList<Cart> getCarts() {
        return cartRepository.getCarts();
    }

    // 7.4.2.3 Get a Specific Cart
    public Cart getCartById(UUID cartId) {
        Cart cart = cartRepository.getCartById(cartId);
        if (cart == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
        }
        return cart;
    }

    // 7.4.2.4 Get a User’s Cart
    public Cart getCartByUserId(UUID userId) {
        Cart cart = cartRepository.getCartByUserId(userId);
        if (cart == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found for this user");
        }
        return cart;
    }

    // 7.4.2.5 Add Product to the Cart
    public void addProductToCart(UUID cartId, Product product) {
        cartRepository.addProductToCart(cartId, product);
    }

    // 7.4.2.6 Delete Product from the Cart
    public void deleteProductFromCart(UUID cartId, Product product) {
        cartRepository.deleteProductFromCart(cartId, product);
    }

    // 7.4.2.7 Delete the Cart
    public void deleteCartById(UUID cartId) {
        cartRepository.deleteCartById(cartId);
    }
}
