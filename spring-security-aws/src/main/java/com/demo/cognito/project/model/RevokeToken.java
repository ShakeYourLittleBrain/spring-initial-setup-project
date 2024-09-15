package com.demo.cognito.project.model;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RevokeToken {
    private String refreshToken;
}
