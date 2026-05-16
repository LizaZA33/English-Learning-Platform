package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.model.dto.mapper.FlashcardMapper;
import com.example.English_Learning_Platform.model.dto.request.FlashcardRequest;
import com.example.English_Learning_Platform.model.dto.response.FlashcardResponse;
import com.example.English_Learning_Platform.model.entity.FlashcardEntity;
import com.example.English_Learning_Platform.model.entity.LessonEntity;
import com.example.English_Learning_Platform.repository.FlashcardRepository;
import com.example.English_Learning_Platform.repository.LessonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlashcardServiceTest {

    @Mock
    private FlashcardRepository flashcardRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private FlashcardMapper flashcardMapper;

    @InjectMocks
    private FlashcardService flashcardService;

    @Test
    void shouldCreateFlashcardWhenValidRequest() {
        FlashcardRequest request = new FlashcardRequest();
        request.setLessonId(1L);
        request.setTerm("Hello");
        request.setTranslation("Привет");
        LessonEntity lesson = new LessonEntity();
        lesson.setId(1L);
        FlashcardEntity entity = new FlashcardEntity();
        entity.setId(1L);
        entity.setTerm("Hello");
        entity.setLessonEntity(lesson);
        FlashcardResponse response = FlashcardResponse.builder()
                .id(1L).term("Hello").lessonId(1L).build();
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
        when(flashcardMapper.toEntity(request)).thenReturn(entity);
        when(flashcardRepository.save(any(FlashcardEntity.class))).thenReturn(entity);
        when(flashcardMapper.toResponse(entity)).thenReturn(response);
        FlashcardResponse result = flashcardService.createFlashcard(request);
        assertNotNull(result);
        assertEquals("Hello", result.getTerm());
        verify(flashcardRepository).save(any(FlashcardEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingFlashcardForNonExistentLesson() {
        FlashcardRequest request = new FlashcardRequest();
        request.setLessonId(999L);
        when(lessonRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> flashcardService.createFlashcard(request));
        assertEquals("Урок не найден", exception.getMessage());
        verify(flashcardRepository, never()).save(any());
    }

    @Test
    void shouldReturnFlashcardByIdWhenExists() {
        FlashcardEntity entity = new FlashcardEntity();
        entity.setId(1L);
        entity.setTerm("Test");
        FlashcardResponse response = FlashcardResponse.builder().id(1L).term("Test").build();
        when(flashcardRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(flashcardMapper.toResponse(entity)).thenReturn(response);
        FlashcardResponse result = flashcardService.getFlashcardById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(flashcardRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenFlashcardNotFound() {
        when(flashcardRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> flashcardService.getFlashcardById(999L));
    }

    @Test
    void shouldUpdateFlashcardWhenExists() {
        Long id = 1L;
        FlashcardRequest request = new FlashcardRequest();
        request.setTerm("Updated");
        request.setDefinition("New def");
        request.setExample("Example");
        request.setTranslation("Перевод");
        request.setDifficulty(3);
        FlashcardEntity entity = new FlashcardEntity();
        entity.setId(id);
        entity.setTerm("Old");
        FlashcardResponse response = FlashcardResponse.builder().id(id).term("Updated").build();
        when(flashcardRepository.findById(id)).thenReturn(Optional.of(entity));
        when(flashcardRepository.save(entity)).thenReturn(entity);
        when(flashcardMapper.toResponse(entity)).thenReturn(response);
        FlashcardResponse result = flashcardService.updateFlashcard(id, request);
        assertNotNull(result);
        assertEquals("Updated", result.getTerm());
        verify(flashcardRepository).save(entity);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentFlashcard() {
        when(flashcardRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> flashcardService.updateFlashcard(999L, new FlashcardRequest()));
    }

    @Test
    void shouldDeleteFlashcardWhenExists() {
        when(flashcardRepository.existsById(1L)).thenReturn(true);
        flashcardService.deleteFlashcard(1L);
        verify(flashcardRepository).existsById(1L);
        verify(flashcardRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentFlashcard() {
        when(flashcardRepository.existsById(999L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> flashcardService.deleteFlashcard(999L));
        verify(flashcardRepository, never()).deleteById(any());
    }

    @Test
    void shouldReturnFlashcardsByLessonWithPagination() {
        Long lessonId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        LessonEntity lesson = new LessonEntity();
        lesson.setId(lessonId);
        FlashcardEntity entity = new FlashcardEntity();
        entity.setId(1L);
        Page<FlashcardEntity> page = new PageImpl<>(List.of(entity));
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(flashcardRepository.findByLessonEntity(lesson, pageable)).thenReturn(page);
        when(flashcardMapper.toResponse(entity)).thenReturn(
                FlashcardResponse.builder().id(1L).build());
        Page<FlashcardResponse> result = flashcardService.getFlashcardsByLesson(lessonId, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldSearchFlashcardsByTerm() {
        Pageable pageable = PageRequest.of(0, 10);
        FlashcardEntity entity = new FlashcardEntity();
        entity.setId(1L);
        entity.setTerm("vocabulary");
        Page<FlashcardEntity> page = new PageImpl<>(List.of(entity));
        when(flashcardRepository.findByTermContainingIgnoreCase("vocab", pageable))
                .thenReturn(page);
        when(flashcardMapper.toResponse(entity)).thenReturn(
                FlashcardResponse.builder().id(1L).term("vocabulary").build());
        Page<FlashcardResponse> result = flashcardService.searchByTerm("vocab", pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
}