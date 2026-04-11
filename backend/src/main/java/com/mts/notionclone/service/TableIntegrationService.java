package com.mts.notionclone.service;

import com.mts.notionclone.model.TableEmbed;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TableIntegrationService {

    public TableEmbed fetchTable(String tableId) {
        // Заглушка интеграции: здесь должен быть вызов MWS Tables API с токеном пользователя.
        return new TableEmbed(
                tableId,
                "MWS Table " + tableId,
                List.of("id", "owner", "status"),
                List.of(
                        Map.of("id", 1, "owner", "Анна", "status", "В работе"),
                        Map.of("id", 2, "owner", "Илья", "status", "Готово")
                ),
                "https://tables.mws.ru"
        );
    }
}
