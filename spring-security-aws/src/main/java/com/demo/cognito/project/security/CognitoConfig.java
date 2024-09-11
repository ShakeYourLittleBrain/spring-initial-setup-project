package com.demo.cognito.project.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
public class CognitoConfig {

    @Value(value = "aws.cognito.accessKey")
    private String cognitoAccessKey;

    @Value(value = "aws.cognito.secretKey")
    private String cognitoSecretKey;

    @Bean
    public CognitoIdentityProviderClient cognitoIdentityProviderClient() {
    return CognitoIdentityProviderClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(() -> AwsBasicCredentials.create(cognitoAccessKey, cognitoSecretKey))
            .build();
    }

}



