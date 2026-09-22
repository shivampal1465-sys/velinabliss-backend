package com.velinabliss.Velinabliss.controller;

import com.velinabliss.Velinabliss.entity.Cart;
import com.velinabliss.Velinabliss.entity.CartItem;
import com.velinabliss.Velinabliss.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Add product to cart
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam String size,
            @RequestParam String color) {

        return ResponseEntity.ok(
                cartService.addToCart(
                        userId,
                        productId,
                        quantity,
                        size,
                        color
                )
        );
    }
    // Get cart
    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                cartService.getCart(userId)
        );
    }
    @PutMapping("/item/{itemId}")
    public ResponseEntity<CartItem> updateQuantity(
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {

        CartItem item = cartService.updateQuantity(itemId, quantity);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<String> removeItem(@PathVariable Long itemId) {

        cartService.removeItem(itemId);
        return ResponseEntity.ok("Item removed from cart");
    }
}