package com.example.English_Learning_Platform.controller;

import com.example.English_Learning_Platform.model.dto.request.LessonCreateRequest;
import com.example.English_Learning_Platform.model.dto.response.LessonResponse;
import com.example.English_Learning_Platform.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Lessons", description = "API для управления уроками")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Создать новый урок",
            description = "Создает урок. Любой аутентифицированный пользователь может создать личный урок"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Урок создан",
                    content = @Content(schema = @Schema(implementation = LessonResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные для создания урока",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = LessonCreateRequest.class),
                    examples = @ExampleObject(value = """
                            {
                              "title": "Неправильные глаголы",
                              "description": "Изучение основных неправильных глаголов"
                            }""")
            )
    )
    public ResponseEntity<LessonResponse> createLesson(@Valid @RequestBody LessonCreateRequest request) {
        LessonResponse response = lessonService.createLesson(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить мои личные уроки",
            description = "Возвращает список личных уроков текущего пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список уроков получен")
    })
    public ResponseEntity<Page<LessonResponse>> getMyPersonalLessons(Pageable pageable) {
        Page<LessonResponse> page = lessonService.getPersonalLessons(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить уроки учителя",
            description = "Возвращает список уроков конкретного учителя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список уроков получен"),
            @ApiResponse(responseCode = "404", description = "Учитель не найден")
    })
    public ResponseEntity<Page<LessonResponse>> getTeacherLessons(
            @Parameter(description = "ID учителя", example = "1")
            @PathVariable Long teacherId,
            Pageable pageable) {
        Page<LessonResponse> page = lessonService.getTeacherLessons(teacherId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить урок по ID",
            description = "Возвращает урок с карточками"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Урок найден",
                    content = @Content(schema = @Schema(implementation = LessonResponse.class))),
            @ApiResponse(responseCode = "404", description = "Урок не найден"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к уроку")
    })
    public ResponseEntity<LessonResponse> getLessonById(
            @Parameter(description = "ID урока", example = "1")
            @PathVariable Long id) {
        LessonResponse response = lessonService.getLessonById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@lessonSecurity.canModifyLesson(#id)")
    @Operation(
            summary = "Обновить урок",
            description = "Обновляет информацию об уроке. Доступно владельцу или администратору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Урок обновлен",
                    content = @Content(schema = @Schema(implementation = LessonResponse.class))),
            @ApiResponse(responseCode = "404", description = "Урок не найден"),
            @ApiResponse(responseCode = "403", description = "Нет прав на изменение")
    })
    public ResponseEntity<LessonResponse> updateLesson(
            @Parameter(description = "ID урока", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody LessonCreateRequest request) {
        LessonResponse response = lessonService.updateLesson(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@lessonSecurity.canModifyLesson(#id)")
    @Operation(
            summary = "Удалить урок",
            description = "Удаляет урок. Доступно владельцу или администратору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Урок успешно удален"),
            @ApiResponse(responseCode = "404", description = "Урок не найден"),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление")
    })
    public ResponseEntity<Void> deleteLesson(
            @Parameter(description = "ID урока для удаления", example = "1")
            @PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }
}