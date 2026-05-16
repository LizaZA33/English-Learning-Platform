package com.example.English_Learning_Platform.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "flashcard")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private LessonEntity lessonEntity;
    @Column(nullable = false, length = 100)
    private String term;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String definition;
    @Column(columnDefinition = "TEXT")
    private String example;
    @Column(length = 100)
    private String translation;
    @Column(name = "difficulty")
    @Min(1) @Max(5)
    private Integer difficulty = 1;
}
