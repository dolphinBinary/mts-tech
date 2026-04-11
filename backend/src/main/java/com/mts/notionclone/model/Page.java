package com.mts.notionclone.model;

import java.time.Instant;

public record Page(
        String id,
        String title,
        String content,
        long version,
        Instant updatedAt
) {
}
