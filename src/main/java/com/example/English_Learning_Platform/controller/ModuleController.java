package com.example.English_Learning_Platform.controller;

import com.example.English_Learning_Platform.model.dto.request.ModuleCreateRequest;
import com.example.English_Learning_Platform.model.dto.response.ModuleResponse;
import com.example.English_Learning_Platform.service.ModuleService;
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
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Modules", description = "API для управления учебными модулями")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(
            summary = "Создать модуль",
            description = "Создает новый учебный модуль. Доступно только для учителей"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Модуль создан",
                    content = @Content(schema = @Schema(implementation = ModuleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
    public ResponseEntity<ModuleResponse> createModule(@Valid @RequestBody ModuleCreateRequest request) {
        ModuleResponse response = moduleService.createModule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    @Operation(
            summary = "Получить список всех модулей",
            description = "Возвращает список всех учебных модулей"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список модулей получен")
    })
    public ResponseEntity<Page<ModuleResponse>> getAllModules(Pageable pageable) {
        Page<ModuleResponse> page = moduleService.getAllModules(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    @Operation(
            summary = "Получить модуль по ID",
            description = "Возвращает информацию о модуле"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Модуль найден",
                    content = @Content(schema = @Schema(implementation = ModuleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Модуль не найден")
    })
    public ResponseEntity<ModuleResponse> getModuleById(
            @Parameter(description = "ID модуля", example = "1")
            @PathVariable Long id) {
        ModuleResponse response = moduleService.getModuleById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    @Operation(
            summary = "Поиск модулей по названию",
            description = "Ищет модули по вхождению текста в название"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Результаты поиска получены")
    })
    public ResponseEntity<Page<ModuleResponse>> searchModules(
            @Parameter(description = "Поисковый запрос", example = "Базовый")
            @RequestParam String name,
            Pageable pageable) {
        Page<ModuleResponse> page = moduleService.searchByName(name, pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(
            summary = "Удалить модуль",
            description = "Удаляет модуль"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Модуль успешно удален"),
            @ApiResponse(responseCode = "404", description = "Модуль не найден")
    })
    public ResponseEntity<Void> deleteModule(
            @Parameter(description = "ID модуля для удаления", example = "1")
            @PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }
}