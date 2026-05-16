package com.example.English_Learning_Platform.controller;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.model.dto.request.FlashcardRequest;
import com.example.English_Learning_Platform.model.dto.response.FlashcardResponse;
import com.example.English_Learning_Platform.security.JwtAuthenticationFilter;
import com.example.English_Learning_Platform.security.JwtUtils;
import com.example.English_Learning_Platform.service.FlashcardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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

@WebMvcTest(value = FlashcardController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {JwtAuthenticationFilter.class})
})
@AutoConfigureMockMvc(addFilters = false)
class FlashcardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private FlashcardService flashcardService;
    @MockBean
    private JwtUtils jwtUtils;
    @Test
    void shouldReturnFlashcardById_whenFlashcardExists() throws Exception {
        FlashcardResponse response = FlashcardResponse.builder()
                .id(1L)
                .term("hello")
                .definition("a greeting")
                .example("Hello world")
                .translation("привет")
                .difficulty(1)
                .lessonId(10L)
                .build();

        when(flashcardService.getFlashcardById(1L)).thenReturn(response);
        mockMvc.perform(get("/api/flashcards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.term").value("hello"))
                .andExpect(jsonPath("$.definition").value("a greeting"))
                .andExpect(jsonPath("$.translation").value("привет"))
                .andExpect(jsonPath("$.difficulty").value(1))
                .andExpect(jsonPath("$.lessonId").value(10));
    }

    @Test
    void shouldReturn404_whenFlashcardNotFound() throws Exception {
        when(flashcardService.getFlashcardById(999L))
                .thenThrow(new ResourceNotFoundException("Флешкарточка не найдена"));
        mockMvc.perform(get("/api/flashcards/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Флешкарточка не найдена"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "world", "test"})
    void shouldSearchFlashcardsByTerm(String term) throws Exception {
        FlashcardResponse response = FlashcardResponse.builder()
                .id(1L)
                .term(term)
                .definition("definition")
                .translation("перевод")
                .difficulty(1)
                .lessonId(1L)
                .build();
        Page<FlashcardResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);
        when(flashcardService.searchByTerm(eq(term), any())).thenReturn(page);
        mockMvc.perform(get("/api/flashcards/search")
                        .param("term", term))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].term").value(term));
    }
    @Test
    void shouldReturn400_whenCreatingFlashcardWithInvalidData() throws Exception {
        FlashcardRequest request = new FlashcardRequest();
        request.setLessonId(null);
        request.setTerm("");
        request.setTranslation("");
        mockMvc.perform(post("/api/flashcards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
    @Test
    void shouldReturn204_whenDeletingFlashcardSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/flashcards/1"))
                .andExpect(status().isNoContent());
    }
    @Test
    void shouldReturn404_whenDeletingNonexistentFlashcard() throws Exception {
        doThrow(new ResourceNotFoundException("Флешкарточка не найдена"))
                .when(flashcardService).deleteFlashcard(999L);

        mockMvc.perform(delete("/api/flashcards/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Флешкарточка не найдена"));
    }
}