package com.velinabliss.Velinabliss.service;

import com.velinabliss.Velinabliss.entity.Cart;
import com.velinabliss.Velinabliss.entity.CartItem;
import com.velinabliss.Velinabliss.repository.CartItemRepository;
import com.velinabliss.Velinabliss.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    // ==========================================
    // ADD PRODUCT TO CART
    // ==========================================

    public Cart addToCart(
            Long userId,
            Long productId,
            Integer quantity,
            String size,
            String color) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {

                    Cart newCart = new Cart();

                    newCart.setUserId(userId);

                    return cartRepository.save(newCart);
                });

        // Check:
        // Product + Size + Color
        Optional<CartItem> existingItem =
                cartItemRepository
                        .findByCartIdAndProductIdAndSizeAndColor(
                                cart.getId(),
                                productId,
                                size,
                                color
                        );

        if (existingItem.isPresent()) {

            // Same product + same size + same color
            // quantity increase hogi

            CartItem cartItem = existingItem.get();

            cartItem.setQuantity(
                    cartItem.getQuantity() + quantity
            );

            cartItemRepository.save(cartItem);

        } else {

            // Different size/color hone par
            // new cart item create hoga

            CartItem cartItem = new CartItem();

            cartItem.setProductId(productId);

            cartItem.setQuantity(quantity);

            cartItem.setCart(cart);

            cartItem.setSize(size);

            cartItem.setColor(color);

            cartItemRepository.save(cartItem);
        }

        return cartRepository
                .findById(cart.getId())
                .orElseThrow();
    }


    // ==========================================
    // GET USER CART
    // ==========================================

    public Cart getCart(Long userId) {

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {

                    Cart cart = new Cart();

                    cart.setUserId(userId);

                    return cartRepository.save(cart);
                });
    }


    // ==========================================
    // UPDATE CART QUANTITY
    // ==========================================

    public CartItem updateQuantity(
            Long itemId,
            Integer quantity) {

        CartItem item = cartItemRepository
                .findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cart item not found"
                        )
                );

        item.setQuantity(quantity);

        return cartItemRepository.save(item);
    }


    // ==========================================
    // REMOVE CART ITEM
    // ==========================================

    public void removeItem(Long itemId) {

        if (!cartItemRepository.existsById(itemId)) {

            throw new RuntimeException(
                    "Cart item not found"
            );
        }

        cartItemRepository.deleteById(itemId);
    }
}