package com.example.English_Learning_Platform.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class RegisterRequest {
    @NotBlank(message = "Обязательно введите имя")
    @Size(min = 2, max = 100, message = "Кол-во символов в имени должно быть от 2 до 100")
    private String firstName;
    @NotBlank(message = "Обязательно введите фамилию")
    @Size(min = 2, max = 100, message = "Кол-во символов в фамилии должно быть от 2 до 100")
    private String lastName;
    @Size(min = 2, max = 100, message = "Кол-во символов в отчестве должно быть от 2 до 100")
    private String patronymic;
    @NotBlank (message = "Требуется почта")
    @Email(message = "Почта должна быть валидной")
    private String email;
    @NotBlank (message = "Требуется пароль")
    @Size(min = 8, message = "Пароль должен быть больше 8ми символов")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "Пароль должен содержать хотя бы одну цифру, одну букву в верхнем и нижнем регистре, и минимум один специальный символ")
    private String password;
}
