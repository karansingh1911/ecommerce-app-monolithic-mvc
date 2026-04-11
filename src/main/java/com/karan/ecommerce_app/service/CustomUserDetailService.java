package com.karan.ecommerce_app.service;

import com.karan.ecommerce_app.model.CustomUserDetails;
import com.karan.ecommerce_app.model.User;
import com.karan.ecommerce_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("User not found with email "+ email));


    }
}
