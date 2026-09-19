package com.velinabliss.Velinabliss.repository;

import com.velinabliss.Velinabliss.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
