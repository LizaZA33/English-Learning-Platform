# English-Learning-Platform

### Описание проекта

**English Learning Platform** — это полноценная веб-платформа для изучения английского языка с использованием системы интервальных повторений. В отличие от десктопных решений, это современное веб-приложение на базе Spring Boot, которое поддерживает ролевую модель пользователей и предоставляет возможности для социального обучения. Платформа предназначена для студентов, преподавателей и администраторов, предлагая каждому уникальный набор функций для эффективного изучения и преподавания английской лексики.

**Ключевые особенности:**
- Регистрация и аутентификация пользователей с JWT.
- Система флеш-карточек для запоминания слов и выражений.
- Иерархическая структура учебных материалов: Модули -> Лекции -> Уроки -> Карточки.
- Возможность создания личных уроков и публичных учебных групп.
- Присоединение к группам по инвайт-коду.
- Отслеживание прогресса обучения по лекциям и урокам.
- Ролевая модель доступа: Студент, Учитель, Администратор.
- Полностью документированное REST API (Swagger/OpenAPI).

## Технологический стек

| Компонент | Технология |
|-----------|------------|
| Язык программирования | Java 21 |
| Фреймворк | Spring Boot 3.2.0 (Web, Data JPA, Security, Validation, Cache) |
| База данных | PostgreSQL 16 |
| Миграции | Liquibase |
| Безопасность | Spring Security, JWT (JSON Web Token) |
| Маппинг сущностей | MapStruct |
| Документация API | Springdoc OpenAPI (Swagger UI) |
| Сборка | Apache Maven |
| Контейнеризация | Docker, Docker Compose |
| Тестирование | JUnit 5, Mockito, Spring MVC Test (MockMvc), JaCoCo |

## Архитектура и структура проекта

Проект представляет собой классическое монолитное приложение со слоистой архитектурой.

- **Контроллеры (Controller)**: Обрабатывают HTTP-запросы, валидируют входные данные, возвращают DTO.
- **Сервисы (Service)**: Содержат бизнес-логику приложения.
- **Репозитории (Repository)**: Слой доступа к данным на основе Spring Data JPA.
- **Сущности (Entity)**: Модели данных, отображаемые на таблицы в базе данных.
- **DTO (Data Transfer Object)**: Объекты для передачи данных между слоями.
- **Мапперы (Mapper)**: Используют MapStruct для преобразования Entity <-> DTO.
- **Конфигурация (Config)**: Настройки Spring Security, OpenAPI, CORS и JPA.
- **Безопасность (Security)**: Кастомные фильтры, утилиты для работы с JWT, реализация `UserDetailsService`.

Структура каталогов соответствует стандартному Spring Boot проекту:

```
src/
├── main/
│   ├── java/com/example/English_Learning_Platform/
│   │   ├── config/        # Конфигурационные классы
│   │   ├── controller/    # REST контроллеры
│   │   ├── exception/     # Глобальный обработчик исключений
│   │   ├── model/
│   │   │   ├── dto/       # Запросы и ответы (Request/Response)
│   │   │   ├── entity/    # JPA сущности
│   │   │   ├── enums/     # Перечисления (роли)
│   │   ├── repository/    # JPA репозитории
│   │   ├── security/      # Компоненты безопасности (JWT)
│   │   └── service/       # Сервисы
│   └── resources/
│       ├── db/changelog/   # Liquibase миграции
│       └── application.properties / YAML файлы конфигурации
└── test/                  # Unit и интеграционные тесты
```

### Схема базы данных

<img width="974" height="812" alt="Database Schema" src="https://github.com/user-attachments/assets/2164fca0-f24f-4ddd-8802-58e07aa1db85" />

## Установка и запуск

### Предварительные требования

- Java Development Kit (JDK) 21
- Apache Maven 3.6+
- Docker и Docker Compose (рекомендуемый способ)
- Git

### Способ 1: Запуск через Docker Compose (рекомендуемый)

Этот способ автоматически поднимает контейнер с PostgreSQL и само приложение.

```bash
# 1. Клонируйте репозиторий
git clone https://github.com/LizaZA23/english-learning-platform.git
cd english-learning-platform

# 2. Запустите приложение
docker-compose up -d

# 3. Приложение будет доступно по адресу:
#    - API: http://localhost:8080
#    - Swagger UI: http://localhost:8080/swagger-ui.html
```

### Способ 2: Локальный запуск (для разработки)

Для локального запуска вам потребуется установленный PostgreSQL.

1.  Создайте базу данных, например, `english_platform`.
2.  Настройте подключение в файле `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/english_platform
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    ```
3.  Соберите и запустите приложение с помощью Maven:
    ```bash
    mvn clean spring-boot:run
    ```

## Роли пользователей и их возможности

### Студент (STUDENT)

