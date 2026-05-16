package com.example.English_Learning_Platform.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "course_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseGroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private TeacherEntity teacherEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private ModuleEntity moduleEntity;
    @Column(name = "invite_code", unique = true, length = 12)
    private String inviteCode;
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentGroupEntity> studentGroupEntities = new HashSet<>();
    @PrePersist
    public void generateInviteCode() {
        if (inviteCode == null) {
            this.inviteCode = UUID.randomUUID().toString().substring(0, 12);
        }
    }
}
