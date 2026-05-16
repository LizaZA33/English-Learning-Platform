package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.exception.ValidationException;
import com.example.English_Learning_Platform.model.dto.mapper.TeacherMapper;
import com.example.English_Learning_Platform.model.dto.request.TeacherProfileRequest;
import com.example.English_Learning_Platform.model.dto.response.CourseGroupResponse;
import com.example.English_Learning_Platform.model.dto.response.TeacherResponse;
import com.example.English_Learning_Platform.model.entity.TeacherEntity;
import com.example.English_Learning_Platform.model.entity.UserEntity;
import com.example.English_Learning_Platform.model.enums.Role;
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
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeacherMapper teacherMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TeacherService teacherService;

    private UserEntity userEntity;
    private TeacherEntity teacherEntity;
    private TeacherResponse teacherResponse;
    private TeacherProfileRequest profileRequest;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .id(1L)
                .email("teacher@test.com")
                .password("password")
                .roles(new HashSet<>(Set.of(Role.USER)))
                .build();

        teacherEntity = TeacherEntity.builder()
                .id(1L)
                .userEntity(userEntity)
                .firstName("John")
                .lastName("Doe")
                .build();

        teacherResponse = TeacherResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("teacher@test.com")
                .groups(List.of())
                .build();

        profileRequest = new TeacherProfileRequest();
        profileRequest.setFirstName("John");
        profileRequest.setLastName("Doe");
        profileRequest.setPhoneNumber("+79001234567");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("teacher@test.com");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldCreateTeacherProfile() {
        mockSecurityContext();
        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(userEntity));
        when(teacherRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(teacherRepository.save(any(TeacherEntity.class))).thenReturn(teacherEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(teacherMapper.toResponse(teacherEntity)).thenReturn(teacherResponse);

        TeacherResponse result = teacherService.createProfile(profileRequest);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(teacherRepository).save(any(TeacherEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenTeacherProfileAlreadyExists() {
        mockSecurityContext();
        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(userEntity));
        when(teacherRepository.findByUserId(1L)).thenReturn(Optional.of(teacherEntity));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> teacherService.createProfile(profileRequest));

        assertEquals("Профиль учителя уже существует", exception.getMessage());
    }

    @Test
    void shouldReturnAllTeachers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TeacherEntity> page = new PageImpl<>(List.of(teacherEntity));
        when(teacherRepository.findAll(pageable)).thenReturn(page);
        when(teacherMapper.toResponse(teacherEntity)).thenReturn(teacherResponse);

        Page<TeacherResponse> result = teacherService.findAllTeachers(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldSearchTeachers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TeacherEntity> page = new PageImpl<>(List.of(teacherEntity));
        when(teacherRepository.searchTeachers("John", pageable)).thenReturn(page);
        when(teacherMapper.toResponse(teacherEntity)).thenReturn(teacherResponse);

        Page<TeacherResponse> result = teacherService.findAllTeachers("John", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldReturnExistingTeacherById() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacherEntity));
        when(teacherMapper.toResponse(teacherEntity)).thenReturn(teacherResponse);

        TeacherResponse result = teacherService.getTeacherById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldUpdateTeacherProfile() {
        mockSecurityContext();
        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(userEntity));
        when(teacherRepository.findByUserId(1L)).thenReturn(Optional.of(teacherEntity));
        when(teacherRepository.save(teacherEntity)).thenReturn(teacherEntity);
        when(teacherMapper.toResponse(teacherEntity)).thenReturn(teacherResponse);

        TeacherProfileRequest updateRequest = new TeacherProfileRequest();
        updateRequest.setFirstName("Updated");

        TeacherResponse result = teacherService.updateProfile(updateRequest);

        assertNotNull(result);
        verify(teacherRepository).save(teacherEntity);
    }
}