package com.example.cineapp.usuario.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String lastname;
    private String phone;
}
