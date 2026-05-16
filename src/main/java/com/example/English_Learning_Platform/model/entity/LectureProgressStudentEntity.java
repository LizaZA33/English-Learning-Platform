package com.example.English_Learning_Platform.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lecture_student", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"lecture_id", "student_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LectureProgressStudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private LectureEntity lectureEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity studentEntity;
    @Column(name = "progress_percent")
    private Integer progressPercent = 0;
}
