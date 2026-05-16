package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.model.dto.response.LectureProgressResponse;
import com.example.English_Learning_Platform.model.dto.response.LessonProgressResponse;
import com.example.English_Learning_Platform.model.entity.*;
import com.example.English_Learning_Platform.model.enums.Role;
import com.example.English_Learning_Platform.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private LectureProgressStudentRepository lectureProgressRepository;

    @Mock
    private LessonProgressStudentRepository lessonProgressRepository;

    @InjectMocks
    private ProgressService progressService;

    @Test
    void shouldReturnLectureProgressForStudent() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .email("student@test.com")
                .roles(new HashSet<>(Set.of(Role.STUDENT)))
                .build();

        StudentEntity studentEntity = StudentEntity.builder()
                .id(1L)
                .userEntity(userEntity)
                .firstName("John")
                .lastName("Doe")
                .build();

        LectureEntity lectureEntity = new LectureEntity();
        lectureEntity.setId(1L);
        lectureEntity.setTitle("Lecture 1");

        LectureProgressStudentEntity progress = new LectureProgressStudentEntity();
        progress.setId(1L);
        progress.setLectureEntity(lectureEntity);
        progress.setStudentEntity(studentEntity);
        progress.setProgressPercent(50);

        studentEntity.setLectureProgress(new HashSet<>(Set.of(progress)));

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(userEntity));
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(studentEntity));

        Pageable pageable = PageRequest.of(0, 10);
        Page<LectureProgressResponse> result = progressService.getLectureProgress("student@test.com", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(50, result.getContent().get(0).getLectureProgress());
    }

    @Test
    void shouldReturnLessonProgressForStudent() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .email("student@test.com")
                .roles(new HashSet<>(Set.of(Role.STUDENT)))
                .build();

        StudentEntity studentEntity = StudentEntity.builder()
                .id(1L)
                .userEntity(userEntity)
                .firstName("John")
                .lastName("Doe")
                .build();

        LessonEntity lessonEntity = new LessonEntity();
        lessonEntity.setId(1L);
        lessonEntity.setTitle("Lesson 1");

        LessonProgressStudentEntity progress = new LessonProgressStudentEntity();
        progress.setId(1L);
        progress.setLessonEntity(lessonEntity);
        progress.setStudentEntity(studentEntity);
        progress.setProgressPercent(75);

        studentEntity.setLessonProgress(new HashSet<>(Set.of(progress)));

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(userEntity));
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(studentEntity));

        Pageable pageable = PageRequest.of(0, 10);
        Page<LessonProgressResponse> result = progressService.getLessonProgress("student@test.com", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(75, result.getContent().get(0).getLessonProgress());
    }

    @Test
    void shouldUpdateLectureProgressForExistingRecord() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L).email("student@test.com").build();

        StudentEntity studentEntity = StudentEntity.builder()
                .id(1L).userEntity(userEntity).firstName("John").lastName("Doe").build();

        LectureEntity lectureEntity = new LectureEntity();
        lectureEntity.setId(1L);

        LectureProgressStudentEntity progress = new LectureProgressStudentEntity();
        progress.setId(1L);
        progress.setLectureEntity(lectureEntity);
        progress.setStudentEntity(studentEntity);
        progress.setProgressPercent(30);

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(userEntity));
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(studentEntity));
        when(lectureProgressRepository.findByLectureIdAndStudentId(1L, 1L))
                .thenReturn(Optional.of(progress));
        when(lectureProgressRepository.save(progress)).thenReturn(progress);

        progressService.updateLectureProgress("student@test.com", 1L, 80);

        assertEquals(80, progress.getProgressPercent());
        verify(lectureProgressRepository).save(progress);
    }

    @Test
    void shouldUpdateLessonProgressForExistingRecord() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L).email("student@test.com").build();

        StudentEntity studentEntity = StudentEntity.builder()
                .id(1L).userEntity(userEntity).firstName("John").lastName("Doe").build();

        LessonEntity lessonEntity = new LessonEntity();
        lessonEntity.setId(1L);

        LessonProgressStudentEntity progress = new LessonProgressStudentEntity();
        progress.setId(1L);
        progress.setLessonEntity(lessonEntity);
        progress.setStudentEntity(studentEntity);
        progress.setProgressPercent(20);

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(userEntity));
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(studentEntity));
        when(lessonProgressRepository.findByLessonIdAndStudentId(1L, 1L))
                .thenReturn(Optional.of(progress));
        when(lessonProgressRepository.save(progress)).thenReturn(progress);

        progressService.updateLessonProgress("student@test.com", 1L, 90);

        assertEquals(90, progress.getProgressPercent());
        verify(lessonProgressRepository).save(progress);
    }

    @Test
    void shouldThrowExceptionWhenStudentNotFoundForProgress() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L).email("student@test.com").build();

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(userEntity));
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.empty());

        Pageable pageable = PageRequest.of(0, 10);
        assertThrows(ResourceNotFoundException.class,
                () -> progressService.getLectureProgress("student@test.com", pageable));
    }
}