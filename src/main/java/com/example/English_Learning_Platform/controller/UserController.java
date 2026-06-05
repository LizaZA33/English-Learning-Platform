package com.example.English_Learning_Platform.controller;

import com.example.English_Learning_Platform.model.dto.request.UserUpdateRequest;
import com.example.English_Learning_Platform.model.dto.response.UserResponse;
import com.example.English_Learning_Platform.model.enums.Role;
import com.example.English_Learning_Platform.service.UserService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "API для управления пользователями (только для администраторов)")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Получить список всех пользователей",
            description = "Возвращает список пользователей с возможностью поиска. Доступно только администратору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей получен"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @Parameter(description = "Поиск по email, имени, фамилии или отчеству", example = "Иванов")
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<UserResponse> page = userService.findAllUsers(search, pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Обновить роли пользователя",
            description = "Добавляет или удаляет роль у пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Роли обновлены",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<UserResponse> updateUserRoles(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Роль для добавления/удаления", example = "TEACHER")
            @RequestParam Role role,
            @Parameter(description = "true - добавить роль, false - удалить", example = "true")
            @RequestParam boolean add) {
        UserResponse response = userService.updateUserRole(userId, role, add);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Обновить профиль текущего пользователя",
            description = "Обновляет личные данные текущего пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль обновлен",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateProfile(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя для удаления", example = "1")
            @PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}