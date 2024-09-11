package com.demo.cognito.project.service;

import com.demo.cognito.project.model.UserLoginRequest;
import com.demo.cognito.project.model.UserRegistrationRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

public interface IUserService {
    void registerUser(UserRegistrationRequest userRequest);
    void confirmIdentity(UserRegistrationRequest userRequest);
    AuthenticationResultType loginUser(UserLoginRequest userRequest);
}
