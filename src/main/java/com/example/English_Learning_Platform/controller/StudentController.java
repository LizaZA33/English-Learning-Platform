package com.example.English_Learning_Platform.controller;

import com.example.English_Learning_Platform.model.dto.request.StudentProfileRequest;
import com.example.English_Learning_Platform.model.dto.response.StudentResponse;
import com.example.English_Learning_Platform.service.StudentService;
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
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Students", description = "API для управления профилями студентов")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Создать профиль студента",
            description = "Создает профиль студента для текущего пользователя. После создания пользователь получает роль STUDENT"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Профиль студента создан",
                    content = @Content(schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или профиль уже существует"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    public ResponseEntity<StudentResponse> createStudentProfile(@Valid @RequestBody StudentProfileRequest request) {
        StudentResponse response = studentService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(
            summary = "Получить список всех студентов",
            description = "Возвращает список студентов с возможностью поиска по имени, фамилии или отчеству"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список студентов получен"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<Page<StudentResponse>> getAllStudents(
            @Parameter(description = "Поиск по имени, фамилии или отчеству", example = "Иванов")
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<StudentResponse> page = studentService.findAllStudents(search, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(
            summary = "Получить студента по ID",
            description = "Возвращает полную информацию о студенте, включая группы и прогресс"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Студент найден",
                    content = @Content(schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Студент не найден")
    })
    public ResponseEntity<StudentResponse> getStudentById(
            @Parameter(description = "ID студента", example = "1")
            @PathVariable Long id) {
        StudentResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(response);
    }
}