package com.example.English_Learning_Platform.model.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_group", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "group_id"})
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentGroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity studentEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private CourseGroupEntity group;
}