- Создание личных уроков и флеш-карточек.
- Изучение карточек (функционал на стороне клиента, API предоставляет данные).
- Вступление в учебные группы по инвайт-коду.
- Просмотр своего прогресса по лекциям и урокам.
- Изучение материалов, созданных учителями.

### Учитель (TEACHER)

- Все возможности студента.
- Создание публичных уроков и лекций.
- Создание и управление учебными группами.
- Просмотр списка студентов в своих группах.
- Получение статистики по платформе (количество студентов, уроков, групп).

### Администратор (ADMIN)

- Все возможности учителя.
- Управление пользователями: назначение и удаление ролей.
- Удаление любых пользователей.
- Просмотр всей статистики платформы.

## Документация API

Документация API в формате OpenAPI (Swagger) доступна после запуска приложения по адресу:

**Swagger UI:** `http://localhost:8080/swagger-ui.html`

Swagger UI предоставляет интерактивную документацию, где можно просмотреть все доступные эндпоинты, их параметры, примеры запросов и ответов, а также авторизоваться и выполнить тестовые запросы.

### Как получить JWT токен:

Для работы с защищенными эндпоинтами необходимо передавать JWT токен в заголовке `Authorization`.

1.  **Регистрация**:
    ```bash
    POST /api/auth/register
    Content-Type: application/json

    {
        "email": "user@example.com",
        "password": "Password1@",
        "firstName": "Иван",
        "lastName": "Иванов"
    }
    ```
    В ответ вы получите `token`.

2.  **Вход в систему**:
    ```bash
    POST /api/auth/login
    Content-Type: application/json

    {
        "email": "user@example.com",
        "password": "Password1@"
    }
    ```
    В ответ вы получите `token`.

3.  **Использование токена**: Скопируйте полученный токен и в Swagger UI нажмите кнопку "Authorize", вставьте значение как `Bearer <your_token>`. При использовании `curl` или Postman добавьте заголовок:
    ```
    Authorization: Bearer <your_token>
    ```

### Основные группы API Endpoints:

- **Authentication (`/api/auth`)**: Регистрация, вход, получение текущего пользователя.
- **Flashcards (`/api/flashcards`)**: CRUD операции с флеш-карточками.
- **Lessons (`/api/lessons`)**: Управление уроками (личными и публичными).
- **Lectures (`/api/lectures`)**: Управление лекциями (доступно учителям).
- **Course Groups (`/api/groups`)**: Создание групп, присоединение по коду, управление.
- **Progress (`/api/progress`)**: Получение и обновление прогресса пользователя.
- **Statistics (`/api/statistics`)**: Получение общей статистики (для ADMIN и TEACHER).
- **Admin (`/api/users`)**: Управление пользователями (только ADMIN).

Полный список всех эндпоинтов с примерами запросов и ответов доступен в Swagger UI.

## Тестирование

В проекте реализован комплекс unit-тестов для сервисного и контроллерного слоев.

### Покрытие кода (JaCoCo)

- **Покрытие строк (Line coverage):** 73%
- **Покрытие ветвей (Branch coverage):** 54%

<img width="1579" height="502" alt="JaCoCo Report" src="https://github.com/user-attachments/assets/97e4398d-95fa-4bde-a8cb-a3a50cc26dcd" />


### Что покрыто тестами:

- **Сервисный слой**: `AuthService`, `CourseGroupService`, `FlashcardService`, `LessonService`, `ModuleService`, `ProgressService`, `StatisticsService`, `TeacherService`, `UserService`.
- **Контроллерный слой**: Проверка HTTP статусов, валидации входных данных и структуры JSON-ответов с использованием `MockMvc`.
- **Негативные сценарии**: Обработка `ResourceNotFoundException`, `ValidationException`, ошибок доступа.
- **Валидация**: Параметризованные тесты для проверки входных данных.

### Запуск тестов

```bash
mvn test
```

Для генерации отчета JaCoCo:

```bash
mvn clean test
# Отчет будет сгенерирован в target/site/jacoco/index.html
```

## Мониторинг и логирование

- **Логирование**: Настроено с использованием SLF4J (Logback) с детализацией для пакетов `com.example`, `org.springframework.security` и `org.hibernate`. Уровень логирования настраивается в `application.properties`.
- **Health Check**: При запуске в Docker контейнере добавлены healthcheck'и для базы данных и самого приложения.

## Безопасность

- Все пароли пользователей хешируются с помощью `BCryptPasswordEncoder`.
- Аутентификация stateless на основе JWT.
- Доступ к эндпоинтам контролируется с помощью аннотаций `@PreAuthorize` на основе ролей (ADMIN, TEACHER, STUDENT, USER).
- Настроена политика CORS для взаимодействия с фронтендом (по умолчанию разрешен `http://localhost:3000`).
- JWT secret и время жизни токена вынесены в конфигурацию.

## Поддержка и обратная связь

По всем вопросам и предложениям вы можете обращаться:

- **Email**: tearechard12@gmail.com
- **Telegram**: @ProistoLisa
