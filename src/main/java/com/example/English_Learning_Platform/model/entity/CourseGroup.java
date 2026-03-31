package com.example.English_Learning_Platform.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(name = "invite_code", unique = true)
    private String inviteCode;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (inviteCode == null) {
            inviteCode = generateInviteCode();
        }
    }
    private String generateInviteCode() {
        return "GRP-" + System.currentTimeMillis() % 10000;
    }
}
