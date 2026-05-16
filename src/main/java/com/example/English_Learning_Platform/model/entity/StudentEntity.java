package com.example.English_Learning_Platform.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity userEntity;
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    @Column(name = "patronymic", length = 100)
    private String patronymic;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;
    @Builder.Default
    @OneToMany(mappedBy = "studentEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentGroupEntity> studentGroupEntities = new HashSet<>();
    @Builder.Default
    @OneToMany(mappedBy = "studentEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LectureProgressStudentEntity> lectureProgress = new HashSet<>();
    @Builder.Default
    @OneToMany(mappedBy = "studentEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LessonProgressStudentEntity> lessonProgress = new HashSet<>();
}
