package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.model.dto.mapper.ModuleMapper;
import com.example.English_Learning_Platform.model.dto.request.ModuleCreateRequest;
import com.example.English_Learning_Platform.model.dto.response.ModuleResponse;
import com.example.English_Learning_Platform.model.entity.ModuleEntity;
import com.example.English_Learning_Platform.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final ModuleMapper moduleMapper;

    @Transactional
    public ModuleResponse createModule(ModuleCreateRequest request) {
        ModuleEntity entity = new ModuleEntity();
        entity.setName(request.getName());
        ModuleEntity saved = moduleRepository.save(entity);
        log.info("Создан модуль: {}", saved.getName());
        return moduleMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ModuleResponse> getAllModules(Pageable pageable) {
        return moduleRepository.findAll(pageable).map(moduleMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ModuleResponse getModuleById(Long id) {
        ModuleEntity entity = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Модуль не найден"));
        return moduleMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<ModuleResponse> searchByName(String name, Pageable pageable) {
        return moduleRepository.findByNameContainingIgnoreCase(name, pageable).map(moduleMapper::toResponse);
    }

    @Transactional
    public void deleteModule(Long id) {
        if (!moduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Модуль не найден");
        }
        moduleRepository.deleteById(id);
        log.info("Удалён модуль с id {}", id);
    }
}