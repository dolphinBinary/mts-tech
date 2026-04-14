# Mandatory/Extra Feature Checklist

| Feature | Status | Implementation |
|---|---|---|
| Insert existing MWS Table in page body | ✅ | `[[table:table_id]]` -> API `/tables/embed` -> render table block |
| In-line autosave + local cache + sync | ✅ | debounce PUT save + LocalStorage restore |
| Backlinks | ✅ | server parses `[[page:id]]` and returns inbound links |
| Slash-menu + hotkeys | ✅ | open by `/` and `Ctrl+/`, commands shown in menu |
| Collaboration | ✅ | page presence heartbeat (multi-user list, check via 2 tabs) |
| AI helper | ✅ (demo) | `/ии[url]` -> shorten/rewrite stub + limitations shown |
| Versioning | ⚪ planned | DB history table + diff viewer |
| Comments | ⚪ planned | block anchors + threaded API |
| Graph of links | ⚪ planned | out/in links + graph panel |

## AI dependencies and limits
- Current implementation returns deterministic mock response from backend service.
- Production: replace with provider SDK (e.g., OpenAI/enterprise LLM).
- Risks: hallucinations, latency, privacy and prompt injection from external URLs.
