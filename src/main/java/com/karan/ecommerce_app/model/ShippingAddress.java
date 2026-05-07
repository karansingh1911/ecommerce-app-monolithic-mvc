package com.karan.ecommerce_app.model;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
    public class ShippingAddress {
    private String fullName;
    private String phone;

    private String line1;
    private String line2;

    private String city;
    private String state;
    private String pincode;
    }

