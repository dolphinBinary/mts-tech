package com.mts.notionclone.repo;

import com.mts.notionclone.model.Page;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PageRepository {
    private final Map<String, Page> pages = new ConcurrentHashMap<>();

    public Page getOrCreate(String id) {
        return pages.computeIfAbsent(id, key -> new Page(id, "Новая страница", "", 0, Instant.now()));
    }

    public Page save(String id, String title, String content, long previousVersion) {
        Page current = getOrCreate(id);
        long nextVersion = Math.max(current.version(), previousVersion) + 1;
        Page updated = new Page(id, title, content, nextVersion, Instant.now());
        pages.put(id, updated);
        return updated;
    }

    public Collection<Page> all() {
        return pages.values();
    }
}
