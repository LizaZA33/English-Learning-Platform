package com.example.English_Learning_Platform.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "teacher")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
public class Teacher extends User{
    @Id
    private Long id;
    @NotBlank(message = "Обязательно введите имя")
    @Column(name = "firstName", nullable = false)
    @Size(min = 2, max = 100, message = "Кол-во символов в имени должно быть от 2 до 100")
    private String firstName;
    @NotBlank(message = "Обязательно введите фамилию")
    @Size(min = 2, max = 100, message = "Кол-во символов в фамилии должно быть от 2 до 100")
    @Column(name = "lastName", nullable = false)
    private String lastName;
    @Column(name = "patronymic")
    private String patronymic;
    @Column(name = "number", length = 11)
    private char [] number;
}
