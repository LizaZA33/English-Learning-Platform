# English-Learning-Platform

### Описание проекта

**English Learning Platform** — это полноценная веб-платформа для изучения английского языка с использованием системы интервальных повторений. В отличие от десктопных решений, это современное веб-приложение на базе Spring Boot, которое поддерживает ролевую модель пользователей и предоставляет возможности для социального обучения. Платформа предназначена для студентов, преподавателей и администраторов, предлагая каждому уникальный набор функций для эффективного изучения и преподавания английской лексики.

**Ключевые особенности:**
-  **Три роли пользователей** (STUDENT, TEACHER, ADMIN) с разными правами доступа
-  **Система классов** для группового обучения
-  **Расширенная аналитика прогресса** для учителей
-  **JWT-аутентификация** и безопасное хранение данных
-  **Контейнеризация** через Docker
-  **REST API** для потенциального мобильного приложения
-  **Алгоритм интервальных повторений** (Spaced Repetition System)
- **Автоматическая подборка карточек** для изучения

## Установка и запуск

### Предварительные требования

- **Java Development Kit (JDK) 17+**
- **Apache Maven 3.6+**
- **Docker 20.10+ и Docker Compose 2.0+** (рекомендуемый способ)
- **PostgreSQL 15+** (если запускаете без Docker)
- **Git**

### Способ 1: Запуск через Docker Compose (рекомендуется)

```bash
# 1. Клонируйте репозиторий
git clone https://github.com/LizaZA23/english-learning-platform.git
cd english-learning-platform

# 2. Запустите приложение одной командой
docker-compose up -d

# 3. Приложение будет доступно по адресу:
#    - Веб-приложение: http://localhost:8080
#    - Swagger UI: http://localhost:8080/swagger-ui.html
#    - PostgreSQL: localhost:5432
```

### Способ 2: Локальная установка

```bash
# 1. Клонируйте репозиторий
git clone https://github.com/LizaZA23/english-learning-platform.git
cd english-learning-platform

# 2. Настройте базу данных PostgreSQL
createdb english_learning_db

# 3. Настройте конфигурацию
cp src/main/resources/application.example.yml src/main/resources/application.yml
# Отредактируйте application.yml с вашими данными БД

# 4. Соберите приложение
mvn clean install

# 5. Запустите приложение
java -jar target/english-learning-platform-1.0.0.jar

# Или через Maven:
mvn spring-boot:run
```

### Способ 3: Запуск для разработки

```bash
# 1. Клонируйте и настройте как в способе 2
# 2. Используйте профиль разработки
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 3. Для доступа к H2 консоли (в режиме разработки):
#    http://localhost:8080/h2-console
#    JDBC URL: jdbc:h2:mem:englishdb
#    Username: sa
#    Password: password
```

## Роли пользователей и их возможности

### STUDENT (Студент)
- **Создание личных наборов** карточек
- **Изучение собственных карточек** с алгоритмом интервальных повторений
- **Вступление в классы** по пригласительному коду
- **Просмотр личного прогресса** и статистики
- **Изучение назначенных наборов** от учителя

### TEACHER (Учитель)
- **Все возможности STUDENT**
- **Создание публичных наборов** карточек
- **Создание классов** и управление студентами
- **Назначение наборов** для изучения всему классу
- **Мониторинг прогресса** студентов
- **Просмотр детальной статистики** по классам

### ADMIN (Администратор)
- **Все возможности TEACHER**
- **Управление пользователями** (назначение ролей, блокировка)
- **Удаление любых наборов** и классов
- **Доступ к общей статистике** платформы
- **Просмотр системных логов**

## Примеры использования API

### Аутентификация
```bash
# Регистрация нового пользователя
POST /api/auth/register
{
    "username": "student123",
    "email": "student@example.com",
    "password": "SecurePass123",
    "role": "STUDENT"
}

# Вход в систему
POST /api/auth/login
{
    "username": "student123",
    "password": "SecurePass123"
}
# Ответ содержит JWT токен
```

### Работа с наборами карточек
```bash
# Создание набора (TEACHER может создавать публичные)
POST /api/sets
Authorization: Bearer <your-jwt-token>
{
    "title": "Основные глаголы",
    "description": "Базовые английские глаголы",
    "isPublic": false
}

# Получение карточек для изучения
GET /api/study/session?setId=1
Authorization: Bearer <your-jwt-token>

# Отправка ответа на карточку
POST /api/study/answer
Authorization: Bearer <your-jwt-token>
{
    "cardId": 123,
    "remembered": true,
    "sessionId": "session-uuid"
}
```

### Управление классами (для TEACHER)
```bash
# Создание класса
POST /api/classrooms
Authorization: Bearer <your-jwt-token>
{
    "name": "Английский для начинающих",
    "description": "Группа для изучения базовой лексики"
}

# Получение прогресса студентов в классе
GET /api/classrooms/1/progress
Authorization: Bearer <your-jwt-token>
```

## Архитектура проекта

### Стек технологий
| Категория | Технологии | Назначение |
|-----------|------------|------------|
| **Бэкенд** | Spring Boot 3.x, Java 17 | Основной фреймворк |
| **База данных** | PostgreSQL 15, Liquibase | Хранение данных и миграции |
| **Безопасность** | Spring Security, JWT | Аутентификация и авторизация |
| **Документация** | OpenAPI 3, Swagger UI | Документирование API |
| **Контейнеризация** | Docker, Docker Compose | Развертывание |
| **Тестирование** | JUnit 5, Mockito, Testcontainers | Unit и интеграционные тесты |
| **Валидация** | Bean Validation 3.0 | Валидация данных |
| **Маппинг** | MapStruct | Преобразование DTO ↔ Entity |
| **Логирование** | SLF4J, Logback | Логирование приложения |

