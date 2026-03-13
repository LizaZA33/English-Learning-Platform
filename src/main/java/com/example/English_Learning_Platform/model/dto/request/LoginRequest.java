package com.example.English_Learning_Platform.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class LoginRequest {
    @NotBlank (message = "Требуется почта")
    @Email(message = "Почта должна быть валидной")
    private String email;
    @NotBlank (message = "Требуется пароль")
    private String password;
}
