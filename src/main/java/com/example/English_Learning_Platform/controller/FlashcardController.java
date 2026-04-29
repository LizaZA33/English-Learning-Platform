package com.example.English_Learning_Platform.controller;

import com.example.English_Learning_Platform.model.dto.request.FlashcardRequest;
import com.example.English_Learning_Platform.model.dto.response.FlashcardResponse;
import com.example.English_Learning_Platform.service.FlashcardService;
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
@RequestMapping("/api/flashcards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Flashcards", description = "API для управления флеш-карточками")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class FlashcardController {

    private final FlashcardService flashcardService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'TEACHER')")
    @Operation(
            summary = "Создать флеш-карточку",
            description = "Создает новую карточку в указанном уроке"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Карточка создана",
                    content = @Content(schema = @Schema(implementation = FlashcardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "404", description = "Урок не найден")
    })
    public ResponseEntity<FlashcardResponse> createFlashcard(@Valid @RequestBody FlashcardRequest request) {
        FlashcardResponse response = flashcardService.createFlashcard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("@flashcardSecurity.hasAccessToLesson(#lessonId)")
    @Operation(
            summary = "Получить карточки урока",
            description = "Возвращает все карточки для указанного урока"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список карточек получен"),
            @ApiResponse(responseCode = "404", description = "Урок не найден")
    })
    public ResponseEntity<Page<FlashcardResponse>> getFlashcardsByLesson(
            @Parameter(description = "ID урока", example = "1")
            @PathVariable Long lessonId,
            Pageable pageable) {
        Page<FlashcardResponse> page = flashcardService.getFlashcardsByLesson(lessonId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@flashcardSecurity.hasAccessToFlashcard(#id)")
    @Operation(
            summary = "Получить карточку по ID",
            description = "Возвращает детальную информацию о карточке"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карточка найдена",
                    content = @Content(schema = @Schema(implementation = FlashcardResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карточка не найдена")
    })
    public ResponseEntity<FlashcardResponse> getFlashcardById(
            @Parameter(description = "ID карточки", example = "1")
            @PathVariable Long id) {
        FlashcardResponse response = flashcardService.getFlashcardById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@flashcardSecurity.canModifyFlashcard(#id)")
    @Operation(
            summary = "Обновить карточку",
            description = "Обновляет содержимое карточки"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карточка обновлена",
                    content = @Content(schema = @Schema(implementation = FlashcardResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карточка не найдена"),
            @ApiResponse(responseCode = "403", description = "Нет прав на изменение")
    })
    public ResponseEntity<FlashcardResponse> updateFlashcard(
            @Parameter(description = "ID карточки", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody FlashcardRequest request) {
        FlashcardResponse response = flashcardService.updateFlashcard(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@flashcardSecurity.canModifyFlashcard(#id)")
    @Operation(
            summary = "Удалить карточку",
            description = "Удаляет карточку"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Карточка успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Карточка не найдена"),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление")
    })
    public ResponseEntity<Void> deleteFlashcard(
            @Parameter(description = "ID карточки для удаления", example = "1")
            @PathVariable Long id) {
        flashcardService.deleteFlashcard(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'TEACHER', 'STUDENT', 'ADMIN')")
    @Operation(
            summary = "Поиск карточек по термину",
            description = "Ищет карточки по вхождению текста в термин"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Результаты поиска получены")
    })
    public ResponseEntity<Page<FlashcardResponse>> searchFlashcards(
            @Parameter(description = "Поисковый запрос", example = "hello")
            @RequestParam String term,
            Pageable pageable) {
        Page<FlashcardResponse> page = flashcardService.searchByTerm(term, pageable);
        return ResponseEntity.ok(page);
    }
}