### Структура проекта
```
src/main/java/com/englishlearning/
├── config/           # Конфигурационные классы
├── controller/       # REST контроллеры
├── dto/             # Data Transfer Objects
├── model/           # JPA сущности
├── repository/      # Spring Data JPA репозитории
├── service/         # Бизнес-логика
├── security/        # Конфигурация безопасности
├── exception/       # Обработка исключений
├── util/            # Утилитарные классы
└── mapper/          # Мапперы для DTO
```

### Диаграмма сущностей
```
┌───────────┐     ┌──────────────┐     ┌─────────────┐
│   User    │◄────┤  Classroom   ├────►│ FlashcardSet│
├───────────┤     ├──────────────┤     ├─────────────┤
│ id        │     │ id           │     │ id          │
│ username  │     │ name         │     │ title       │
│ email     │     │ inviteCode   │     │ isPublic    │
│ role      │     │ teacher_id   │     │ owner_id    │
│ ...       │     │ ...          │     │ ...         │
└───────────┘     └──────────────┘     └─────────────┘
      │                   │                     │
      │            @ManyToMany                  │
      └───────────────────┘                     │
                                                │ @OneToMany
                                          ┌──────────┐
                                          │ Flashcard│
                                          ├──────────┤
                                          │ id       │
                                          │ term     │
                                          │ definition│
                                          │ difficulty│
                                          │ ...      │
                                          └──────────┘
```

## Тестирование

### Запуск тестов
```bash
# Запуск всех тестов
mvn test

# Запуск unit тестов
mvn test -Dtest="*UnitTest"

# Запуск integration тестов
mvn test -Dtest="*IntegrationTest"

# Запуск с измерением покрытия кода
mvn clean test jacoco:report
# Отчет будет доступен в target/site/jacoco/index.html
```

### Тестовые пользователи (при запуске с тестовыми данными)
| Роль | Username | Пароль | Возможности |
|------|----------|---------|-------------|
| STUDENT | student | student123 | Личные наборы, вступление в классы |
| TEACHER | teacher | teacher123 | Публичные наборы, управление классами |
| ADMIN | admin | admin123 | Управление пользователями, вся статистика |

## Миграции базы данных

Проект использует Liquibase для управления миграциями:

```bash
# Просмотр состояния миграций
mvn liquibase:status

# Применение миграций (автоматически при запуске)
mvn liquibase:update

# Создание новой миграции
mvn liquibase:diff
```

## Docker развертывание

### Docker Compose файл
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: english_learning_db
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: secure_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
  
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_URL: jdbc:postgresql://postgres:5432/english_learning_db
    depends_on:
      - postgres
    restart: unless-stopped

volumes:
  postgres_data:
```

### Docker команды
```bash
# Сборка и запуск
docker-compose up -d --build

# Просмотр логов
docker-compose logs -f app

# Остановка
docker-compose down

# Остановка с удалением volumes
docker-compose down -v
```

## Документация API

После запуска приложения доступна автоматически сгенерированная документация:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI спецификация**: http://localhost:8080/v3/api-docs

### Примеры запросов в Postman
В папке `postman/` находится полная коллекция запросов:
- `English-Learning-Platform.postman_collection.json` - все endpoints
- `environment.postman_environment.json` - переменные окружения

## Безопасность

### Реализованные меры безопасности:
1. **JWT аутентификация** с expiry time
2. **BCrypt хеширование** паролей
3. **Ролевая модель доступа** (@PreAuthorize)
4. **Защита от CSRF** (отключена для REST API)
5. **Валидация входных данных** (@Valid)
6. **SQL инъекции** - предотвращены через JPA
7. **CORS настройка** для веб-клиента

### Endpoints безопасности:
- `POST /api/auth/register` - регистрация
- `POST /api/auth/login` - получение JWT токена
- `GET /api/auth/refresh` - обновление токена
- `POST /api/auth/logout` - выход из системы

## Мониторинг и логирование

### Уровни логирования
```yaml
# application.yml
logging:
  level:
    com.englishlearning: DEBUG
    org.springframework.security: INFO
    org.hibernate.SQL: DEBUG # SQL запросы (только для разработки)
```

### Ключевые логируемые события:
- Создание/удаление пользователей
- Попытки входа (успешные/неуспешные)
- Создание классов и наборов
- Действия администраторов

## Производительность

### Оптимизации:
1. **Lazy loading** для связей ManyToMany
2. **Пагинация** для списков
3. **Кэширование** часто используемых данных (опционально)
4. **Индексы** в базе данных на частые запросы
5. **Connection pooling** через HikariCP

## Вклад в проект

### Правила разработки:
1. Создавайте feature branch от `develop`
2. Пишите тесты для новой функциональности
3. Обновляйте документацию
4. Используйте meaningful commit messages
5. Создавайте Pull Request для ревью

### Code Style:
```bash
# Проверка стиля кода
mvn spotless:check

# Форматирование кода
mvn spotless:apply
```

## 📞 Поддержка и обратная связь

### Каналы связи:
- **Email**: tearechard12@gmail.com
- **Telegram**: @ProistoLisa
