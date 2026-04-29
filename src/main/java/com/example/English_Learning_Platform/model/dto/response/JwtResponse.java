package com.example.English_Learning_Platform.model.dto.response;

import lombok.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long id;
    private String email;
    private String fullName;
    private Set<String> roles;
}
