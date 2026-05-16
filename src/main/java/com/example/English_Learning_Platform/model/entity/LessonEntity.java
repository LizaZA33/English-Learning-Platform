package com.example.English_Learning_Platform.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lesson")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private TeacherEntity teacherEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;
    @Builder.Default
    @OneToMany(mappedBy = "lessonEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FlashcardEntity> flashcardEntities = new HashSet<>();
    @Builder.Default
    @OneToMany(mappedBy = "lessonEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LessonProgressStudentEntity> studentProgress = new HashSet<>();
}