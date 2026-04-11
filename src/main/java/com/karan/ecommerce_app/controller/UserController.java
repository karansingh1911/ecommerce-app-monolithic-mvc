package com.karan.ecommerce_app.controller;

import com.karan.ecommerce_app.dto.user.UpdatePasswordRequestDTO;
import com.karan.ecommerce_app.dto.user.UpdateUserRequestDTO;
import com.karan.ecommerce_app.dto.user.UpdateUserResponseDTO;
import com.karan.ecommerce_app.dto.user.UserDTO;
import com.karan.ecommerce_app.model.CustomUserDetails;
import com.karan.ecommerce_app.service.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.ResourceBundle;

@RequestMapping("users")
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(userService.getllUsers(), HttpStatus.OK);
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.ACCEPTED);
    }

    @GetMapping("{userId}/profile-image")
    public ResponseEntity<byte[]> getUserImageById(@PathVariable Long userId) {
        byte[] image = userService.getUserImageById(userId);

        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg") // or image/png
                .body(image);
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccount(@AuthenticationPrincipal CustomUserDetails userDetails){
        return new ResponseEntity<>(userService.deleteUserByEmail(userDetails.getUsername()),HttpStatus.NO_CONTENT);
    }



    @PutMapping("/{id}/profile-image")
    public ResponseEntity<String> uploadProfileImage(@PathVariable int id, @RequestParam MultipartFile image) {
        return new ResponseEntity<>(userService.uploadProfileImage(id, image), HttpStatus.OK);
    }


    @DeleteMapping("/{userID}")
    public ResponseEntity<String> deleteUserById(@PathVariable int userId) {
        return new ResponseEntity<>(userService.deleteUserById(userId), HttpStatus.NO_CONTENT);
    }


    @PatchMapping("/{userId}")
    public ResponseEntity<UpdateUserResponseDTO> updateUserById(@PathVariable long userId,
                                                                @Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO){
        return new ResponseEntity(userService.updateUserByUserId(userId,updateUserRequestDTO),HttpStatus.OK);
    }

    @PatchMapping("{userId}/change-Password")
    public ResponseEntity<Void> changePassword(@PathVariable long userId, @RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO){
        userService.changePassword(userId,updatePasswordRequestDTO);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}






