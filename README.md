# Tic-Tac-Toe
Backend-приложение для игры в "Крестики-нолики"с поддержкой игры против компьютера и мультиплеера.
Реализовано на Java с использованием Spring Boot, Spring Security, Spring Data JPA и PostgreSQL.

## 📋 О проекте

REST API, позволяющее пользователям регистрироваться, создавать игры против компьютера или другого игрока, совершать 
ходы и отслеживать состояние игры. Компьютер использует алгоритм Minimax для выбора оптимального хода. Приложение 
поддерживает множество параллельных игр, валидацию ходов и определение победителя.

### Ключевые особенности

- 👥 Регистрация и аутентификация пользователей (Basic Auth)
- 🤖 Игра против компьютера с алгоритмом Minimax
- 👫 Мультиплеер: игра с другим игроком (очередь ходов)
- 🎮 Поддержка множества одновременных игр
- 🔒 Валидация всех ходов согласно правилам игры
- 📊 Определение победителя и состояния игры
- 🛡️ Централизованная обработка ошибок
- 🗄️ Хранение данных в PostgreSQL с использованием JPA
- 🏗️ Чистая архитектура с разделением ответственности

## 🏗️ Архитектура и структура проекта

Проект реализован с использованием чистой архитектуры и разделен на следующие слои:
```
src/main/java/com/example/
├── TicTacToeApplication.java          # Точка входа
├── domain/                            # Доменный слой
│   ├── model/                         # Доменные модели
│   │   ├── CurrentGame.java
│   │   ├── GameField.java
│   │   ├── GameStatus.java
│   │   ├── GameType.java
│   │   ├── Move.java
│   │   ├── RegistrationData.java
│   │   └── User.java
│   ├── service/                       # Интерфейсы сервисов и их реализация
│   │   ├── AuthService.java
│   │   ├── AuthServiceImpl.java
│   │   ├── CustomUserDetailsService.java
│   │   ├── GameService.java
│   │   ├── GameServiceImpl.java
│   │   ├── UserService.java
│   │   └── UserServiceImpl.java
│   └── exception/                     # Исключения
│       ├── BadCredentialsException.java
│       ├── GameNotFoundException.java
│       ├── GameNotJoinableException.java
│       ├── GameOverException.java
│       ├── InvalidMoveException.java
│       ├── UserAlreadyExistsException.java
│       └── UserDoesntExistsException.java
├── datasource/                        # Слой данных
│   ├── model/                         # JPA-сущности
│   │   ├── DSCurrentGame.java
│   │   ├── DSGameField.java
│   │   └── DSUser.java
│   ├── repository/                    # Репозитории
│   │   ├── CurrentGameDatRepository.java
│   │   ├── GameRepository.java
│   │   └── UserRepository.java
│   ├── converter                      # Конвертер для поля
│   │   └── FieldConverter.java
│   └── mapper/                        # Мапперы между доменом и JPA
│       └── MapperDomainDatasource.java
├── web/                               # Веб-слой
│   ├── controller/                    # Контроллеры
│   │   ├── AuthController.java
│   │   ├── GameRepository.java
│   │   └── UserController.java
│   ├── model/                         # DTO для API
│   │   ├── AuthResponse.java
│   │   ├── ErrorResponse.java
│   │   ├── SecurityUserDetails.java
│   │   ├── SignUpRequest.java
│   │   ├── WebCurrentGame.java
│   │   ├── WebGameField.java
│   │   └── WebUser.java
│   ├── mapper/                        # Мапперы для API
│   │   └── MapperDomainWeb.java
│   ├── filter/                        # Фильтр для аутентификации
│   │   └── AuthFilter.java
│   └── advice/                        # Обработка исключений
│       └── GlobalExceptionHandler.java
└── di/                                # Конфигурация DI
    ├── AppConfig.java
    └── SecurityConfig.java
```

## 🛠️ Технологический стек

- **Java** 18+
- **Spring Boot** 4.0.1
- **Spring Web MVC**
- **Spring Security** (Basic Auth)
- **Spring Data JPA** (Hibernate)
- **PostgreSQL**
- **Gradle** 9.2.1 (система сборки)

## 📦 Требования

- JDK 18 или выше
- PostgresSQL
- Gradle 9.2.1 (используется wrapper)
- Интернет-соединение для загрузки зависимостей

## 🔧 Конфигурация

### Настройка базы данных

