package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ValidationException;
import com.example.English_Learning_Platform.model.dto.mapper.UserMapper;
import com.example.English_Learning_Platform.model.dto.request.LoginRequest;
import com.example.English_Learning_Platform.model.dto.request.RegisterRequest;
import com.example.English_Learning_Platform.model.dto.response.JwtResponse;
import com.example.English_Learning_Platform.model.dto.response.UserResponse;
import com.example.English_Learning_Platform.model.entity.UserEntity;
import com.example.English_Learning_Platform.model.enums.Role;
import com.example.English_Learning_Platform.repository.UserRepository;
import com.example.English_Learning_Platform.security.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserEntity userEntity;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@test.com");
        registerRequest.setPassword("Password1@");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("Password1@");

        userEntity = UserEntity.builder()
                .id(1L)
                .email("test@test.com")
                .password("encodedPassword")
                .roles(new HashSet<>(Set.of(Role.USER)))
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .email("test@test.com")
                .roles(Set.of(Role.USER))
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@test.com");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyUsed() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> authService.register(registerRequest));

        assertEquals("Email уже используется", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldGetCurrentUser() {
        mockSecurityContext();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(userEntity));
        when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

        UserResponse result = authService.getCurrentUser();

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
        verify(userMapper).toResponse(userEntity);
    }

    @Test
    void shouldThrowExceptionWhenCurrentUserNotFound() {
        mockSecurityContext();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> authService.getCurrentUser());
    }
}