package com.karan.ecommerce_app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;



@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false,length = 1000)
    private String productDescription;

    @Column(nullable = false)
    private String productCategory;

    @Column(nullable = false)
    private String productBrand;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime productReleaseDate;

    @Column(nullable = false)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime productUpdateDate;

    @NotNull
    @Positive
    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = true)
    private String productImageType;

    @Lob
    @JsonIgnore
    @Basic(fetch=FetchType.LAZY)
    @Column(nullable = true)
    private byte[] productImage;

    public boolean isAvailable() {
        return stockQuantity != null && stockQuantity > 0;
    }

    @PrePersist
    public void onCreate(){
        this.productReleaseDate= LocalDateTime.now();
        this.productUpdateDate= LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate(){
        this.productUpdateDate=LocalDateTime.now();
    }


}
