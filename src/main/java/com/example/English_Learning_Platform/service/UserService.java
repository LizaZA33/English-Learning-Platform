package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.model.dto.mapper.UserMapper;
import com.example.English_Learning_Platform.model.dto.response.UserResponse;
import com.example.English_Learning_Platform.model.entity.UserEntity;
import com.example.English_Learning_Platform.model.enums.Role;
import com.example.English_Learning_Platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Page<UserResponse> findAllUsers(String search, Pageable pageable) {
        Page<UserEntity> page;
        if (search != null && !search.isBlank()) {
            page = userRepository.searchUsers(search, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }
        return page.map(userMapper::toResponse);
    }

    @Transactional
    public UserResponse updateUserRole(Long userId, Role role, boolean add) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        if (add) {
            userEntity.getRoles().add(role);
            log.info("Пользователю {} добавлена роль {}", userEntity.getEmail(), role);
        } else {
            userEntity.getRoles().remove(role);
            log.info("У пользователя {} удалена роль {}", userEntity.getEmail(), role);
        }
        UserEntity saved = userRepository.save(userEntity);
        return userMapper.toResponse(saved);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(userId);
        log.info("Пользователь с id {} удалён", userId);
    }
}
