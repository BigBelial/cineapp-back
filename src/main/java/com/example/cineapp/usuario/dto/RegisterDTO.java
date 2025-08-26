package com.example.cineapp.usuario.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String name;
    private String lastname;
    private String email;
    private String phone;
    private String password;
    private Boolean isSuperUser;
}

