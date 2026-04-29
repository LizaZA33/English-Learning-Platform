package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.model.dto.mapper.FlashcardMapper;
import com.example.English_Learning_Platform.model.dto.request.FlashcardRequest;
import com.example.English_Learning_Platform.model.dto.response.FlashcardResponse;
import com.example.English_Learning_Platform.model.entity.FlashcardEntity;
import com.example.English_Learning_Platform.model.entity.LessonEntity;
import com.example.English_Learning_Platform.repository.FlashcardRepository;
import com.example.English_Learning_Platform.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
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

    private LessonEntity lessonEntity;
    private FlashcardEntity flashcardEntity;
    private FlashcardRequest flashcardRequest;
    private FlashcardResponse flashcardResponse;

    @BeforeEach
    void setUp() {
        lessonEntity = new LessonEntity();
        lessonEntity.setId(1L);
        lessonEntity.setTitle("Test Lesson");

        flashcardEntity = new FlashcardEntity();
        flashcardEntity.setId(1L);
        flashcardEntity.setTerm("Hello");
        flashcardEntity.setDefinition("A greeting");
        flashcardEntity.setTranslation("Привет");
        flashcardEntity.setDifficulty(1);
        flashcardEntity.setLessonEntity(lessonEntity);

        flashcardRequest = new FlashcardRequest();
        flashcardRequest.setLessonId(1L);
        flashcardRequest.setTerm("Hello");
        flashcardRequest.setDefinition("A greeting");
        flashcardRequest.setTranslation("Привет");
        flashcardRequest.setDifficulty(1);

        flashcardResponse = FlashcardResponse.builder()
                .id(1L)
                .term("Hello")
                .definition("A greeting")
                .translation("Привет")
                .difficulty(1)
                .lessonId(1L)
                .build();
    }

    @Test
    void createFlashcard_Success() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lessonEntity));
        when(flashcardMapper.toEntity(flashcardRequest)).thenReturn(flashcardEntity);
        when(flashcardRepository.save(any(FlashcardEntity.class))).thenReturn(flashcardEntity);
        when(flashcardMapper.toResponse(flashcardEntity)).thenReturn(flashcardResponse);

        FlashcardResponse response = flashcardService.createFlashcard(flashcardRequest);

        assertNotNull(response);
        assertEquals("Hello", response.getTerm());
        assertEquals(1L, response.getLessonId());
        verify(flashcardRepository, times(1)).save(any(FlashcardEntity.class));
    }

    @Test
    void createFlashcard_LessonNotFound_ThrowsException() {
        when(lessonRepository.findById(99L)).thenReturn(Optional.empty());
        flashcardRequest.setLessonId(99L);

        assertThrows(ResourceNotFoundException.class,
                () -> flashcardService.createFlashcard(flashcardRequest));
    }

    @Test
    void getFlashcardById_Success() {
        when(flashcardRepository.findById(1L)).thenReturn(Optional.of(flashcardEntity));
        when(flashcardMapper.toResponse(flashcardEntity)).thenReturn(flashcardResponse);

        FlashcardResponse response = flashcardService.getFlashcardById(1L);

        assertNotNull(response);
        assertEquals("Hello", response.getTerm());
    }

    @Test
    void getFlashcardById_NotFound_ThrowsException() {
        when(flashcardRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> flashcardService.getFlashcardById(99L));
    }

    @Test
    void getFlashcardsByLesson_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<FlashcardEntity> page = new PageImpl<>(List.of(flashcardEntity));

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lessonEntity));
        when(flashcardRepository.findByLessonEntity(lessonEntity, pageable)).thenReturn(page);
        when(flashcardMapper.toResponse(any())).thenReturn(flashcardResponse);

        Page<FlashcardResponse> response = flashcardService.getFlashcardsByLesson(1L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }
}
