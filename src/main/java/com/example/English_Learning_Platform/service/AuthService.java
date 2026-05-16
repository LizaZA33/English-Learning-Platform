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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email уже используется");
        }

        UserEntity userEntity = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(new HashSet<>(Set.of(Role.USER)))
                .build();

        UserEntity saved = userRepository.save(userEntity);
        log.info("Зарегистрирован новый пользователь: {}", saved.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        Set<String> rolesSet = saved.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        String fullName = request.getLastName() + " " + request.getFirstName()
                + (request.getPatronymic() != null && !request.getPatronymic().isBlank()
                ? " " + request.getPatronymic() : "");

        return JwtResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(saved.getId())
                .email(saved.getEmail())
                .fullName(fullName.trim())
                .roles(rolesSet.toString())
                .build();
    }

    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден после аутентификации"));

        Set<String> rolesSet = userEntity.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        String fullName = "";
        if (userEntity.getTeacherEntity() != null) {
            fullName = userEntity.getTeacherEntity().getLastName() + " "
                    + userEntity.getTeacherEntity().getFirstName() + " "
                    + (userEntity.getTeacherEntity().getPatronymic() != null
                    ? userEntity.getTeacherEntity().getPatronymic() : "");
        } else if (userEntity.getStudentEntity() != null) {
            fullName = userEntity.getStudentEntity().getLastName() + " "
                    + userEntity.getStudentEntity().getFirstName() + " "
                    + (userEntity.getStudentEntity().getPatronymic() != null
                    ? userEntity.getStudentEntity().getPatronymic() : "");
        }
        return JwtResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .fullName(fullName.trim())
                .roles(rolesSet.toString())
                .build();
    }

    public UserResponse getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return userMapper.toResponse(userEntity);
    }
}