package com.example.English_Learning_Platform.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lesson_student", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"lesson_id", "student_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgressStudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private LessonEntity lessonEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity studentEntity;
    @Column(name = "progress_percent")
    private Integer progressPercent = 0;
    @Column(name = "cards_studied")
    private Integer cardsStudied = 0;
    @Column(name = "total_cards")
    private Integer totalCards = 0;
}
