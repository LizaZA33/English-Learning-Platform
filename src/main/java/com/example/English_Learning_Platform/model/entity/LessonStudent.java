package com.example.English_Learning_Platform.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lesson_students")
@Getter
@Setter
public class LessonStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @Column(name = "progress_percent")
    private Integer progressPercent = 0;
}