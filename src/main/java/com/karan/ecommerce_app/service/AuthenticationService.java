package com.karan.ecommerce_app.service;

import com.karan.ecommerce_app.dto.user.CreateUserRequest;
import com.karan.ecommerce_app.dto.user.CreateUserResponse;
import com.karan.ecommerce_app.model.Cart;
import com.karan.ecommerce_app.model.User;
import com.karan.ecommerce_app.repository.CartRepository;
import com.karan.ecommerce_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {
    @Autowired
    UserRepository userRepo;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    PasswordEncoder encoder;

    @Transactional
    public Object register(CreateUserRequest createUserRequest) {
        //Creating and saving the user in DB
        User user = new User();
        user.setFirstName(createUserRequest.getFirstName());
        user.setLastName(createUserRequest.getLastName());
        user.setPhoneNumber(createUserRequest.getPhoneNumber());
        user.setEmail(createUserRequest.getEmail());
        user.setPassword(encoder.encode(createUserRequest.getPassword()));
        User savedUser = userRepo.save(user);

        // assigning an empty cart to the newly created user
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);


        //Creating and returning the createUserResponse
        CreateUserResponse createUserResponse = new CreateUserResponse();
        createUserResponse.setEmail(savedUser.getEmail());
        createUserResponse.setFirstName(savedUser.getFirstName());
        createUserResponse.setLastName(savedUser.getLastName());
        createUserResponse.setPhoneNumber(savedUser.getPhoneNumber());
        return createUserResponse;
    }
}
