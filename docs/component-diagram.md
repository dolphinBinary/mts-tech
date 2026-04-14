# Component & Integration Scheme

```mermaid
flowchart LR
    U[User in MWS UI] --> FE[Wiki Editor React Module]
    FE -->|autosave + sync| BE[Wiki API Java/Spring]
    FE -->|local cache| LS[(LocalStorage)]
    FE -->|table embed by ID| BE
    BE -->|REST| MWS[MWS Tables API]
    FE -->|presence heartbeat| BE
    FE -->|AI actions| BE
    BE -->|optional| LLM[LLM Provider]
```

## Обязательный контур
1. In-line автосохранение: debounce + PUT `/api/pages/{id}`.
2. Локальный кэш: `localStorage` c восстановлением после reload.
3. Backlinks: серверный расчет входящих ссылок `[[page:id]]`.
4. Slash-menu + hotkeys: `/` + `Ctrl+/`.
5. Совместная работа (проверяемый вид): presence heartbeat, список активных пользователей на странице.
6. Вставка таблицы: `[[table:id]]` + запрос на `/api/tables/embed`.
