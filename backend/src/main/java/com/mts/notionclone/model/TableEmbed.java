package com.mts.notionclone.model;

import java.util.List;
import java.util.Map;

public record TableEmbed(
        String tableId,
        String tableName,
        List<String> columns,
        List<Map<String, Object>> rows,
        String sourceUrl
) {
}
