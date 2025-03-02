package com.example.repository;

import com.example.model.Cart;
import com.example.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class CartRepository extends MainRepository<Cart> {

    @Override
    protected String getDataPath() {
        return "src/main/java/com.example/data/cart.json";
    }

    @Override
    protected Class<Cart[]> getArrayType() {
        return Cart[].class;
    }

    // 6.4.2.1 Add New Cart
    public Cart addCart(Cart cart) {
        save(cart);
        return cart;
    }

    // 6.4.2.2 Get All Carts
    public ArrayList<Cart> getCarts() {
        return findAll();
    }

    // 6.4.2.3 Get Specific Cart (Using Loop)
    public Cart getCartById(UUID cartId) {
        for (Cart cart : findAll()) {
            if (cart.getId().equals(cartId)) {
                return cart;
            }
        }
        throw new NoSuchElementException("Cart not found with ID: " + cartId);
    }

    // 6.4.2.4 Get User’s Cart (Using Loop)
    public Cart getCartByUserId(UUID userId) {
        for (Cart cart : findAll()) {
            if (cart.getUserId().equals(userId)) {
                return cart;
            }
        }
        throw new NoSuchElementException("Cart not found for User ID: " + userId);
    }

    // 6.4.2.5 Add Product to Cart
    public void addProductToCart(UUID cartId, Product product) {
        ArrayList<Cart> carts = findAll();
        for (Cart cart : carts) {
            if (cart.getId().equals(cartId)) {
                cart.getProducts().add(product);
                saveAll(carts);
                return;
            }
        }
        throw new NoSuchElementException("Cart not found with ID: " + cartId);
    }

    // 6.4.2.6 Delete Product from Cart
    public void deleteProductFromCart(UUID cartId, Product product) {
        ArrayList<Cart> carts = findAll();
        for (Cart cart : carts) {
            if (cart.getId().equals(cartId)) {
                cart.getProducts().removeIf(p -> p.getId().equals(product.getId()));
                saveAll(carts);
                return;
            }
        }
        throw new NoSuchElementException("Cart not found with ID: " + cartId);
    }

    // 6.4.2.7 Delete the Cart (Using Loop)
    public void deleteCartById(UUID cartId) {
        ArrayList<Cart> carts = findAll();
        for (int i = 0; i < carts.size(); i++) {
            if (carts.get(i).getId().equals(cartId)) {
                carts.remove(i);
                saveAll(carts);
                return;
            }
        }
        throw new NoSuchElementException("Cart not found with ID: " + cartId);
    }
}
