package com.example.English_Learning_Platform.security;

import com.example.English_Learning_Platform.model.entity.LessonEntity;
import com.example.English_Learning_Platform.model.entity.UserEntity;
import com.example.English_Learning_Platform.model.enums.Role;
import com.example.English_Learning_Platform.repository.LessonRepository;
import com.example.English_Learning_Platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("lessonSecurity")
@RequiredArgsConstructor
public class LessonSecurity {

    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    public boolean hasAccessToLesson(Long lessonId) {
        UserEntity currentUser = getCurrentUser();
        if (currentUser == null) return false;
        if (currentUser.getRoles().contains(Role.ADMIN)) return true;
        LessonEntity lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null) return false;
        if (lesson.getOwner() != null && lesson.getOwner().getId().equals(currentUser.getId())) {
            return true;
        }
        if (lesson.getTeacherEntity() != null &&
                lesson.getTeacherEntity().getUserEntity().getId().equals(currentUser.getId())) {
            return true;
        }
        return currentUser.getRoles().contains(Role.STUDENT);
    }

    public boolean canModifyLesson(Long lessonId) {
        UserEntity currentUser = getCurrentUser();
        if (currentUser == null) return false;
        if (currentUser.getRoles().contains(Role.ADMIN)) return true;
        LessonEntity lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null) return false;
        if (lesson.getOwner() != null && lesson.getOwner().getId().equals(currentUser.getId())) {
            return true;
        }
        if (lesson.getTeacherEntity() != null &&
                lesson.getTeacherEntity().getUserEntity().getId().equals(currentUser.getId())) {
            return true;
        }
        return false;
    }
}