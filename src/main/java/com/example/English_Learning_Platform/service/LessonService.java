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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        LessonEntity lesson = new LessonEntity();
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());

        if (request.getTeacherId() != null) {
            TeacherEntity teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден"));
            lesson.setTeacherEntity(teacher);
        } else {
            lesson.setOwner(currentUser);
        }

        lesson = lessonRepository.save(lesson);
        return lessonMapper.toResponse(lesson);
    }

    public Page<LessonResponse> getPersonalLessons(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        return lessonRepository.findByOwnerId(currentUser.getId(), pageable).map(lessonMapper::toResponse);
    }

    public Page<LessonResponse> getTeacherLessons(Long teacherId, Pageable pageable) {
        return lessonRepository.findByTeacherId(teacherId, pageable).map(lessonMapper::toResponse);
    }

    public LessonResponse getLessonById(Long id) {
        LessonEntity lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));
        return lessonMapper.toResponse(lesson);
    }

    @Transactional
    public LessonResponse updateLesson(Long id, LessonCreateRequest request) {
        LessonEntity lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson = lessonRepository.save(lesson);
        return lessonMapper.toResponse(lesson);
    }

    @Transactional
    public void deleteLesson(Long id) {
        LessonEntity lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));
        lessonRepository.delete(lesson);
    }
}