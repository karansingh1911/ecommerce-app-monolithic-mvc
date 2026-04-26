package com.karan.ecommerce_app.service;

import com.karan.ecommerce_app.dto.user.*;
import com.karan.ecommerce_app.model.User;
import com.karan.ecommerce_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepo;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Cacheable(value = "userList", key = "'all'")
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers() {
        List<UserDTO> userDTOList = userRepo.findAll().stream().map(user -> {
            return UserDTO.builder().firstName(user.getFirstName()).lastName(user.getLastName()).email(user.getEmail()).phoneNumber(user.getPhoneNumber()).role(user.getRole()).build();
        }).toList();
        return userDTOList ;
    }

    @Caching(evict = {@CacheEvict(value = "userById", key = "#userId"), @CacheEvict(value = "userByEmail", allEntries = true), @CacheEvict(value = "userList", allEntries = true), @CacheEvict(value = "userImages", key = "#userId")})
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserById(long userId) {
        userRepo.deleteById(userId);
    }

    @Cacheable(value = "userByEmail", key = "#email.toLowerCase()")
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {

        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        return UserDTO.builder().firstName(user.getFirstName()).lastName(user.getLastName()).email(user.getEmail()).phoneNumber(user.getPhoneNumber()).role(user.getRole()).build();
    }

    @Cacheable(value = "userById", key = "#userId")
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO getUserById(long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        return UserDTO.builder().firstName(user.getFirstName()).lastName(user.getLastName()).email(user.getEmail()).phoneNumber(user.getPhoneNumber()).role(user.getRole()).build();

    }

    @CacheEvict(value = "userImages", key = "#id")
    @Transactional
    public String uploadProfileImage(long id, MultipartFile image) {

        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found with the userID: " + id));
        try {
            user.setProfileImage(image.getBytes());
            user.setImageType(image.getContentType());
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image");
        }
        userRepo.save(user);
        return "Profile updated successfully";

    }

    //when the user is deleted with certain email then all users must update, userByEmail should update just by that single
    // record and so should happen with the userById with just userId, further the userList should be updated and Image should
    // only be removed/updated from the certain key with user::id
    //NOTE: Nuking every namespace isn't required for it, just a record in the namespace should be affected in some places and
    // the list
    @Caching(evict = {@CacheEvict(value = "userByEmail", key = "#email.toLowerCase()"), @CacheEvict(value = "userById",key =
            "#userId"),
            @CacheEvict(value = "userList", allEntries = true), @CacheEvict(value = "userImages",key = "#userId")})
    @Transactional
    public void deleteUserByEmail(String email,Long userId) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with the id: " + email));
        userRepo.delete(user);
    }

    @Cacheable(value = "userImages", key = "#userId")
    @Transactional(readOnly = true)
    public byte[] getUserImageById(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found with the email: " + userId));
        byte[] image = user.getProfileImage();
        if (image == null) {
            throw new RuntimeException("Profile image not found for this user");
        }
        return image;


    }

    @Caching(evict = {@CacheEvict(value = "userById", key = "#userId"), @CacheEvict(value = "userByEmail", allEntries = true), @CacheEvict(value = "userList", allEntries = true)})
    @Transactional
    public UpdateUserResponseDTO updateUserByUserId(long userId, UpdateUserRequestDTO updateUserRequestDTO) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Update only provided fields
        if (updateUserRequestDTO.getFirstName() != null) {
            user.setFirstName(updateUserRequestDTO.getFirstName());
        }

        if (updateUserRequestDTO.getEmail() != null && !updateUserRequestDTO.getEmail().equals(user.getEmail())) {
            user.setEmail(updateUserRequestDTO.getEmail());
        }
        if (updateUserRequestDTO.getLastName() != null) {
            user.setLastName(updateUserRequestDTO.getLastName());
        }
        if (updateUserRequestDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(updateUserRequestDTO.getPhoneNumber());
        }

        User savedUser = userRepo.save(user);
        return UpdateUserResponseDTO.builder().firstName(savedUser.getFirstName()).lastName(savedUser.getLastName()).email(savedUser.getEmail()).phoneNumber(savedUser.getPhoneNumber()).build();
    }


    @Transactional
    public void changePassword(long userId, UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 1. Verify old password
        if (!encoder.matches(updatePasswordRequestDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }

        // 2. Prevent reuse of same password
        if (encoder.matches(updatePasswordRequestDTO.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("New password must be different from old password");
        }

        // 3. Encode and update
        String encodedPassword = encoder.encode(updatePasswordRequestDTO.getNewPassword());
        user.setPassword(encodedPassword);

        userRepo.save(user);
    }
    @Transactional(readOnly = true)
    @Cacheable(value = "userById",key = "#userId")
    public UserResponseDTO getMe(Long userId) {
        User me = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("Resource not found"));
        return UserResponseDTO.builder().firstName(me.getFirstName()).lastName(me.getLastName()).email(me.getEmail()).phoneNumber(me.getPhoneNumber()).build();
    }
}
