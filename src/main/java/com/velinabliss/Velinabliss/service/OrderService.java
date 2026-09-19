
        package com.velinabliss.Velinabliss.service;

import com.velinabliss.Velinabliss.entity.Cart;
import com.velinabliss.Velinabliss.entity.CartItem;
import com.velinabliss.Velinabliss.entity.Order;
import com.velinabliss.Velinabliss.entity.OrderItem;
import com.velinabliss.Velinabliss.entity.Product;
import com.velinabliss.Velinabliss.entity.User;

import com.velinabliss.Velinabliss.repository.CartItemRepository;
import com.velinabliss.Velinabliss.repository.CartRepository;
import com.velinabliss.Velinabliss.repository.OrderItemRepository;
import com.velinabliss.Velinabliss.repository.OrderRepository;
import com.velinabliss.Velinabliss.repository.ProductRepository;
import com.velinabliss.Velinabliss.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }


    // ==========================================
    // CUSTOMER DETAILS
    // ==========================================

    private void setCustomerDetails(Order order) {

        if (order.getUserId() == null) {
            return;
        }

        User user =
                userRepository.findById(
                        order.getUserId()
                ).orElse(null);

        if (user == null) {
            return;
        }

        order.setCustomerName(
                user.getName()
        );

        order.setCustomerEmail(
                user.getEmail()
        );

        order.setCustomerMobile(
                user.getMobile()
        );

        order.setCustomerAddress(
                user.getAddress()
        );
    }


    // ==========================================
    // CREATE ORDER
    // ==========================================

    public Order createOrder(Order order) {

        Order savedOrder =
                orderRepository.save(order);

        setCustomerDetails(savedOrder);

        return savedOrder;
    }


    // ==========================================
    // GET ALL ORDERS
    // ==========================================

    public List<Order> getAllOrders() {

        List<Order> orders =
                orderRepository.findAll();

        for (Order order : orders) {

            List<OrderItem> items =
                    orderItemRepository
                            .findByOrderId(
                                    order.getId()
                            );

            order.setItems(items);

            // Customer details
            setCustomerDetails(order);
        }

        return orders;
    }


    // ==========================================
    // GET ORDER BY ID
    // ==========================================

    public Order getOrderById(Long id) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        List<OrderItem> items =
                orderItemRepository
                        .findByOrderId(
                                order.getId()
                        );

        order.setItems(items);

        // Customer details
        setCustomerDetails(order);

        return order;
    }


    // ==========================================
    // GET ORDERS BY USER
    // ==========================================

    public List<Order> getOrdersByUserId(
            Long userId) {

        List<Order> orders =
                orderRepository
                        .findByUserId(userId);

        for (Order order : orders) {

            List<OrderItem> items =
                    orderItemRepository
                            .findByOrderId(
                                    order.getId()
                            );

            order.setItems(items);

            // Customer details
            setCustomerDetails(order);
        }

        return orders;
    }


    // ==========================================
    // CHECKOUT CART
    // ==========================================

    public Order checkout(Long userId) {

        Cart cart =
                cartRepository
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Cart not found"
                                )
                        );


        List<CartItem> cartItems =
                cartItemRepository
                        .findByCartId(
                                cart.getId()
                        );


        if (cartItems.isEmpty()) {

            throw new RuntimeException(
                    "Cart is empty"
            );
        }


        BigDecimal totalAmount =
                BigDecimal.ZERO;


        // ==========================================
        // CREATE ORDER
        // ==========================================

        Order order =
                new Order();

        order.setUserId(userId);

        order.setStatus("PLACED");

        order.setOrderDate(
                LocalDateTime.now()
        );

        order =
                orderRepository.save(order);


        // ==========================================
        // CART ITEMS -> ORDER ITEMS
        // ==========================================

        for (CartItem cartItem : cartItems) {

            Product product =
                    productRepository
                            .findById(
                                    cartItem.getProductId()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Product not found: "
                                                    + cartItem.getProductId()
                                    )
                            );


            BigDecimal itemTotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );


            totalAmount =
                    totalAmount.add(
                            itemTotal
                    );


            // ==========================================
            // CREATE ORDER ITEM
            // ==========================================

            OrderItem orderItem =
                    new OrderItem();


            orderItem.setOrderId(
                    order.getId()
            );


            orderItem.setProductId(
                    product.getId()
            );


            orderItem.setQuantity(
                    cartItem.getQuantity()
            );


            orderItem.setPrice(
                    product.getPrice()
                            .doubleValue()
            );


            // Size
            orderItem.setSize(
                    cartItem.getSize()
            );


            // Color
            orderItem.setColor(
                    cartItem.getColor()
            );


            orderItemRepository.save(
                    orderItem
            );
        }


        // ==========================================
        // TOTAL AMOUNT
        // ==========================================

        order.setTotalAmount(
                totalAmount.doubleValue()
        );


        order =
                orderRepository.save(order);


        // ==========================================
        // CUSTOMER DETAILS
        // ==========================================

        setCustomerDetails(order);


        // ==========================================
        // EMPTY CART
        // ==========================================

        cartItemRepository.deleteAll(
                cartItems
        );


        return order;
    }


    // ==========================================
    // UPDATE ORDER STATUS
    // ==========================================

    public Order updateOrderStatus(
            Long orderId,
            String status) {

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found: "
                                                + orderId
                                )
                        );


        order.setStatus(status);


        order =
                orderRepository.save(order);


        // Customer details
        setCustomerDetails(order);


        return order;
    }

}

