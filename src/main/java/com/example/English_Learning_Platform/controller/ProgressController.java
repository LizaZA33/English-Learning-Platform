package com.example.English_Learning_Platform.controller;

import com.example.English_Learning_Platform.model.dto.response.LectureProgressResponse;
import com.example.English_Learning_Platform.model.dto.response.LessonProgressResponse;
import com.example.English_Learning_Platform.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Progress", description = "API для отслеживания прогресса обучения")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/lectures")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Получить прогресс по лекциям",
            description = "Возвращает прогресс текущего студента по всем лекциям"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Прогресс получен")
    })
    public ResponseEntity<Page<LectureProgressResponse>> getLectureProgress(
            Principal principal,
            Pageable pageable) {
        Page<LectureProgressResponse> page = progressService.getLectureProgress(principal.getName(), pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/lessons")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Получить прогресс по урокам",
            description = "Возвращает прогресс текущего студента по всем урокам"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Прогресс получен")
    })
    public ResponseEntity<Page<LessonProgressResponse>> getLessonProgress(
            Principal principal,
            Pageable pageable) {
        Page<LessonProgressResponse> page = progressService.getLessonProgress(principal.getName(), pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/lectures/{lectureId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Обновить прогресс по лекции",
            description = "Обновляет процент завершения лекции для текущего студента"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Прогресс обновлен"),
            @ApiResponse(responseCode = "404", description = "Лекция не найдена")
    })
    public ResponseEntity<Void> updateLectureProgress(
            @Parameter(description = "ID лекции", example = "1")
            @PathVariable Long lectureId,
            @Parameter(description = "Процент завершения (0-100)", example = "75")
            @RequestParam Integer progressPercent,
            Principal principal) {
        progressService.updateLectureProgress(principal.getName(), lectureId, progressPercent);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/lessons/{lessonId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Обновить прогресс по уроку",
            description = "Обновляет процент завершения урока для текущего студента"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Прогресс обновлен"),
            @ApiResponse(responseCode = "404", description = "Урок не найден")
    })
    public ResponseEntity<Void> updateLessonProgress(
            @Parameter(description = "ID урока", example = "1")
            @PathVariable Long lessonId,
            @Parameter(description = "Процент завершения (0-100)", example = "50")
            @RequestParam Integer progressPercent,
            Principal principal) {
        progressService.updateLessonProgress(principal.getName(), lessonId, progressPercent);
        return ResponseEntity.ok().build();
    }
}