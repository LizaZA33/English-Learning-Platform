package com.example.English_Learning_Platform.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @Size(min = 2, max = 100, message = "Кол-во символов в имени должно быть от 2 до 100")
    private String firstName;
    @Size(min = 2, max = 100, message = "Кол-во символов в фамилии должно быть от 2 до 100")
    private String lastName;
    @Size(min = 2, max = 100, message = "Кол-во символов в отчестве должно быть от 2 до 100")
    private String patronymic;
    @Email(message = "Почта должна быть валидной")
    private String email;
    @Size(min = 8, message = "Пароль должен быть больше 8ми символов")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "Пароль должен содержать хотя бы одну цифру, одну букву в верхнем и нижнем регистре, и минимум один специальный символ")
    private String password;
}
