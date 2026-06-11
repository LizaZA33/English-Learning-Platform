package com.example.English_Learning_Platform.controller;

import com.example.English_Learning_Platform.model.dto.request.CourseGroupRequest;
import com.example.English_Learning_Platform.model.dto.response.CourseGroupResponse;
import com.example.English_Learning_Platform.service.CourseGroupService;
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
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Course Groups", description = "API для управления учебными группами")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class CourseGroupController {

    private final CourseGroupService courseGroupService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(
            summary = "Создать учебную группу",
            description = "Создает новую учебную группу. Доступно только для учителей"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Группа создана",
                    content = @Content(schema = @Schema(implementation = CourseGroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "403", description = "Только учитель может создавать группы")
    })
    public ResponseEntity<CourseGroupResponse> createGroup(@Valid @RequestBody CourseGroupRequest request) {
        CourseGroupResponse response = courseGroupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/join")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Присоединиться к группе по коду",
            description = "Позволяет студенту присоединиться к группе по уникальному коду приглашения"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное присоединение к группе",
                    content = @Content(schema = @Schema(implementation = CourseGroupResponse.class))),
            @ApiResponse(responseCode = "404", description = "Группа не найдена"),
            @ApiResponse(responseCode = "400", description = "Пользователь уже в группе")
    })
    public ResponseEntity<CourseGroupResponse> joinGroup(
            @Parameter(description = "Код приглашения группы", example = "a1b2c3d4e5f6")
            @RequestParam String inviteCode) {
        CourseGroupResponse response = courseGroupService.joinGroup(inviteCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(
            summary = "Получить список групп",
            description = "Возвращает список групп с возможностью фильтрации по учителю и поиска по названию"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список групп получен")
    })
    public ResponseEntity<Page<CourseGroupResponse>> getAllGroups(
            @Parameter(description = "ID учителя для фильтрации", example = "1")
            @RequestParam(required = false) Long teacherId,
            @Parameter(description = "Поиск по названию группы", example = "Английский")
            @RequestParam(required = false) String name,
            Pageable pageable) {
        Page<CourseGroupResponse> page = courseGroupService.findAllGroups(teacherId, name, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT', 'ADMIN')")
    @Operation(
            summary = "Получить группу по ID",
            description = "Возвращает детальную информацию о группе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Группа найдена",
                    content = @Content(schema = @Schema(implementation = CourseGroupResponse.class))),
            @ApiResponse(responseCode = "404", description = "Группа не найдена")
    })
    public ResponseEntity<CourseGroupResponse> getGroupById(
            @Parameter(description = "ID группы", example = "1")
            @PathVariable Long id) {
        CourseGroupResponse response = courseGroupService.getGroupById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @Operation(
            summary = "Удалить группу",
            description = "Удаляет группу. Доступно только учителю-владельцу группы или администратору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Группа успешно удалена"),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление"),
            @ApiResponse(responseCode = "404", description = "Группа не найдена")
    })
    public ResponseEntity<Void> deleteGroup(
            @Parameter(description = "ID группы для удаления", example = "1")
            @PathVariable Long id) {
        courseGroupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    @Operation(
            summary = "Получить мои группы",
            description = "Возвращает группы текущего пользователя"
    )
    public ResponseEntity<Page<CourseGroupResponse>> getMyGroups(Pageable pageable) {
        Page<CourseGroupResponse> page = courseGroupService.getMyGroups(pageable);
        return ResponseEntity.ok(page);
    }
}