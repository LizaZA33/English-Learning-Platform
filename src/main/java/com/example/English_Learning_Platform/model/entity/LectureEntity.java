package com.example.English_Learning_Platform.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "lecture")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LectureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private ModuleEntity moduleEntity;
    @OneToMany(mappedBy = "lectureEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LectureProgressStudentEntity> studentProgress = new HashSet<>();
}
