package com.karan.ecommerce_app.dto.ShippingAddress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ShippingAddressResponseDTO {

    private String fullName;
    private String phone;

    private String line1;
    private String line2;

    private String city;
    private String state;
    private String pincode;
}
