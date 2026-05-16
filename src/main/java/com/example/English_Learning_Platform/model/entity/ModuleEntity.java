package com.example.English_Learning_Platform.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @OneToMany(mappedBy = "moduleEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CourseGroupEntity> groups = new ArrayList<>();
    @OneToMany(mappedBy = "moduleEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LectureEntity> lectureEntities = new ArrayList<>();
}
