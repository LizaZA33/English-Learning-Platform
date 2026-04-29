package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.model.dto.mapper.LectureMapper;
import com.example.English_Learning_Platform.model.dto.request.LectureCreateRequest;
import com.example.English_Learning_Platform.model.dto.response.LectureResponse;
import com.example.English_Learning_Platform.model.entity.LectureEntity;
import com.example.English_Learning_Platform.model.entity.ModuleEntity;
import com.example.English_Learning_Platform.repository.LectureRepository;
import com.example.English_Learning_Platform.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureService {

    private final LectureRepository lectureRepository;
    private final ModuleRepository moduleRepository;
    private final LectureMapper lectureMapper;

    @Transactional
    public LectureResponse createLecture(LectureCreateRequest request) {
        ModuleEntity module = moduleRepository.findByName(request.getModuleName())
                .orElseThrow(() -> new ResourceNotFoundException("Модуль не найден"));
        LectureEntity entity = new LectureEntity();
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());
        entity.setModuleEntity(module);
        LectureEntity saved = lectureRepository.save(entity);
        log.info("Создана лекция: {}", saved.getTitle());
        return lectureMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<LectureResponse> getAllLectures(Pageable pageable) {
        return lectureRepository.findAll(pageable).map(lectureMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public LectureResponse getLectureById(Long id) {
        LectureEntity entity = lectureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Лекция не найдена"));
        return lectureMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<LectureResponse> getLecturesByModule(Long moduleId, Pageable pageable) {
        return lectureRepository.findByModuleEntityId(moduleId, pageable).map(lectureMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<LectureResponse> searchByTitle(String title, Pageable pageable) {
        return lectureRepository.searchByTitle(title, pageable).map(lectureMapper::toResponse);
    }

    @Transactional
    public LectureResponse updateLecture(Long id, LectureCreateRequest request) {
        LectureEntity entity = lectureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Лекция не найдена"));
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());
        if (request.getModuleName() != null) {
            ModuleEntity module = moduleRepository.findByName(request.getModuleName())
                    .orElseThrow(() -> new ResourceNotFoundException("Модуль не найден"));
            entity.setModuleEntity(module);
        }
        LectureEntity saved = lectureRepository.save(entity);
        log.info("Обновлена лекция: {}", saved.getId());
        return lectureMapper.toResponse(saved);
    }

    @Transactional
    public void deleteLecture(Long id) {
        if (!lectureRepository.existsById(id)) {
            throw new ResourceNotFoundException("Лекция не найдена");
        }
        lectureRepository.deleteById(id);
        log.info("Удалена лекция с id {}", id);
    }
}