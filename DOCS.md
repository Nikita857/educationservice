# Education Platform — Документация

## Быстрый старт

```bash
docker compose up -d          # PostgreSQL + MinIO
./gradlew bootRun             # Запуск приложения
```

- **API Docs:** http://localhost:8080/api-client
- **MinIO Console:** http://localhost:9001 (minioadmin / minioadmin123)

---

## Архитектура

### Сущности

```
Course (курс)
├── Module (модуль) — orderIndex
│   └── Lesson (урок) — orderIndex
│       └── Test (опционально)
├── Test (тест модуля, опционально)
└── Test (тест курса, опционально)
```

### Роли

| Роль         | Права                               |
| ------------ | ----------------------------------- |
| `ADMIN`      | Полный доступ                       |
| `INSTRUCTOR` | Создание/редактирование курсов      |
| `MANAGER`    | Запрос доступа к курсам для команды |
| `EMPLOYEE`   | Прохождение курсов                  |

---

## Уроки

### Типы уроков (LessonType)

| Тип        | Описание                       |
| ---------- | ------------------------------ |
| `VIDEO`    | Видеоурок                      |
| `LECTURE`  | Текстовая лекция               |
| `EXTERNAL` | Материал из внешнего источника |

### Способы закрытия (LessonCompletionType)

| Тип          | Описание                                          |
| ------------ | ------------------------------------------------- |
| `TEST`       | Прохождение теста                                 |
| `SCREENSHOT` | Загрузка скриншота (подтверждение внешнего теста) |
| `READ`       | Отметка о прочтении (без проверки)                |

---

## Тесты (XOR связи)

Тест может быть привязан **только к одной** сущности:

```java
// В БД: CHECK constraint гарантирует XOR
lesson_id  OR  module_id  OR  course_id  // только одно заполнено
```

### API тестов

| Эндпоинт                          | Описание                               |
| --------------------------------- | -------------------------------------- |
| `GET /lessons/{id}/test`          | Получить тест урока                    |
| `GET /modules/{id}/test`          | Получить тест модуля                   |
| `GET /courses/{id}/test`          | Получить тест курса                    |
| `GET /tests/{id}/questions`       | Вопросы теста (без правильных ответов) |
| `POST /tests/{id}/start`          | Начать попытку                         |
| `POST /test-attempts/{id}/submit` | Отправить ответы                       |
| `GET /test-attempts/{id}`         | Результат попытки                      |

### Логика подсчета баллов

- Вопрос считается правильным если **все** правильные ответы выбраны и **нет** неправильных
- `score = (earnedPoints * 100) / totalPoints`
- `isPassed = score >= passingScore`

---

## Файлы (MinIO)

### Бакеты

| Бакет                  | Конфигурация              | Типы файлов |
| ---------------------- | ------------------------- | ----------- |
| `education-video`      | `MINIO_VIDEO_BUCKET`      | video/\*    |
| `education-image`      | `MINIO_IMAGE_BUCKET`      | image/\*    |
| `education-attachment` | `MINIO_ATTACHMENT_BUCKET` | остальное   |

### Формат пути

```
bucket:objectName
```

Пример: `education-video:lessons/550e8400-e29b.mp4`

### API файлов

| Эндпоинт                        | Описание                                        |
| ------------------------------- | ----------------------------------------------- |
| `POST /files/upload`            | Загрузка (автоопределение бакета)               |
| `POST /files/upload/{fileType}` | Загрузка с явным типом (VIDEO/IMAGE/ATTACHMENT) |
| `POST /files/urls`              | Batch: presigned URL для списка файлов          |
| `GET /files/url?path=...`       | Presigned URL для одного файла                  |
| `DELETE /files?path=...`        | Удаление файла                                  |

### Пример использования

**Загрузка:**

```bash
POST /api/v1/files/upload?folder=lessons
Content-Type: multipart/form-data
file: [video.mp4]

# Ответ: "education-video:lessons/550e8400-e29b.mp4"
```

**Получение URL (фронтенд при открытии урока):**

```json
POST /api/v1/files/urls
{
  "paths": [
    "education-video:lessons/abc.mp4",
    "education-image:materials/img1.jpg"
  ]
}

// Ответ:
{
  "urls": {
    "education-video:lessons/abc.mp4": "http://minio:9000/...?X-Amz-...",
    "education-image:materials/img1.jpg": "http://minio:9000/...?X-Amz-..."
  }
}
```

---

## Переменные окружения

```bash
# База данных
DB_URL=jdbc:postgresql://localhost:5432/education
DB_USERNAME=postgres
DB_PASSWORD=123456789

# JWT
JWT_SECRET=<min 256 bit>

# MinIO
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin123
MINIO_VIDEO_BUCKET=education-video
MINIO_IMAGE_BUCKET=education-image
MINIO_ATTACHMENT_BUCKET=education-attachment

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

---

## Миграции БД

| Версия | Описание                                                                   |
| ------ | -------------------------------------------------------------------------- |
| V1     | Базовые таблицы (users, courses, modules, lessons, tests, questions, etc.) |
| V2     | Система доступа к курсам (course_access, course_access_requests)           |
| V3     | XOR связи Test с lesson/module/course                                      |
| V4     | Типы уроков (lesson_type, completion_type, external_url)                   |

---

## Swagger

После запуска доступен по адресу: http://localhost:8080/api-client
