package com.example.English_Learning_Platform.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lessons")
@Getter
@Setter
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(length = 2000)
    private String description;
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    OrphanRemoval — параметр в JPA (Java Persistence API),
//    который позволяет автоматически удалять дочерние сущности, если на них больше нет ссылок.
    private List<Flashcard> flashcards = new ArrayList<>();
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LessonStudent> studentProgress = new ArrayList<>();
}
