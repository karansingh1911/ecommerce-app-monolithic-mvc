package com.karan.ecommerce_app.repository;

import com.karan.ecommerce_app.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByProductNameContainingIgnoreCaseOrProductDescriptionContainingIgnoreCaseOrProductBrandContainingIgnoreCase(String name,String description,String brand, Pageable pageable);
}