Создайте базу данных и укажите параметры подключения в application.properties:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/Tic-Tac-Toe
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```
## 📡 API Документация

### Базовый URL
```
http://localhost:8080
```

### Аутентификация

Большинство эндпоинтов требует базовой аутентификации (Basic Auth). Заголовок `Authorization: Basic <base64(login:password)>`

#### Регистрация нового пользователя

Запрос:

```
POST /auth/register
```

Тело запроса (JSON):
```
{
  "login": "testuser",
  "password": "testpass"
}
```

Ответ (201 Created):
```
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2025-03-18T12:00:00"
}
```
Другие коды состояния:
- 400 Bad Resuest - пустой логин или пароль
- 409 Conflict - пользователь с таким логином уже существует

#### Аутентификация через Basic Auth. 
Заголовок Authorization должен содержать учётные данные.

Запрос:

```
POST /auth/authenticate
```

Ответ (200 OK):
```
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2025-03-18T12:00:00"
}
```
Другие коды состояния:
- 401 Unauthorized - неверный логин или пароль

Все остальные запросы требует аутентификацию.
### Игры
Используют модель ответа WebCurrentGame
```
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "field": {
    "field": [
      [1, 0, 0],
      [0, 2, 0],
      [0, 0, 0]
    ]
  },
  "type": "VS_COMPUTER",
  "status": "TURN_PLAYER_X",
  "playerXId": "550e8400-e29b-41d4-a716-446655440000",
  "playerOId": null
}
```
- **id** - id игры
- **field** - игровое поле 3x3 (0 - пусто, 1 - X, 2 - O)
- **type** - `VS_COMPUTER` или `VS_PLAYER`
- **status** - `WAITING`, `TURN_PLAYER_X`, `TURN_PLAYER_O`, `WIN_PLAYER_X`, `WIN_PLAYER_O`, `DRAW`
- playerXId - UUID игрока за X
- playerOId - UUID игрока за O(может быть null, если игрока ожидает второго игрока или если игра против компьютера)

#### Создание новой игры

Запрос:

```
POST /game
```
Параметр запроса (query): 
- gameType (необязательный) - тип игры: `VS_COMPUTER` (по умолчанию) или `VS_PLAYER`

Ответ: объект `WebCurrentGame` (см. выше)

Коды состояния:
- 201 CREATED - Игра успешно создана

#### Получение состояния игры

Запрос:
```
GET /game/{gameId}
```
Параметры:
- gameId(UUID) - идентификатор игры

Ответ: объект `WebCurrentGame` (см. выше)

Коды состояния:
- 200 OK - Игра успешно найдена
- 404 Not Found - игра не найдена

#### Получение списка доступных для присоединения игр

Возвращает игры со статусом WAITING (ожидание второго игрока) и игры,
в которых пользователь уже участвует (незавершённые).

```
GET /game/available
```

Ответ: массив объектов `WebCurrentGame` (см. выше)

Коды состояния:
- 200 OK - Игры успешно найдены

#### Присоединение к игре

```
GET /game/{gameId}/join
```
Параметры:
- gameId(UUID) - идентификатор игры

Ответ: обновленный объект `WebCurrentGame` (см. выше)

Коды состояния:
- 200 OK - Успешно присоединился к игре
- 400 Bad Request - игра уже начата, или это игра с компьютером, или пользователь уже в игре
- 404 Not Found - игра не найдена


#### Сделать ход

Запрос:
```
POST /game/{gameId}
Content-Type: application/json
```
Параметры:
- gameId(UUID) - идентификатор игры

Тело запроса:
```
{
  "field": [
    [1, 0, 0],
    [0, 0, 0],
    [0, 0, 0]
  ]
}
```

Ответ: обновленный объект `WebCurrentGame` (см. выше)

Коды состояния:
- 200 OK - Игра успешно обновлена
- 400 Bad Request - некорректный ход
- 404 Not Found - игра не найдена
- 409 Conflict - игра завершена

### Пользователи

#### Получение информации о пользователе

```
GET /user/{userId}
```
Параметры:
- userId(UUID) - идентификатор пользователя
Ответ:
```
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "login": "player1"
}
```

Коды состояния:
- 200 OK - Пользователь успешно найден
- 404 Not Found - Пользователь не найден

## 🎮 Правила игры

### Представление поля

Поле представляется как матрица 3x3:

- 0 - пустая клетка
- 1 - крестик (игрок)
- 2 - нолик (компьютер)

### Порядок ходов
1. Игрок (крестики) ходит первым
2. Компьютер(или второй игрок) (нолики) отвечает
3. Ход считается валидным, если:
- Игра не завершена
- Изменена только одна клетка
- Клетка была пустой (0) и стала крестиком (1)

### Определение победителя
- Игра завершается при:
  - Три одинаковых символа в ряд (горизонталь, вертикаль, диагональ)
  - Заполнении всех клеток поля (ничья)

## ⚙️ Конфигурация

### Сборка (build.gradle.kts)
```
plugins {
    id("java")
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
}
```
### Порты
По умолчанию приложение использует порт 8080. Для изменения порта создайте/измените файл application.properties:
```
server.port=9090
```

## 🧪 Алгоритм ИИ
Компьютер использует алгоритм Minimax с эвристической оценкой:
- Победа компьютера: +20 - глубина
- Победа игрока: -20 + глубина
- Ничья: глубина (чем быстрее ничья, тем лучше)

Алгоритм анализирует все возможные ходы и выбирает оптимальный, обеспечивая:
- Победу при возможности
- Предотвращение победы игрока
- Оптимальную стратегию в нейтральных ситуациях

## 🚨 Обработка ошибок
Приложение возвращает структурированные сообщения об ошибках:
```
{
  "error": "Invalid Move",
  "message": "Field must be 3x3",
  "timestamp": "2024-01-15T10:30:00"
}
```
### Возможные ошибки:
- Invalid Move (400) - некорректный ход
- Game Not Joinable (400) - Невозможно присоединиться к игре
- Unauthorized (401) - Неверные учётные данные или их отсутствие
- Game Not Found (404) - Игра не существует
- User Not Found (404) - Пользователь не существует
- Game Over (409) - Попытка хода в завершенную игру
- User Already Exists (409) - Пользователь с таким логином уже есть
- Internal Server Error (500) - Внутренняя ошибка сервера

## 📝 Примечания
- Все идентификаторы — UUID
- Хранилище: используется база данных PostgreSQL
- При первом запуске таблицы создаются автоматически (ddl-auto=update)
- Безопасность: Для аутентификации используется Basic Auth
- Производительность: Алгоритм Minimax работает оптимально для поля 3x3. Для больших полей потребуется оптимизация.