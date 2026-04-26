package com.karan.ecommerce_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable= false,unique = true)// the userId will be foreign key
    private User user;

    //when cartItem removed from the list then whole row gets deleted automatically
    @OneToMany(mappedBy = "cart",cascade =CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<CartItem> cartItemsList= new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime cartCreatedAt;

    @Column(nullable = false, updatable = true)
     private LocalDateTime cartUpdatedAt;

    public void addItem(CartItem item){
        cartItemsList.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItem item){
        this.cartItemsList.remove(item);
        item.setCart(null);

    }


    //JPA lifecycle methods
    @PrePersist
    public void onCreate(){
        this.cartCreatedAt=LocalDateTime.now();
        this.cartUpdatedAt=LocalDateTime.now();

    }
    @PreUpdate
    public void onUpdate(){
        this.cartUpdatedAt= LocalDateTime.now();
    }

}