package com.demo.cognito.project.service;

import com.demo.cognito.project.model.UserLoginRequest;
import com.demo.cognito.project.model.UserRegistrationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.Map;

@Service
public class UserServiceImpl implements IUserService{

    private final CognitoIdentityProviderClient cognitoClient;

    @Value(value = "${aws.cognito.cognitoClientId}")
    private String cognitoClientId;

    @Value(value = "${aws.cognito.userPoolId}")
    private String cognitoUserPoolId;

    public UserServiceImpl(CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
    }

    public void registerUser(UserRegistrationRequest userRequest) {
        try {
            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .clientId(cognitoClientId)
                    .username(userRequest.getUsername())
                    .password(userRequest.getPassword())
                    .userAttributes(AttributeType.builder()
                            .name("email").value(userRequest.getEmail()).build()
                    )
                    .build();
           cognitoClient.signUp(signUpRequest);

        } catch (Exception e) {
            // Handle register errors
            throw new RuntimeException("Error registering user : " + e.getMessage(), e);
        }
    }

    public void confirmIdentity(UserRegistrationRequest userRequest) {
        try {
            AdminConfirmSignUpRequest adminConfirmSignUpRequest = AdminConfirmSignUpRequest.builder()
                    .userPoolId(cognitoUserPoolId)
                    .username(userRequest.getUsername())
                    .build();
            cognitoClient.adminConfirmSignUp(adminConfirmSignUpRequest);

        } catch (Exception e) {
            // Handle register errors
            throw new RuntimeException("Error confirming user : " + e.getMessage(), e);
        }
    }

    public AuthenticationResultType loginUser(UserLoginRequest userRequest) {
        try {
            InitiateAuthRequest initiateAuthRequest = InitiateAuthRequest.builder()
                    .clientId(cognitoClientId)
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .authParameters(
                            Map.of(
                                    "USERNAME", "username",
                                    "PASSWORD", "password"
                            )
                    )
                    .build();
            InitiateAuthResponse initiateAuthResponse = cognitoClient.initiateAuth(initiateAuthRequest);

            return initiateAuthResponse.authenticationResult();
        } catch (Exception e) {
            // Handle login errors
            throw new RuntimeException("Error login user : " + e.getMessage(), e);
        }
    }

    public String revokeToken(String refreshToken) {
        try {
            RevokeTokenRequest revokeTokenRequest = RevokeTokenRequest.builder()
                    .clientId(cognitoClientId)
                    .token(refreshToken)
                    .build();

            cognitoClient.revokeToken(revokeTokenRequest);

            return "Token revoked successfully";
        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Failed to revoke token: " + e.awsErrorDetails().errorMessage());
        }
    }
}
