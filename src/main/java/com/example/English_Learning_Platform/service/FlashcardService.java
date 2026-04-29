package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.model.dto.mapper.FlashcardMapper;
import com.example.English_Learning_Platform.model.dto.request.FlashcardRequest;
import com.example.English_Learning_Platform.model.dto.response.FlashcardResponse;
import com.example.English_Learning_Platform.model.entity.FlashcardEntity;
import com.example.English_Learning_Platform.model.entity.LessonEntity;
import com.example.English_Learning_Platform.repository.FlashcardRepository;
import com.example.English_Learning_Platform.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final LessonRepository lessonRepository;
    private final FlashcardMapper flashcardMapper;

    @Transactional
    public FlashcardResponse createFlashcard(FlashcardRequest request) {
        LessonEntity lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));
        FlashcardEntity entity = flashcardMapper.toEntity(request);
        entity.setLessonEntity(lesson);
        FlashcardEntity saved = flashcardRepository.save(entity);
        log.info("Создана флешкарточка: {} для урока {}", saved.getTerm(), lesson.getId());
        return flashcardMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public FlashcardResponse getFlashcardById(Long id) {
        FlashcardEntity entity = flashcardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Флешкарточка не найдена"));
        return flashcardMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<FlashcardResponse> getFlashcardsByLesson(Long lessonId, Pageable pageable) {
        LessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));
        Page<FlashcardEntity> page = flashcardRepository.findByLessonEntity(lesson, pageable);
        return page.map(flashcardMapper::toResponse);
    }

    @Transactional
    public FlashcardResponse updateFlashcard(Long id, FlashcardRequest request) {
        FlashcardEntity entity = flashcardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Флешкарточка не найдена"));
        entity.setTerm(request.getTerm());
        entity.setDefinition(request.getDefinition());
        entity.setExample(request.getExample());
        entity.setTranslation(request.getTranslation());
        entity.setDifficulty(request.getDifficulty());
        FlashcardEntity saved = flashcardRepository.save(entity);
        log.info("Обновлена флешкарточка: {}", saved.getId());
        return flashcardMapper.toResponse(saved);
    }

    @Transactional
    public void deleteFlashcard(Long id) {
        if (!flashcardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Флешкарточка не найдена");
        }
        flashcardRepository.deleteById(id);
        log.info("Удалена флешкарточка с id {}", id);
    }

    @Transactional(readOnly = true)
    public Page<FlashcardResponse> searchByTerm(String term, Pageable pageable) {
        Page<FlashcardEntity> page = flashcardRepository.findByTermContainingIgnoreCase(term, pageable);
        return page.map(flashcardMapper::toResponse);
    }
}