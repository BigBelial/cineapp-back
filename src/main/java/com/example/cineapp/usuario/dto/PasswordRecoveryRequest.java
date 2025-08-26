package com.example.cineapp.usuario.dto;

import lombok.Data;

@Data
public class PasswordRecoveryRequest {
    private String email;
    private String phone;
    private String newPassword;
}

