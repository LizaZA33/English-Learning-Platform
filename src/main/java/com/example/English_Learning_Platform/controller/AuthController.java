package com.example.English_Learning_Platform.controller;

import com.example.English_Learning_Platform.model.dto.request.LoginRequest;
import com.example.English_Learning_Platform.model.dto.request.RegisterRequest;
import com.example.English_Learning_Platform.model.dto.response.JwtResponse;
import com.example.English_Learning_Platform.model.dto.response.UserResponse;
import com.example.English_Learning_Platform.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "API для аутентификации и регистрации пользователей")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает нового пользователя с ролью USER и возвращает JWT токен"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или email уже используется",
                    content = @Content(schema = @Schema(implementation = com.example.English_Learning_Platform.model.dto.response.ErrorResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные для регистрации",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = RegisterRequest.class),
                    examples = @ExampleObject(value = """
                            {
                              "email": "user@example.com",
                              "password": "Password1@",
                              "firstName": "Иван",
                              "lastName": "Иванов"
                            }""")
            )
    )
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Регистрация нового пользователя: {}", request.getEmail());
        JwtResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Вход в систему",
            description = "Аутентифицирует пользователя по email и паролю, возвращает JWT токен"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверный email или пароль",
                    content = @Content(schema = @Schema(implementation = com.example.English_Learning_Platform.model.dto.response.ErrorResponse.class)))
    })
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Вход пользователя: {}", request.getEmail());
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Получить текущего пользователя",
            description = "Возвращает информацию о текущем аутентифицированном пользователе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе получена",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse response = authService.getCurrentUser();
        return ResponseEntity.ok(response);
    }
}