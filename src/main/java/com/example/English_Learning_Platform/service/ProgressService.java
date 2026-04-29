package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.model.dto.response.LectureProgressResponse;
import com.example.English_Learning_Platform.model.dto.response.LessonProgressResponse;
import com.example.English_Learning_Platform.model.entity.*;
import com.example.English_Learning_Platform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final LectureProgressStudentRepository lectureProgressRepository;
    private final LessonProgressStudentRepository lessonProgressRepository;

    @Transactional(readOnly = true)
    public Page<LectureProgressResponse> getLectureProgress(String email, Pageable pageable) {
        StudentEntity student = getStudentByEmail(email);
        var progressSet = student.getLectureProgress();
        var list = progressSet.stream()
                .map(p -> LectureProgressResponse.builder()
                        .lectureId(p.getLectureEntity().getId())
                        .lectureTitle(p.getLectureEntity().getTitle())
                        .lectureProgress(p.getProgressPercent())
                        .build())
                .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, list.size());
    }

    @Transactional(readOnly = true)
    public Page<LessonProgressResponse> getLessonProgress(String email, Pageable pageable) {
        StudentEntity student = getStudentByEmail(email);
        var progressSet = student.getLessonProgress();
        var list = progressSet.stream()
                .map(p -> LessonProgressResponse.builder()
                        .lessonId(p.getLessonEntity().getId())
                        .lessonTitle(p.getLessonEntity().getTitle())
                        .lessonProgress(p.getProgressPercent())
                        .build())
                .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, list.size());
    }

    @Transactional
    public void updateLectureProgress(String email, Long lectureId, Integer progressPercent) {
        StudentEntity student = getStudentByEmail(email);
        LectureProgressStudentEntity progress = lectureProgressRepository
                .findByLectureIdAndStudentId(lectureId, student.getId())
                .orElseGet(() -> {
                    LectureProgressStudentEntity newProgress = new LectureProgressStudentEntity();
                    LectureEntity lecture = new LectureEntity();
                    lecture.setId(lectureId);
                    newProgress.setLectureEntity(lecture);
                    newProgress.setStudentEntity(student);
                    return newProgress;
                });
        progress.setProgressPercent(progressPercent);
        lectureProgressRepository.save(progress);
        log.info("Прогресс лекции {} для студента {} обновлён до {}%", lectureId, student.getId(), progressPercent);
    }

    @Transactional
    public void updateLessonProgress(String email, Long lessonId, Integer progressPercent) {
        StudentEntity student = getStudentByEmail(email);
        LessonProgressStudentEntity progress = lessonProgressRepository
                .findByLessonIdAndStudentId(lessonId, student.getId())
                .orElseGet(() -> {
                    LessonProgressStudentEntity newProgress = new LessonProgressStudentEntity();
                    LessonEntity lesson = new LessonEntity();
                    lesson.setId(lessonId);
                    newProgress.setLessonEntity(lesson);
                    newProgress.setStudentEntity(student);
                    return newProgress;
                });
        progress.setProgressPercent(progressPercent);
        lessonProgressRepository.save(progress);
        log.info("Прогресс урока {} для студента {} обновлён до {}%", lessonId, student.getId(), progressPercent);
    }

    private StudentEntity getStudentByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        return studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Студент не найден"));
    }
}