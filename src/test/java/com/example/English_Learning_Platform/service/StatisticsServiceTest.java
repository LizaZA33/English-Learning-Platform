package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CourseGroupRepository groupRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    void shouldReturnStudentCount() {
        when(studentRepository.count()).thenReturn(42L);
        long result = statisticsService.getStudentCount();
        assertEquals(42L, result);
        verify(studentRepository).count();
    }

    @Test
    void shouldReturnTeacherCount() {
        when(teacherRepository.count()).thenReturn(10L);
        long result = statisticsService.getTeacherCount();
        assertEquals(10L, result);
        verify(teacherRepository).count();
    }

    @Test
    void shouldReturnLessonCount() {
        when(lessonRepository.count()).thenReturn(150L);
        long result = statisticsService.getLessonCount();
        assertEquals(150L, result);
        verify(lessonRepository).count();
    }

    @Test
    void shouldReturnGroupCount() {
        when(groupRepository.count()).thenReturn(25L);
        long result = statisticsService.getGroupCount();
        assertEquals(25L, result);
        verify(groupRepository).count();
    }
}