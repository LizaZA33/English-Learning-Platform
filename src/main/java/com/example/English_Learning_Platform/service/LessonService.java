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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonService {

    private final LessonRepository lessonRepository;
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final LessonMapper lessonMapper;

    @Transactional
    public LessonResponse createLesson(LessonCreateRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        LessonEntity lessonEntity = LessonEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        if (request.getTeacherId() != null) {
            TeacherEntity teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден"));
            if (!teacher.getUserEntity().getId().equals(userEntity.getId()) &&
                    !userEntity.getRoles().contains(Role.ADMIN)) {
                throw new ValidationException("Вы не можете создавать уроки от имени другого учителя");
            }
            lessonEntity.setTeacherEntity(teacher);
        } else {
            lessonEntity.setOwner(userEntity);
        }

        LessonEntity saved = lessonRepository.save(lessonEntity);
        log.info("Создан урок '{}'", saved.getTitle());
        return lessonMapper.toResponse(saved);
    }

    @Transactional
    public Page<LessonResponse> getPersonalLessons(Pageable pageable) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        Page<LessonEntity> page = lessonRepository.findByOwnerId(userEntity.getId(), pageable);
        return page.map(lessonMapper::toResponse);
    }

    @Transactional
    public Page<LessonResponse> getTeacherLessons(Long teacherId, Pageable pageable) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Учитель не найден");
        }
        Page<LessonEntity> page = lessonRepository.findByTeacherId(teacherId, pageable);
        return page.map(lessonMapper::toResponse);
    }

    @Transactional
    public LessonResponse getLessonById(Long id) {
        LessonEntity lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));
        return lessonMapper.toResponse(lesson);
    }

    @Transactional
    public LessonResponse updateLesson(Long id, LessonCreateRequest request) {
        LessonEntity lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));

        if (request.getTitle() != null) lesson.setTitle(request.getTitle());
        if (request.getDescription() != null) lesson.setDescription(request.getDescription());

        LessonEntity saved = lessonRepository.save(lesson);
        log.info("Обновлён урок '{}'", saved.getTitle());
        return lessonMapper.toResponse(saved);
    }

    @Transactional
    public void deleteLesson(Long id) {
        LessonEntity lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));
        lessonRepository.delete(lesson);
        log.info("Удалён урок '{}'", lesson.getTitle());
    }
}
