# MWS Wiki Editor Module (React + Java)

Решение под кейс True Tech Hack 2026: вики-редактор, где страница = рабочий узел знаний с таблицами, связями и автосинхронизацией.

## Что реализовано (обязательный контур)

- **Вставка существующей таблицы MWS Tables в тело страницы**
  - Синтаксис: `[[table:table_id]]`
  - В UI появляется кнопка вставки, затем рендерится табличный блок in-line.
- **In-line автосохранение + локальный кэш + синхронизация с бэкендом**
  - Автосейв по debounce (700ms)
  - Локальный кэш в `localStorage`
  - Версионирование страницы (`version`) на API.
- **Backlinks**
  - Ссылки между страницами: `[[page:some_page]]`
  - Сервер возвращает список входящих ссылок на текущую страницу.
- **Slash-menu + горячие клавиши**
  - Открытие меню по `/` и `Ctrl+/`.
- **Совместная работа в проверяемом виде**
  - Presence heartbeat: список активных пользователей страницы (можно проверить через несколько вкладок).

## Дополнительно

- `/ии[https://url]` + выпадашка действий: **Сократить / Переписать**.
- Вывод ограничений AI в UI.

## Архитектура

Схема компонентов и интеграций: `docs/component-diagram.md`.

Фича-матрица (обязательные + дополнительные): `docs/feature-checklist.md`.

## API (backend)

- `GET /api/pages/{id}` — получить страницу + backlinks
- `PUT /api/pages/{id}` — автосохранение страницы
- `GET /api/pages/{id}/backlinks` — получить обратные ссылки
- `POST /api/tables/embed` — вставка таблицы `{ "tableId": "..." }`
- `POST /api/pages/{id}/presence` — heartbeat присутствия `{ "userId": "..." }`
- `POST /api/ai/action` — demo AI actions

## Быстрый запуск

### Вариант 1: Docker Compose (одна команда)

```bash
docker compose up --build
```

Frontend: `http://localhost:5173`  
Backend: `http://localhost:8080`

### Вариант 2: локально

Backend:
```bash
cd backend
mvn spring-boot:run
```

Frontend:
```bash
cd frontend
npm install
npm run dev
```

## Примечание по интеграции с MWS Tables API

Сейчас `TableIntegrationService` содержит заглушку данных. Для production нужно:
1. Подключить auth токен пользователя/сервисный ключ.
2. Делать реальный запрос в MWS Tables API по `tableId`.
3. Поддержать пагинацию/фильтры и обработку ошибок API.

## Что подготовить для сдачи (чек)

- [x] Код в открытом репозитории + инструкция запуска
- [x] Компонентная схема и интеграции (`docs/component-diagram.md`)
- [x] Шаблон фич и реализаций (`docs/feature-checklist.md`)
- [ ] Демо-видео
- [x] Презентация (7-мин питч, `docs/pitch-presentation.md`)
