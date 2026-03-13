package com.example.English_Learning_Platform.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "student")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
public class Student extends User{
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
    @Size(min = 2, max = 100, message = "Кол-во символов в отчестве должно быть от 2 до 100")
    private String patronymic;
    @Column(name = "number", length = 11)
    private char [] number;
    @Past(message = "Дата рождения должна быть в прошлом")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @ManyToMany
    @JoinTable(
            name = "student_groups",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private List<CourseGroup> groups = new ArrayList<>();
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LectureStudent> lectureProgress = new ArrayList<>();
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LessonStudent> lessonProgress = new ArrayList<>();
}
