package com.example.English_Learning_Platform.controller;

import com.example.English_Learning_Platform.model.dto.request.LectureCreateRequest;
import com.example.English_Learning_Platform.model.dto.response.LectureResponse;
import com.example.English_Learning_Platform.service.LectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lectures")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Lectures", description = "API для управления лекциями")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class LectureController {

    private final LectureService lectureService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(
            summary = "Создать лекцию",
            description = "Создает новую лекцию в указанном модуле. Доступно только для учителей"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Лекция создана",
                    content = @Content(schema = @Schema(implementation = LectureResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "404", description = "Модуль не найден")
    })
    public ResponseEntity<LectureResponse> createLecture(@Valid @RequestBody LectureCreateRequest request) {
        LectureResponse response = lectureService.createLecture(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    @Operation(
            summary = "Получить список всех лекций",
            description = "Возвращает список всех доступных лекций"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список лекций получен")
    })
    public ResponseEntity<Page<LectureResponse>> getAllLectures(Pageable pageable) {
        Page<LectureResponse> page = lectureService.getAllLectures(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    @Operation(
            summary = "Получить лекцию по ID",
            description = "Возвращает содержимое лекции"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лекция найдена",
                    content = @Content(schema = @Schema(implementation = LectureResponse.class))),
            @ApiResponse(responseCode = "404", description = "Лекция не найдена")
    })
    public ResponseEntity<LectureResponse> getLectureById(
            @Parameter(description = "ID лекции", example = "1")
            @PathVariable Long id) {
        LectureResponse response = lectureService.getLectureById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/module/{moduleId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    @Operation(
            summary = "Получить лекции по модулю",
            description = "Возвращает все лекции в указанном модуле"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список лекций получен"),
            @ApiResponse(responseCode = "404", description = "Модуль не найден")
    })
    public ResponseEntity<Page<LectureResponse>> getLecturesByModule(
            @Parameter(description = "ID модуля", example = "1")
            @PathVariable Long moduleId,
            Pageable pageable) {
        Page<LectureResponse> page = lectureService.getLecturesByModule(moduleId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    @Operation(
            summary = "Поиск лекций по названию",
            description = "Ищет лекции по вхождению текста в название"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Результаты поиска получены")
    })
    public ResponseEntity<Page<LectureResponse>> searchLectures(
            @Parameter(description = "Поисковый запрос", example = "грамматика")
            @RequestParam String title,
            Pageable pageable) {
        Page<LectureResponse> page = lectureService.searchByTitle(title, pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(
            summary = "Обновить лекцию",
            description = "Обновляет содержимое лекции"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лекция обновлена",
                    content = @Content(schema = @Schema(implementation = LectureResponse.class))),
            @ApiResponse(responseCode = "404", description = "Лекция не найдена")
    })
    public ResponseEntity<LectureResponse> updateLecture(
            @Parameter(description = "ID лекции", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody LectureCreateRequest request) {
        LectureResponse response = lectureService.updateLecture(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(
            summary = "Удалить лекцию",
            description = "Удаляет лекцию"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Лекция успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Лекция не найдена")
    })
    public ResponseEntity<Void> deleteLecture(
            @Parameter(description = "ID лекции для удаления", example = "1")
            @PathVariable Long id) {
        lectureService.deleteLecture(id);
        return ResponseEntity.noContent().build();
    }
}