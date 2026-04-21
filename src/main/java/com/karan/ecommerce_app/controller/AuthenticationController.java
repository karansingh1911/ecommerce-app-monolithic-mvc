package com.karan.ecommerce_app.controller;

import com.karan.ecommerce_app.dto.auth.AuthRequest;
import com.karan.ecommerce_app.dto.user.CreateUserRequest;
import com.karan.ecommerce_app.dto.user.UserDTO;
import com.karan.ecommerce_app.enums.Role;
import com.karan.ecommerce_app.model.User;
import com.karan.ecommerce_app.service.AuthenticationService;
import com.karan.ecommerce_app.service.JwtService;
import com.karan.ecommerce_app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationService authService;
    @Autowired
    JwtService jwtService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestPart("createUserRequest") CreateUserRequest createUserRequest) {
        return new ResponseEntity<>(authService.register(createUserRequest), HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AuthRequest authRequest) {
        // throws badCredentials error if user credentials not found inside the DB by the
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        UserDTO userDTO = userService.getUserByEmail(authRequest.getEmail());
        //passing the authenticated email
        String email = authentication.getName();
        Role role= userDTO.getRole();
        String jwtToken= jwtService.generateToken(authRequest.getEmail(),role);
        return new ResponseEntity<>(jwtToken,HttpStatus.ACCEPTED);


    }
}
