package com.example.English_Learning_Platform.model.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileRequest {
    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 100, message = "Кол-во символов в имени должно быть от 2 до 100")
    private String firstName;
    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 100, message = "Кол-во символов в фамилии должно быть от 2 до 100")
    private String lastName;
    private String patronymic;
    private LocalDate dateOfBirth;
    private String phoneNumber;
}
