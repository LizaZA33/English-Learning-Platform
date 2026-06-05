package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.model.dto.mapper.UserMapper;
import com.example.English_Learning_Platform.model.dto.request.UserUpdateRequest;
import com.example.English_Learning_Platform.model.dto.response.UserResponse;
import com.example.English_Learning_Platform.model.entity.StudentEntity;
import com.example.English_Learning_Platform.model.entity.TeacherEntity;
import com.example.English_Learning_Platform.model.entity.UserEntity;
import com.example.English_Learning_Platform.model.enums.Role;
import com.example.English_Learning_Platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Page<UserResponse> findAllUsers(String search, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            return userRepository.searchUsers(search, pageable).map(userMapper::toResponse);
        }
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Transactional
    public UserResponse updateUserRole(Long userId, Role role, boolean add) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        if (add) {
            user.getRoles().add(role);
        } else {
            user.getRoles().remove(role);
        }

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        userRepository.delete(user);
    }

    @Transactional
    public UserResponse updateProfile(UserUpdateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        if (user.getTeacherEntity() != null) {
            TeacherEntity teacher = user.getTeacherEntity();
            if (request.getFirstName() != null) teacher.setFirstName(request.getFirstName());
            if (request.getLastName() != null) teacher.setLastName(request.getLastName());
            if (request.getPatronymic() != null) teacher.setPatronymic(request.getPatronymic());
            if (request.getPhoneNumber() != null) teacher.setPhoneNumber(request.getPhoneNumber());
        }

        if (user.getStudentEntity() != null) {
            StudentEntity student = user.getStudentEntity();
            if (request.getFirstName() != null) student.setFirstName(request.getFirstName());
            if (request.getLastName() != null) student.setLastName(request.getLastName());
            if (request.getPatronymic() != null) student.setPatronymic(request.getPatronymic());
            if (request.getPhoneNumber() != null) student.setPhoneNumber(request.getPhoneNumber());
            if (request.getDateOfBirth() != null) student.setDateOfBirth(request.getDateOfBirth());
        }

        userRepository.save(user);
        return userMapper.toResponse(user);
    }
}
