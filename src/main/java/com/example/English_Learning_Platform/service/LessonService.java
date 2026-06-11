package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.exception.ValidationException;
import com.example.English_Learning_Platform.model.dto.mapper.LessonMapper;
import com.example.English_Learning_Platform.model.dto.request.LessonCreateRequest;
import com.example.English_Learning_Platform.model.dto.response.LessonResponse;
import com.example.English_Learning_Platform.model.entity.LessonEntity;
import com.example.English_Learning_Platform.model.entity.TeacherEntity;
import com.example.English_Learning_Platform.model.entity.UserEntity;
import com.example.English_Learning_Platform.model.enums.Role;
import com.example.English_Learning_Platform.repository.LessonRepository;
import com.example.English_Learning_Platform.repository.TeacherRepository;
import com.example.English_Learning_Platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonService {
    private final LessonRepository lessonRepository;
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final LessonMapper lessonMapper;

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
    }
    @Transactional
    public LessonResponse createLesson(LessonCreateRequest request) {
        UserEntity currentUser = getCurrentUser();
        LessonEntity lesson = LessonEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .owner(currentUser)
                .build();
        if (request.getTeacherId() != null) {
            TeacherEntity teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден"));
            boolean isOwner = teacher.getUserEntity().getId().equals(currentUser.getId());
            boolean isAdmin = currentUser.getRoles().contains(Role.ADMIN);
            if (isOwner || isAdmin) {
                lesson.setTeacherEntity(teacher);
            } else {
                throw new ValidationException("У вас нет прав создавать урок от имени этого учителя");
            }
        }
        else if (currentUser.getRoles().contains(Role.TEACHER) && currentUser.getTeacherEntity() != null) {
            lesson.setTeacherEntity(currentUser.getTeacherEntity());
        }
        LessonEntity saved = lessonRepository.save(lesson);
        log.info("Создан урок '{}' пользователем {}", saved.getTitle(), currentUser.getEmail());

        return lessonMapper.toResponse(saved);
    }

    public Page<LessonResponse> getPersonalLessons(Pageable pageable) {
        UserEntity currentUser = getCurrentUser();
        return lessonRepository.findByOwnerId(currentUser.getId(), pageable)
                .map(lessonMapper::toResponse);
    }

    public Page<LessonResponse> getTeacherLessons(Long teacherId, Pageable pageable) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Учитель не найден");
        }
        return lessonRepository.findByTeacherId(teacherId, pageable)
                .map(lessonMapper::toResponse);
    }

    public LessonResponse getLessonById(Long id) {
        LessonEntity lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));
        UserEntity currentUser = getCurrentUser();
        boolean hasAccess = false;
        if (lesson.getOwner() != null && lesson.getOwner().getId().equals(currentUser.getId())) {
            hasAccess = true;
        }
        else if (lesson.getTeacherEntity() != null &&
                lesson.getTeacherEntity().getUserEntity().getId().equals(currentUser.getId())) {
            hasAccess = true;
        }
        else if (currentUser.getRoles().contains(Role.ADMIN)) {
            hasAccess = true;
        }
        else if (currentUser.getRoles().contains(Role.STUDENT)) {
            hasAccess = true;
        }
        if (!hasAccess) {
            throw new ValidationException("У вас нет доступа к этому уроку");
        }
        return lessonMapper.toResponse(lesson);
    }

    @Transactional
    public LessonResponse updateLesson(Long id, LessonCreateRequest request) {
        LessonEntity lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));

        UserEntity currentUser = getCurrentUser();
        boolean canModify = false;
        if (lesson.getOwner() != null && lesson.getOwner().getId().equals(currentUser.getId())) {
            canModify = true;
        } else if (lesson.getTeacherEntity() != null &&
                lesson.getTeacherEntity().getUserEntity().getId().equals(currentUser.getId())) {
            canModify = true;
        } else if (currentUser.getRoles().contains(Role.ADMIN)) {
            canModify = true;
        }
        if (!canModify) {
            throw new ValidationException("У вас нет прав на изменение этого урока");
        }
        if (request.getTitle() != null) lesson.setTitle(request.getTitle());
        if (request.getDescription() != null) lesson.setDescription(request.getDescription());
        LessonEntity saved = lessonRepository.save(lesson);
        log.info("Обновлен урок {} пользователем {}", saved.getId(), currentUser.getEmail());
        return lessonMapper.toResponse(saved);
    }

    @Transactional
    public void deleteLesson(Long id) {
        LessonEntity lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));

        UserEntity currentUser = getCurrentUser();
        boolean canDelete = false;
        if (lesson.getOwner() != null && lesson.getOwner().getId().equals(currentUser.getId())) {
            canDelete = true;
        } else if (lesson.getTeacherEntity() != null &&
                lesson.getTeacherEntity().getUserEntity().getId().equals(currentUser.getId())) {
            canDelete = true;
        } else if (currentUser.getRoles().contains(Role.ADMIN)) {
            canDelete = true;
        }
        if (!canDelete) {
            throw new ValidationException("У вас нет прав на удаление этого урока");
        }
        if (lesson.getFlashcardEntities() != null && !lesson.getFlashcardEntities().isEmpty()) {
            lesson.getFlashcardEntities().clear();
        }
        if (lesson.getStudentProgress() != null && !lesson.getStudentProgress().isEmpty()) {
            lesson.getStudentProgress().clear();
        }
        lessonRepository.delete(lesson);
        log.info("Удален урок {} пользователем {}", id, currentUser.getEmail());
    }
}