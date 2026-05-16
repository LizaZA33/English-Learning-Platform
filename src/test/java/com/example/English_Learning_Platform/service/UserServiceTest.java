package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.exception.ValidationException;
import com.example.English_Learning_Platform.model.dto.mapper.UserMapper;
import com.example.English_Learning_Platform.model.dto.response.UserResponse;
import com.example.English_Learning_Platform.model.entity.UserEntity;
import com.example.English_Learning_Platform.model.enums.Role;
import com.example.English_Learning_Platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;
    private UserEntity userEntity;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .id(1L)
                .email("user@example.com")
                .roles(new HashSet<>(Set.of(Role.USER)))
                .build();
        userResponse = UserResponse.builder()
                .id(1L)
                .email("user@example.com")
                .roles(Set.of(Role.USER))
                .build();
    }

    @Test
    void shouldReturnAllUsers_whenSearchIsNull() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> page = new PageImpl<>(List.of(userEntity));
        when(userRepository.findAll(pageable)).thenReturn(page);
        when(userMapper.toResponse(userEntity)).thenReturn(userResponse);
        Page<UserResponse> result = userService.findAllUsers(null, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("user@example.com", result.getContent().get(0).getEmail());
        verify(userRepository).findAll(pageable);
        verify(userRepository, never()).searchUsers(anyString(), any());
    }

    @Test
    void shouldReturnSearchResults_whenSearchingByEmail() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> page = new PageImpl<>(List.of(userEntity));
        when(userRepository.searchUsers("user", pageable)).thenReturn(page);
        when(userMapper.toResponse(userEntity)).thenReturn(userResponse);
        Page<UserResponse> result = userService.findAllUsers("user", pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).searchUsers("user", pageable);
        verify(userRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void shouldReturnEmptyPage_whenNoUsersFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> page = new PageImpl<>(List.of());
        when(userRepository.searchUsers("nonexistent", pageable)).thenReturn(page);
        Page<UserResponse> result = userService.findAllUsers("nonexistent", pageable);
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void shouldAddRole_whenAddIsTrue() {
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(userResponse);
        UserResponse result = userService.updateUserRole(1L, Role.STUDENT, true);
        assertNotNull(result);
        verify(userRepository).save(captor.capture());
        UserEntity captured = captor.getValue();
        assertTrue(captured.getRoles().contains(Role.STUDENT));
    }

    @Test
    void shouldRemoveRole_whenAddIsFalse() {
        userEntity.getRoles().add(Role.STUDENT);
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(userResponse);
        UserResponse result = userService.updateUserRole(1L, Role.STUDENT, false);
        assertNotNull(result);
        verify(userRepository).save(captor.capture());
        UserEntity captured = captor.getValue();
        assertFalse(captured.getRoles().contains(Role.STUDENT));
    }

    @Test
    void shouldThrowException_whenUserNotFoundForRoleUpdate() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUserRole(999L, Role.STUDENT, true));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldDeleteUser_whenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowException_whenUserNotFoundForDeletion() {
        when(userRepository.existsById(999L)).thenReturn(false);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteUser(999L));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void shouldCallFindAll_whenSearchIsBlank() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> page = new PageImpl<>(List.of(userEntity));
        when(userRepository.findAll(pageable)).thenReturn(page);
        when(userMapper.toResponse(userEntity)).thenReturn(userResponse);
        Page<UserResponse> result = userService.findAllUsers("   ", pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAll(pageable);
        verify(userRepository, never()).searchUsers(anyString(), any());
    }
}