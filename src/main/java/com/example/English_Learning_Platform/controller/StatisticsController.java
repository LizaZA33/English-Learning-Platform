package com.example.English_Learning_Platform.controller;

import com.example.English_Learning_Platform.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Statistics", description = "API для получения статистики платформы")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/students/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(
            summary = "Количество студентов",
            description = "Возвращает общее количество студентов на платформе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Количество студентов получено",
                    content = @Content(schema = @Schema(type = "integer", example = "150")))
    })
    public ResponseEntity<Long> getStudentCount() {
        return ResponseEntity.ok(statisticsService.getStudentCount());
    }

    @GetMapping("/teachers/count")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Количество учителей",
            description = "Возвращает общее количество учителей на платформе. Доступно только администратору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Количество учителей получено",
                    content = @Content(schema = @Schema(type = "integer", example = "25")))
    })
    public ResponseEntity<Long> getTeacherCount() {
        return ResponseEntity.ok(statisticsService.getTeacherCount());
    }

    @GetMapping("/lessons/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(
            summary = "Количество уроков",
            description = "Возвращает общее количество уроков на платформе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Количество уроков получено",
                    content = @Content(schema = @Schema(type = "integer", example = "500")))
    })
    public ResponseEntity<Long> getLessonCount() {
        return ResponseEntity.ok(statisticsService.getLessonCount());
    }

    @GetMapping("/groups/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(
            summary = "Количество групп",
            description = "Возвращает общее количество учебных групп на платформе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Количество групп получено",
                    content = @Content(schema = @Schema(type = "integer", example = "30")))
    })
    public ResponseEntity<Long> getGroupCount() {
        return ResponseEntity.ok(statisticsService.getGroupCount());
    }
}