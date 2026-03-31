package com.example.English_Learning_Platform.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lecture_students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LectureStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @Column(name = "progress_percent")
    private Integer progressPercent = 0;
}
