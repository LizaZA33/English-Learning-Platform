package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.exception.ValidationException;
import com.example.English_Learning_Platform.model.dto.mapper.LessonMapper;
import com.example.English_Learning_Platform.model.dto.request.LessonCreateRequest;
import com.example.English_Learning_Platform.model.dto.response.LessonResponse;
import com.example.English_Learning_Platform.model.entity.*;
import com.example.English_Learning_Platform.model.enums.Role;
import com.example.English_Learning_Platform.repository.LessonRepository;
import com.example.English_Learning_Platform.repository.TeacherRepository;
import com.example.English_Learning_Platform.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LessonMapper lessonMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LessonService lessonService;

    private UserEntity userEntity;
    private TeacherEntity teacherEntity;
    private LessonEntity lessonEntity;
    private LessonResponse lessonResponse;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .id(1L)
                .email("user@test.com")
                .password("password")
                .roles(new HashSet<>(Set.of(Role.USER)))
                .build();

        teacherEntity = TeacherEntity.builder()
                .id(1L)
                .userEntity(userEntity)
                .firstName("John")
                .lastName("Doe")
                .build();

        userEntity.setTeacherEntity(teacherEntity);

        lessonEntity = LessonEntity.builder()
                .id(1L)
                .title("Test Lesson")
                .description("Description")
                .owner(userEntity)
                .build();

        lessonResponse = LessonResponse.builder()
                .id(1L)
                .title("Test Lesson")
                .description("Description")
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@test.com");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldCreatePersonalLessonWhenNoTeacherId() {
        mockSecurityContext();
        LessonCreateRequest request = new LessonCreateRequest();
        request.setTitle("Personal Lesson");
        request.setDescription("My lesson");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(userEntity));
        when(lessonRepository.save(any(LessonEntity.class))).thenReturn(lessonEntity);
        when(lessonMapper.toResponse(lessonEntity)).thenReturn(lessonResponse);

        LessonResponse result = lessonService.createLesson(request);

        assertNotNull(result);
        assertEquals("Test Lesson", result.getTitle());
        verify(lessonRepository).save(any(LessonEntity.class));
    }

    @Test
    void shouldCreateTeacherLessonWhenValidTeacherId() {
        mockSecurityContext();
        LessonCreateRequest request = new LessonCreateRequest();
        request.setTitle("Teacher Lesson");
        request.setDescription("Description");
        request.setTeacherId(1L);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(userEntity));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacherEntity));
        when(lessonRepository.save(any(LessonEntity.class))).thenReturn(lessonEntity);
        when(lessonMapper.toResponse(lessonEntity)).thenReturn(lessonResponse);

        LessonResponse result = lessonService.createLesson(request);

        assertNotNull(result);
        verify(lessonRepository).save(any(LessonEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingLessonForOtherTeacher() {
        mockSecurityContext();
        UserEntity otherUser = UserEntity.builder().id(2L).email("other@test.com").build();
        TeacherEntity otherTeacher = TeacherEntity.builder()
                .id(2L)
                .userEntity(otherUser)
                .build();

        LessonCreateRequest request = new LessonCreateRequest();
        request.setTitle("Lesson");
        request.setTeacherId(2L);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(userEntity));
        when(teacherRepository.findById(2L)).thenReturn(Optional.of(otherTeacher));

        assertThrows(ValidationException.class,
                () -> lessonService.createLesson(request));
        verify(lessonRepository, never()).save(any());
    }

    @Test
    void shouldAllowAdminToCreateLessonForAnyTeacher() {
        mockSecurityContext();
        userEntity.getRoles().add(Role.ADMIN);
        UserEntity otherUser = UserEntity.builder().id(2L).email("other@test.com").build();
        TeacherEntity otherTeacher = TeacherEntity.builder()
                .id(2L)
                .userEntity(otherUser)
                .build();

        LessonCreateRequest request = new LessonCreateRequest();
        request.setTitle("Admin Lesson");
        request.setTeacherId(2L);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(userEntity));
        when(teacherRepository.findById(2L)).thenReturn(Optional.of(otherTeacher));
        when(lessonRepository.save(any(LessonEntity.class))).thenReturn(lessonEntity);
        when(lessonMapper.toResponse(lessonEntity)).thenReturn(lessonResponse);

        LessonResponse result = lessonService.createLesson(request);

        assertNotNull(result);
        verify(lessonRepository).save(any(LessonEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnCreateLesson() {
        mockSecurityContext();
        LessonCreateRequest request = new LessonCreateRequest();
        request.setTitle("Lesson");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> lessonService.createLesson(request));
    }

    @Test
    void shouldThrowExceptionWhenTeacherNotFoundOnCreateLesson() {
        mockSecurityContext();
        LessonCreateRequest request = new LessonCreateRequest();
        request.setTitle("Lesson");
        request.setTeacherId(999L);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(userEntity));
        when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> lessonService.createLesson(request));
    }

    @Test
    void shouldReturnPersonalLessons() {
        mockSecurityContext();
        Pageable pageable = PageRequest.of(0, 10);
        Page<LessonEntity> page = new PageImpl<>(List.of(lessonEntity));

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(userEntity));
        when(lessonRepository.findByOwnerId(1L, pageable)).thenReturn(page);
        when(lessonMapper.toResponse(lessonEntity)).thenReturn(lessonResponse);

        Page<LessonResponse> result = lessonService.getPersonalLessons(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForPersonalLessons() {
        mockSecurityContext();
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> lessonService.getPersonalLessons(pageable));
    }

    @Test
    void shouldReturnTeacherLessons() {
        Long teacherId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<LessonEntity> page = new PageImpl<>(List.of(lessonEntity));

        when(teacherRepository.existsById(teacherId)).thenReturn(true);
        when(lessonRepository.findByTeacherId(teacherId, pageable)).thenReturn(page);
        when(lessonMapper.toResponse(lessonEntity)).thenReturn(lessonResponse);

        Page<LessonResponse> result = lessonService.getTeacherLessons(teacherId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldThrowExceptionWhenTeacherNotFoundForTeacherLessons() {
        Long teacherId = 999L;
        Pageable pageable = PageRequest.of(0, 10);

        when(teacherRepository.existsById(teacherId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> lessonService.getTeacherLessons(teacherId, pageable));
    }

    @Test
    void shouldReturnLessonById() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lessonEntity));
        when(lessonMapper.toResponse(lessonEntity)).thenReturn(lessonResponse);

        LessonResponse result = lessonService.getLessonById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowExceptionWhenLessonNotFoundById() {
        when(lessonRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> lessonService.getLessonById(999L));
    }

    @Test
    void shouldUpdateLessonWhenExists() {
        LessonCreateRequest request = new LessonCreateRequest();
        request.setTitle("Updated Lesson");
        request.setDescription("Updated Description");

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lessonEntity));
        when(lessonRepository.save(lessonEntity)).thenReturn(lessonEntity);
        when(lessonMapper.toResponse(lessonEntity)).thenReturn(lessonResponse);

        LessonResponse result = lessonService.updateLesson(1L, request);

        assertNotNull(result);
        verify(lessonRepository).save(lessonEntity);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentLesson() {
        when(lessonRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> lessonService.updateLesson(999L, new LessonCreateRequest()));
    }

    @Test
    void shouldDeleteLessonWhenExists() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lessonEntity));

        lessonService.deleteLesson(1L);

        verify(lessonRepository).delete(lessonEntity);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentLesson() {
        when(lessonRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> lessonService.deleteLesson(999L));
        verify(lessonRepository, never()).delete(any());
    }

    @Test
    void shouldVerifyRepositoryInteractionsOnCreateLesson() {
        mockSecurityContext();
        LessonCreateRequest request = new LessonCreateRequest();
        request.setTitle("Verify Lesson");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(userEntity));
        when(lessonRepository.save(any(LessonEntity.class))).thenReturn(lessonEntity);
        when(lessonMapper.toResponse(lessonEntity)).thenReturn(lessonResponse);

        lessonService.createLesson(request);

        verify(userRepository).findByEmail("user@test.com");
        verify(lessonRepository).save(any(LessonEntity.class));
        verify(lessonMapper).toResponse(lessonEntity);
    }

    @Test
    void shouldNotCallSaveWhenValidationFailsOnCreateLesson() {
        mockSecurityContext();
        LessonCreateRequest request = new LessonCreateRequest();
        request.setTitle("Fail Lesson");
        request.setTeacherId(999L);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(userEntity));
        when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> lessonService.createLesson(request));
        verify(lessonRepository, never()).save(any());
    }
}