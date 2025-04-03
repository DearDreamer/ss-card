package com.example.membership.model.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class LoginResponse {
    private String username;
    private String token;
} 