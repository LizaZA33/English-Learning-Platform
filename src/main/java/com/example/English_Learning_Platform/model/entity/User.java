package com.example.English_Learning_Platform.model.entity;

import com.example.English_Learning_Platform.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank (message = "Требуется почта")
    @Email(message = "Почта должна быть валидной")
    @Column(unique = true, nullable = false)
    private String email;
    @NotBlank (message = "Требуется пароль")
    @Size(min = 8, message = "Пароль должен быть больше 8ми символов")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "Пароль должен содержать хотя бы одну цифру, одну букву в верхнем и нижнем регистре, и минимум один специальный символ")
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "role_id", nullable = false)
    private Role role;
    @CreatedDate
    @Column(name = "created_at", nullable =  false, updatable = false)
    private LocalDateTime createAt;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Teacher teacher;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Student student;
    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
    }
}

