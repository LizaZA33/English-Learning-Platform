package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.model.dto.mapper.ModuleMapper;
import com.example.English_Learning_Platform.model.dto.request.ModuleCreateRequest;
import com.example.English_Learning_Platform.model.dto.response.ModuleResponse;
import com.example.English_Learning_Platform.model.entity.ModuleEntity;
import com.example.English_Learning_Platform.repository.ModuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
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
class ModuleServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private ModuleMapper moduleMapper;

    @InjectMocks
    private ModuleService moduleService;
    private ModuleEntity moduleEntity;
    private ModuleResponse moduleResponse;
    private ModuleCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        moduleEntity = new ModuleEntity();
        moduleEntity.setId(1L);
        moduleEntity.setName("Test Module");
        moduleResponse = ModuleResponse.builder()
                .id(1L)
                .name("Test Module")
                .build();
        createRequest = new ModuleCreateRequest();
        createRequest.setName("Test Module");
    }

    @Test
    void shouldCreateModuleWhenValidRequest() {
        when(moduleRepository.save(any(ModuleEntity.class))).thenReturn(moduleEntity);
        when(moduleMapper.toResponse(moduleEntity)).thenReturn(moduleResponse);
        ModuleResponse result = moduleService.createModule(createRequest);
        assertNotNull(result);
        assertEquals("Test Module", result.getName());
        assertEquals(1L, result.getId());
        verify(moduleRepository).save(any(ModuleEntity.class));
    }

    @Test
    void shouldReturnModuleByIdWhenExists() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(moduleEntity));
        when(moduleMapper.toResponse(moduleEntity)).thenReturn(moduleResponse);
        ModuleResponse result = moduleService.getModuleById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Module", result.getName());
        verify(moduleRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenModuleNotFound() {
        when(moduleRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> moduleService.getModuleById(999L));
        assertEquals("Модуль не найден", exception.getMessage());
        verify(moduleRepository).findById(999L);
    }

    @Test
    void shouldReturnAllModulesWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ModuleEntity> page = new PageImpl<>(List.of(moduleEntity));
        when(moduleRepository.findAll(pageable)).thenReturn(page);
        when(moduleMapper.toResponse(moduleEntity)).thenReturn(moduleResponse);
        Page<ModuleResponse> result = moduleService.getAllModules(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(moduleRepository).findAll(pageable);
    }

    @Test
    void shouldSearchModulesByName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ModuleEntity> page = new PageImpl<>(List.of(moduleEntity));
        when(moduleRepository.findByNameContainingIgnoreCase("Test", pageable)).thenReturn(page);
        when(moduleMapper.toResponse(moduleEntity)).thenReturn(moduleResponse);
        Page<ModuleResponse> result = moduleService.searchByName("Test", pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(moduleRepository).findByNameContainingIgnoreCase("Test", pageable);
    }

    @Test
    void shouldDeleteModuleWhenExists() {
        when(moduleRepository.existsById(1L)).thenReturn(true);
        moduleService.deleteModule(1L);
        verify(moduleRepository).existsById(1L);
        verify(moduleRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentModule() {
        when(moduleRepository.existsById(999L)).thenReturn(false);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> moduleService.deleteModule(999L));
        assertEquals("Модуль не найден", exception.getMessage());
        verify(moduleRepository).existsById(999L);
        verify(moduleRepository, never()).deleteById(any());
    }

    @Test
    void shouldUseArgumentCaptorOnCreateModule() {
        when(moduleRepository.save(any(ModuleEntity.class))).thenReturn(moduleEntity);
        when(moduleMapper.toResponse(moduleEntity)).thenReturn(moduleResponse);
        moduleService.createModule(createRequest);
        ArgumentCaptor<ModuleEntity> captor = ArgumentCaptor.forClass(ModuleEntity.class);
        verify(moduleRepository).save(captor.capture());
        ModuleEntity captured = captor.getValue();
        assertEquals("Test Module", captured.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Grammar", "Vocabulary", "Speaking", "Listening"})
    void shouldSearchModulesByVariousNames(String searchTerm) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ModuleEntity> page = new PageImpl<>(List.of(moduleEntity));
        when(moduleRepository.findByNameContainingIgnoreCase(searchTerm, pageable)).thenReturn(page);
        when(moduleMapper.toResponse(moduleEntity)).thenReturn(moduleResponse);
        Page<ModuleResponse> result = moduleService.searchByName(searchTerm, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(moduleRepository).findByNameContainingIgnoreCase(searchTerm, pageable);
    }
}