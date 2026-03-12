package com.example.English_Learning_Platform.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "flashcards")
@Getter
@Setter
public class Flashcard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
    @Column(nullable = false)
    private String term;
    @Column(nullable = false, length = 2000)
    private String definition;
    @Column(length = 2000)
    private String example;
    @Column(length = 500)
    private String translation;
    @Column(name = "difficulty")
    private Integer difficulty = 1;
}
