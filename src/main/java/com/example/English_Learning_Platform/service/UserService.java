package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.exception.ValidationException;
import com.example.English_Learning_Platform.model.dto.mapper.UserMapper;
import com.example.English_Learning_Platform.model.dto.request.StudentProfileRequest;
import com.example.English_Learning_Platform.model.dto.request.TeacherProfileRequest;
import com.example.English_Learning_Platform.model.dto.request.UserUpdateRequest;
import com.example.English_Learning_Platform.model.dto.response.UserResponse;
import com.example.English_Learning_Platform.model.entity.StudentEntity;
import com.example.English_Learning_Platform.model.entity.TeacherEntity;
import com.example.English_Learning_Platform.model.entity.UserEntity;
import com.example.English_Learning_Platform.model.enums.Role;
import com.example.English_Learning_Platform.repository.StudentRepository;
import com.example.English_Learning_Platform.repository.TeacherRepository;
import com.example.English_Learning_Platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final UserMapper userMapper;
    public Page<UserResponse> findAllUsers(String search, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            return userRepository.searchUsers(search, pageable).map(userMapper::toResponse);
        }
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }
    public UserResponse getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUserRole(Long userId, Role role, boolean add) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        if (add) {
            user.getRoles().add(role);
            log.info("Добавлена роль {} пользователю {}", role, user.getEmail());
        } else {
            user.getRoles().remove(role);
            log.info("Удалена роль {} у пользователя {}", role, user.getEmail());
        }
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        userRepository.delete(user);
        log.info("Удален пользователь с ID: {}", userId);
    }
    @Transactional
    public UserResponse createStudentProfile(String email, StudentProfileRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        if (studentRepository.findByUserId(user.getId()).isPresent()) {
            throw new ValidationException("Профиль студента уже существует");
        }
        StudentEntity student = StudentEntity.builder()
                .userEntity(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .patronymic(request.getPatronymic())
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .studentGroupEntities(new HashSet<>())
                .lectureProgress(new HashSet<>())
                .lessonProgress(new HashSet<>())
                .build();

        studentRepository.save(student);
        user.getRoles().add(Role.STUDENT);
        userRepository.save(user);
        log.info("Создан профиль студента для пользователя: {}", email);
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse createTeacherProfile(String email, TeacherProfileRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        if (teacherRepository.findByUserId(user.getId()).isPresent()) {
            throw new ValidationException("Профиль учителя уже существует");
        }

        TeacherEntity teacher = TeacherEntity.builder()
                .userEntity(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .patronymic(request.getPatronymic())
                .phoneNumber(request.getPhoneNumber())
                .groups(new HashSet<>())
                .lessonEntities(new HashSet<>())
                .build();
        teacherRepository.save(teacher);
        user.getRoles().add(Role.TEACHER);
        userRepository.save(user);
        log.info("Создан профиль учителя для пользователя: {}", email);
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(String email, UserUpdateRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (!request.getEmail().equals(user.getEmail()) &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new ValidationException("Email уже используется");
            }
            user.setEmail(request.getEmail());
        }
        if (user.getTeacherEntity() != null) {
            TeacherEntity teacher = user.getTeacherEntity();
            if (request.getFirstName() != null) teacher.setFirstName(request.getFirstName());
            if (request.getLastName() != null) teacher.setLastName(request.getLastName());
            if (request.getPatronymic() != null) teacher.setPatronymic(request.getPatronymic());
            if (request.getPhoneNumber() != null) teacher.setPhoneNumber(request.getPhoneNumber());
            teacherRepository.save(teacher);
        }

        if (user.getStudentEntity() != null) {
            StudentEntity student = user.getStudentEntity();
            if (request.getFirstName() != null) student.setFirstName(request.getFirstName());
            if (request.getLastName() != null) student.setLastName(request.getLastName());
            if (request.getPatronymic() != null) student.setPatronymic(request.getPatronymic());
            if (request.getPhoneNumber() != null) student.setPhoneNumber(request.getPhoneNumber());
            if (request.getDateOfBirth() != null) student.setDateOfBirth(request.getDateOfBirth());
            studentRepository.save(student);
        }
        userRepository.save(user);
        log.info("Обновлен профиль пользователя: {}", email);
        return userMapper.toResponse(user);
    }
}