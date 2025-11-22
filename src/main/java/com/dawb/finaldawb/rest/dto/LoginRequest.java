package com.dawb.finaldawb.rest.dto;

import jakarta.validation.constraints.NotBlank;

// DTO (Data Transfer Object) para la solicitud de inicio de sesión
public class LoginRequest {
    @NotBlank
    private String usernameOrEmail; // Puede ser el nombre de usuario o el email

    @NotBlank
    private String password;

    // Constructores, Getters y Setters
    public LoginRequest() {
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}