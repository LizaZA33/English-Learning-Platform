package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.exception.ValidationException;
import com.example.English_Learning_Platform.model.dto.mapper.StudentMapper;
import com.example.English_Learning_Platform.model.dto.request.StudentProfileRequest;
import com.example.English_Learning_Platform.model.dto.response.StudentResponse;
import com.example.English_Learning_Platform.model.entity.StudentEntity;
import com.example.English_Learning_Platform.model.entity.UserEntity;
import com.example.English_Learning_Platform.model.enums.Role;
import com.example.English_Learning_Platform.repository.StudentRepository;
import com.example.English_Learning_Platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final StudentMapper studentMapper;

    @Transactional
    public StudentResponse createProfile(StudentProfileRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        if (studentRepository.findByUserId(userEntity.getId()).isPresent()) {
            throw new ValidationException("Профиль студента уже существует");
        }

        StudentEntity studentEntity = StudentEntity.builder()
                .userEntity(userEntity)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .patronymic(request.getPatronymic())
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .build();

        StudentEntity saved = studentRepository.save(studentEntity);
        userEntity.getRoles().add(Role.STUDENT);
        userRepository.save(userEntity);

        log.info("Создан профиль студента для пользователя {}", userEntity.getEmail());
        return studentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<StudentResponse> findAllStudents(String search, Pageable pageable) {
        Page<StudentEntity> page;
        if (search != null && !search.isBlank()) {
            page = studentRepository.searchStudents(search, pageable);
        } else {
            page = studentRepository.findAll(pageable);
        }
        return page.map(studentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        StudentEntity studentEntity = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Студент не найден с id: " + id));
        return studentMapper.toResponse(studentEntity);
    }

    @Transactional(readOnly = true)
    public StudentResponse getCurrentStudentProfile() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        StudentEntity studentEntity = studentRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Профиль студента не найден"));

        return studentMapper.toResponse(studentEntity);
    }

    @Transactional
    public StudentResponse updateProfile(StudentProfileRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        StudentEntity studentEntity = studentRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Профиль студента не найден"));

        if (request.getFirstName() != null) studentEntity.setFirstName(request.getFirstName());
        if (request.getLastName() != null) studentEntity.setLastName(request.getLastName());
        if (request.getPatronymic() != null) studentEntity.setPatronymic(request.getPatronymic());
        if (request.getDateOfBirth() != null) studentEntity.setDateOfBirth(request.getDateOfBirth());
        if (request.getPhoneNumber() != null) studentEntity.setPhoneNumber(request.getPhoneNumber());

        StudentEntity saved = studentRepository.save(studentEntity);
        log.info("Обновлён профиль студента: {}", saved.getId());
        return studentMapper.toResponse(saved);
    }
}