
        package com.velinabliss.Velinabliss.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String status;

    private Double totalAmount;
    private LocalDateTime orderDate;


    // =========================
    // ORDER ITEMS
    // =========================

    @Transient
    private List<OrderItem> items;


    // =========================
    // CUSTOMER DETAILS
    // =========================

    @Transient
    private String customerName;

    @Transient
    private String customerEmail;

    @Transient
    private String customerMobile;

    @Transient
    private String customerAddress;
}

