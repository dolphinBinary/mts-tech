# WikiLive: схема компонентов редактора и интеграций

```mermaid
flowchart LR
    U[Пользователь] --> FE[WikiLive Editor (React)]

    subgraph FE_BLOCK[Frontend]
        FE --> RT[Rich Text Core]
        FE --> SM[Slash Menu & Hotkeys]
        FE --> AC[Autosave & Local Cache]
        FE --> COL[Presence UI]
        FE --> AIUI[AI Actions UI]
        FE --> TBL[Table Embed Renderer]
    end

    AC -->|debounce PUT /api/pages/:id| PAGE_API[Page API]
    AC -->|local draft| LS[(LocalStorage)]

    RT -->|wiki-ссылки [[page:id]]| PAGE_API
    RT -->|таблицы [[table:id]]| TBL_API[Tables Embed API]

    COL -->|heartbeat POST /api/pages/:id/presence| PRES_API[Presence API]
    AIUI -->|POST /api/ai/action| AI_API[AI Action API]

    PAGE_API --> SVC[PageService]
    TBL_API --> TSVC[TableIntegrationService]
    PRES_API --> PSVC[PresenceService]
    AI_API --> ASVC[AiActionService]

    SVC --> DB[(PostgreSQL / H2)]
    TSVC --> MWS[MWS Tables API]
    ASVC --> LLM[LLM Provider (опционально)]

    SVC --> BL[Backlinks Resolver]
```

## Поток данных (коротко)

1. Пользователь редактирует контент в `Rich Text Core`.
2. Модуль `Autosave & Local Cache` сохраняет черновик локально и синхронизирует страницу через API.
3. Сервер пересчитывает `backlinks` по `[[page:id]]` и отдает их в `GET /api/pages/{id}`.
4. Токены `[[table:id]]` резолвятся через `Tables Embed API` в табличные блоки.
5. `Presence API` поддерживает список активных участников страницы.
6. `AI Action API` выполняет действие над текстом/URL (в демо — stub, в production — внешний LLM).

## Точки интеграции

- **MWS Tables API**: получение структуры и данных таблицы по `tableId`.
- **LLM provider**: rewrite/summarize/structured output для AI-команд.
- **Auth/SSO (рекомендуется)**: передача `userId`, ролей и прав на страницу/таблицу.
- **Observability (рекомендуется)**: логи/метрики автосейва, AI, embed-ошибок.
