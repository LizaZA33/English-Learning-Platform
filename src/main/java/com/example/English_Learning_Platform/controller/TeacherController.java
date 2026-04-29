package com.example.English_Learning_Platform.controller;

import com.example.English_Learning_Platform.model.dto.request.TeacherProfileRequest;
import com.example.English_Learning_Platform.model.dto.response.TeacherResponse;
import com.example.English_Learning_Platform.service.TeacherService;
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
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Teachers", description = "API для управления профилями учителей")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping("/profile")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and !hasRole('TEACHER'))")
    @Operation(
            summary = "Создать профиль учителя",
            description = "Создает профиль учителя для текущего пользователя. После создания пользователь получает роль TEACHER"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Профиль учителя создан",
                    content = @Content(schema = @Schema(implementation = TeacherResponse.class))),
            @ApiResponse(responseCode = "400", description = "Профиль уже существует"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<TeacherResponse> createTeacherProfile(@Valid @RequestBody TeacherProfileRequest request) {
        TeacherResponse response = teacherService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(
            summary = "Получить список всех учителей",
            description = "Возвращает список учителей с возможностью поиска по имени, фамилии или отчеству"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список учителей получен")
    })
    public ResponseEntity<Page<TeacherResponse>> getAllTeachers(
            @Parameter(description = "Поиск по имени, фамилии или отчеству", example = "Иванов")
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<TeacherResponse> page = teacherService.findAllTeachers(search, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(
            summary = "Получить учителя по ID",
            description = "Возвращает информацию об учителе и его группах"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Учитель найден",
                    content = @Content(schema = @Schema(implementation = TeacherResponse.class))),
            @ApiResponse(responseCode = "404", description = "Учитель не найден")
    })
    public ResponseEntity<TeacherResponse> getTeacherById(
            @Parameter(description = "ID учителя", example = "1")
            @PathVariable Long id) {
        TeacherResponse response = teacherService.getTeacherById(id);
        return ResponseEntity.ok(response);
    }
}