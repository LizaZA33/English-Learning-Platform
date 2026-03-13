package com.example.English_Learning_Platform.model.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}
