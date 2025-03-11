package com.example.repository;

import com.example.model.Cart;
import com.example.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class CartRepository extends MainRepository<Cart> {

    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/carts.json";
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

            return findAll()  // Reload from data source to avoid stale objects
                    .stream()
                    .filter(cart -> cart.getId().equals(cartId))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Cart not found"));

    }

    public Cart getCartByUserId(UUID userId) {
        return findAll().stream()
                .filter(cart -> cart.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No cart found for user"));
    }


    // 6.4.2.5 Add Product to Cart
    public void addProductToCart(UUID cartId, Product product) {
        Cart cart = getCartById(cartId);
        if (cart == null) {
            throw new NoSuchElementException("Cart not found with ID: " + cartId);
        }


        cart.getProducts().add(product);


        // Save the updated cart list
        ArrayList<Cart> carts = findAll();
        carts.removeIf(c -> c.getId().equals(cartId)); // Remove old cart
        carts.add(cart); // Add updated cart
        saveAll(carts); // Persist the updated list
    } //FIXME do this in changes like editing order or updates etc.. to persist??


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
