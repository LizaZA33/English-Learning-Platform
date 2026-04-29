package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.exception.ValidationException;
import com.example.English_Learning_Platform.model.dto.mapper.TeacherMapper;
import com.example.English_Learning_Platform.model.dto.request.TeacherProfileRequest;
import com.example.English_Learning_Platform.model.dto.response.TeacherResponse;
import com.example.English_Learning_Platform.model.entity.TeacherEntity;
import com.example.English_Learning_Platform.model.entity.UserEntity;
import com.example.English_Learning_Platform.model.enums.Role;
import com.example.English_Learning_Platform.repository.TeacherRepository;
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
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final TeacherMapper teacherMapper;

    @Transactional
    public TeacherResponse createProfile(TeacherProfileRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        if (teacherRepository.findByUserId(userEntity.getId()).isPresent()) {
            throw new ValidationException("Профиль учителя уже существует");
        }

        TeacherEntity teacherEntity = TeacherEntity.builder()
                .userEntity(userEntity)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .patronymic(request.getPatronymic())
                .phoneNumber(request.getPhoneNumber())
                .build();

        TeacherEntity saved = teacherRepository.save(teacherEntity);
        userEntity.getRoles().add(Role.TEACHER);
        userRepository.save(userEntity);

        log.info("Создан профиль учителя для пользователя {}", userEntity.getEmail());
        return teacherMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<TeacherResponse> findAllTeachers(String search, Pageable pageable) {
        Page<TeacherEntity> page;
        if (search != null && !search.isBlank()) {
            page = teacherRepository.searchTeachers(search, pageable);
        } else {
            page = teacherRepository.findAll(pageable);
        }
        return page.map(teacherMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(Long id) {
        TeacherEntity teacherEntity = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден"));
        return teacherMapper.toResponse(teacherEntity);
    }

    @Transactional(readOnly = true)
    public TeacherResponse getCurrentTeacherProfile() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        TeacherEntity teacherEntity = teacherRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Профиль учителя не найден"));

        return teacherMapper.toResponse(teacherEntity);
    }

    @Transactional
    public TeacherResponse updateProfile(TeacherProfileRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        TeacherEntity teacherEntity = teacherRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Профиль учителя не найден"));

        if (request.getFirstName() != null) teacherEntity.setFirstName(request.getFirstName());
        if (request.getLastName() != null) teacherEntity.setLastName(request.getLastName());
        if (request.getPatronymic() != null) teacherEntity.setPatronymic(request.getPatronymic());
        if (request.getPhoneNumber() != null) teacherEntity.setPhoneNumber(request.getPhoneNumber());

        TeacherEntity saved = teacherRepository.save(teacherEntity);
        log.info("Обновлён профиль учителя: {}", saved.getId());
        return teacherMapper.toResponse(saved);
    }
}
