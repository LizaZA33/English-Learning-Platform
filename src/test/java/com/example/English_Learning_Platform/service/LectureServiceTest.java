package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.model.dto.mapper.LectureMapper;
import com.example.English_Learning_Platform.model.dto.request.LectureCreateRequest;
import com.example.English_Learning_Platform.model.dto.response.LectureResponse;
import com.example.English_Learning_Platform.model.dto.response.ModuleResponse;
import com.example.English_Learning_Platform.model.entity.LectureEntity;
import com.example.English_Learning_Platform.model.entity.ModuleEntity;
import com.example.English_Learning_Platform.repository.LectureRepository;
import com.example.English_Learning_Platform.repository.ModuleRepository;
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
class LectureServiceTest {

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private LectureMapper lectureMapper;

    @InjectMocks
    private LectureService lectureService;

    @Test
    void shouldCreateLectureWhenValidRequest() {
        LectureCreateRequest request = new LectureCreateRequest();
        request.setTitle("Test Lecture");
        request.setContent("Content");
        request.setModuleName("Test Module");
        ModuleEntity module = new ModuleEntity();
        module.setId(1L);
        module.setName("Test Module");
        LectureEntity entity = new LectureEntity();
        entity.setId(1L);
        entity.setTitle("Test Lecture");
        entity.setModuleEntity(module);
        LectureResponse response = LectureResponse.builder()
                .id(1L).title("Test Lecture")
                .module(ModuleResponse.builder().id(1L).name("Test Module").build())
                .build();
        when(moduleRepository.findByName("Test Module")).thenReturn(Optional.of(module));
        when(lectureRepository.save(any(LectureEntity.class))).thenReturn(entity);
        when(lectureMapper.toResponse(entity)).thenReturn(response);
        LectureResponse result = lectureService.createLecture(request);
        assertNotNull(result);
        assertEquals("Test Lecture", result.getTitle());
        verify(lectureRepository).save(any(LectureEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenModuleNotFoundForLecture() {
        LectureCreateRequest request = new LectureCreateRequest();
        request.setModuleName("NonExistent");
        when(moduleRepository.findByName("NonExistent")).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> lectureService.createLecture(request));
        assertEquals("Модуль не найден", exception.getMessage());
    }

    @Test
    void shouldReturnLectureByIdWhenExists() {
        LectureEntity entity = new LectureEntity();
        entity.setId(1L);
        entity.setTitle("Test");
        ModuleEntity module = new ModuleEntity();
        module.setId(1L);
        entity.setModuleEntity(module);
        LectureResponse response = LectureResponse.builder().id(1L).title("Test").build();
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(lectureMapper.toResponse(entity)).thenReturn(response);
        LectureResponse result = lectureService.getLectureById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowExceptionWhenLectureNotFound() {
        when(lectureRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> lectureService.getLectureById(999L));
    }

    @Test
    void shouldReturnAllLecturesWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        LectureEntity entity = new LectureEntity();
        entity.setId(1L);
        Page<LectureEntity> page = new PageImpl<>(List.of(entity));
        when(lectureRepository.findAll(pageable)).thenReturn(page);
        when(lectureMapper.toResponse(entity)).thenReturn(
                LectureResponse.builder().id(1L).build());
        Page<LectureResponse> result = lectureService.getAllLectures(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldReturnLecturesByModule() {
        Long moduleId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        LectureEntity entity = new LectureEntity();
        entity.setId(1L);
        Page<LectureEntity> page = new PageImpl<>(List.of(entity));
        when(lectureRepository.findByModuleEntityId(moduleId, pageable)).thenReturn(page);
        when(lectureMapper.toResponse(entity)).thenReturn(
                LectureResponse.builder().id(1L).build());

        Page<LectureResponse> result = lectureService.getLecturesByModule(moduleId, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldSearchLecturesByTitle() {
        Pageable pageable = PageRequest.of(0, 10);
        LectureEntity entity = new LectureEntity();
        entity.setId(1L);
        Page<LectureEntity> page = new PageImpl<>(List.of(entity));
        when(lectureRepository.searchByTitle("grammar", pageable)).thenReturn(page);
        when(lectureMapper.toResponse(entity)).thenReturn(
                LectureResponse.builder().id(1L).build());
        Page<LectureResponse> result = lectureService.searchByTitle("grammar", pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldUpdateLectureWhenExists() {
        Long id = 1L;
        LectureCreateRequest request = new LectureCreateRequest();
        request.setTitle("Updated");
        request.setContent("New content");
        request.setModuleName("Test Module");
        ModuleEntity module = new ModuleEntity();
        module.setId(1L);
        module.setName("Test Module");
        LectureEntity entity = new LectureEntity();
        entity.setId(id);
        entity.setTitle("Old");
        entity.setModuleEntity(module);
        LectureResponse response = LectureResponse.builder().id(id).title("Updated").build();
        when(lectureRepository.findById(id)).thenReturn(Optional.of(entity));
        when(moduleRepository.findByName("Test Module")).thenReturn(Optional.of(module));
        when(lectureRepository.save(entity)).thenReturn(entity);
        when(lectureMapper.toResponse(entity)).thenReturn(response);
        LectureResponse result = lectureService.updateLecture(id, request);
        assertNotNull(result);
        assertEquals("Updated", result.getTitle());
        verify(lectureRepository).save(entity);
    }

    @Test
    void shouldDeleteLectureWhenExists() {
        when(lectureRepository.existsById(1L)).thenReturn(true);
        lectureService.deleteLecture(1L);
        verify(lectureRepository).existsById(1L);
        verify(lectureRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentLecture() {
        when(lectureRepository.existsById(999L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> lectureService.deleteLecture(999L));
        verify(lectureRepository, never()).deleteById(any());
    }
}