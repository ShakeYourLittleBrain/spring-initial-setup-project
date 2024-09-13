package com.demo.cognito.project.controller;

import com.demo.cognito.project.model.RevokeToken;
import com.demo.cognito.project.model.UserLoginRequest;
import com.demo.cognito.project.model.UserRegistrationRequest;
import com.demo.cognito.project.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

@RestController
@RequestMapping("/api-v1/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest userRequest) {
        try {
            userService.registerUser(userRequest); // Register user with Cognito
            return ResponseEntity.ok("User registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error registering user: " + e.getMessage());
        }
    }
    @PostMapping("/confirm-identity")
    public ResponseEntity<String> confirmUser(@RequestBody UserRegistrationRequest userRequest) {
        try {
            userService.confirmIdentity(userRequest); // Confirm user with Cognito
            return ResponseEntity.ok("User confirmed successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error registering user: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResultType> loginUser(@RequestBody UserLoginRequest userRequest) {
        AuthenticationResultType authentication = userService.loginUser(userRequest);
        if (authentication != null) { // Login user with Cognito
            return ResponseEntity.ok(authentication);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RevokeToken tokenRequest) {
        try {
            String response = userService.revokeToken(tokenRequest.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error logout: " + e.getMessage());
        }
    }
}
