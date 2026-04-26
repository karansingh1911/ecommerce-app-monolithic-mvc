package com.karan.ecommerce_app.controller;

import com.karan.ecommerce_app.dto.user.*;
import com.karan.ecommerce_app.model.CustomUserDetails;
import com.karan.ecommerce_app.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.ResourceBundle;

@RequestMapping("users")
@RestController
@Validated
public class UserController {
    @Autowired
    UserService userService;

    //admin-related controller methods

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@Positive @PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserDTO> getUserById(@Positive @PathVariable Long userId) {
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @GetMapping("{userId}/profile-image")
    public ResponseEntity<byte[]> getUserImageById(@Positive @PathVariable Long userId) {
        byte[] image = userService.getUserImageById(userId);

        return ResponseEntity.ok().header("Content-Type", "image/jpeg") // or image/png
                .body(image);
    }


    // user-related controller methods
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(userService.getMe(userDetails.getUserId()), HttpStatus.OK);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.deleteUserByEmail(userDetails.getUsername(), userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/profile-image")
    public ResponseEntity<byte[]> getMyProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails) {

        byte[] image = userService.getUserImageById(userDetails.getUserId());

        return ResponseEntity.ok().header("Content-Type", "image/jpeg").body(image);
    }

    @PutMapping("/me/profile-image")
    public ResponseEntity<String> uploadProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam MultipartFile image) {
        return new ResponseEntity<>(userService.uploadProfileImage(userDetails.getUserId(), image), HttpStatus.OK);
    }

    @PatchMapping("/me")
    public ResponseEntity<UpdateUserResponseDTO> updateUserById(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
        return new ResponseEntity<>(userService.updateUserByUserId(userDetails.getUserId(), updateUserRequestDTO), HttpStatus.OK);
    }

    @PatchMapping("/me/change-password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        userService.changePassword(userDetails.getUserId(), updatePasswordRequestDTO);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}






