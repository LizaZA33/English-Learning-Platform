package com.example.English_Learning_Platform.controller;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.model.dto.request.ModuleCreateRequest;
import com.example.English_Learning_Platform.model.dto.response.ModuleResponse;
import com.example.English_Learning_Platform.security.JwtAuthenticationFilter;
import com.example.English_Learning_Platform.security.JwtUtils;
import com.example.English_Learning_Platform.service.ModuleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ModuleController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {JwtAuthenticationFilter.class})
})
@AutoConfigureMockMvc(addFilters = false)
class ModuleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ModuleService moduleService;
    @MockBean
    private JwtUtils jwtUtils;
    @Test
    void shouldReturnModuleById_whenModuleExists() throws Exception {
        ModuleResponse response = ModuleResponse.builder()
                .id(1L)
                .name("Базовый английский")
                .build();
        when(moduleService.getModuleById(1L)).thenReturn(response);
        mockMvc.perform(get("/api/modules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Базовый английский"));
    }

    @Test
    void shouldReturn404_whenModuleNotFound() throws Exception {
        when(moduleService.getModuleById(999L))
                .thenThrow(new ResourceNotFoundException("Модуль не найден"));
        mockMvc.perform(get("/api/modules/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Модуль не найден"));
    }

    @Test
    void shouldReturnAllModules() throws Exception {
        ModuleResponse module1 = ModuleResponse.builder().id(1L).name("Модуль 1").build();
        ModuleResponse module2 = ModuleResponse.builder().id(2L).name("Модуль 2").build();
        Page<ModuleResponse> page = new PageImpl<>(List.of(module1, module2), PageRequest.of(0, 10), 2);
        when(moduleService.getAllModules(any())).thenReturn(page);
        mockMvc.perform(get("/api/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Модуль 1"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].name").value("Модуль 2"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void shouldCreateModule_whenValidData() throws Exception {
        ModuleCreateRequest request = new ModuleCreateRequest();
        request.setName("Новый модуль");
        ModuleResponse response = ModuleResponse.builder()
                .id(10L)
                .name("Новый модуль")
                .build();
        when(moduleService.createModule(any(ModuleCreateRequest.class))).thenReturn(response);
        mockMvc.perform(post("/api/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Новый модуль"));
    }

    @Test
    void shouldReturn400_whenCreatingModuleWithBlankName() throws Exception {
        ModuleCreateRequest request = new ModuleCreateRequest();
        request.setName("");
        mockMvc.perform(post("/api/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"базовый", "продвинутый", "английский"})
    void shouldSearchModulesByName(String name) throws Exception {
        ModuleResponse response = ModuleResponse.builder()
                .id(1L)
                .name("Базовый английский")
                .build();
        Page<ModuleResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);
        when(moduleService.searchByName(eq(name), any())).thenReturn(page);
        mockMvc.perform(get("/api/modules/search")
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Базовый английский"));
    }

    @ParameterizedTest
    @CsvSource({
            "1, Базовый английский",
            "2, Продвинутый английский",
            "3, Бизнес английский"
    })
    void shouldReturnModuleWithExpectedName(long id, String expectedName) throws Exception {
        ModuleResponse response = ModuleResponse.builder()
                .id(id)
                .name(expectedName)
                .build();
        when(moduleService.getModuleById(id)).thenReturn(response);
        mockMvc.perform(get("/api/modules/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(expectedName));
    }

    @Test
    void shouldReturn204_whenDeletingModuleSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/modules/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404_whenDeletingNonexistentModule() throws Exception {
        doThrow(new ResourceNotFoundException("Модуль не найден"))
                .when(moduleService).deleteModule(999L);
        mockMvc.perform(delete("/api/modules/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Модуль не найден"));
    }
}