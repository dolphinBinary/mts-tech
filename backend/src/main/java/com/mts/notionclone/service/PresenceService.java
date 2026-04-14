package com.mts.notionclone.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {
    private final Map<String, Map<String, Instant>> viewersByPage = new ConcurrentHashMap<>();

    public void heartbeat(String pageId, String userId) {
        viewersByPage.computeIfAbsent(pageId, p -> new ConcurrentHashMap<>()).put(userId, Instant.now());
    }

    public List<String> activeUsers(String pageId) {
        Instant threshold = Instant.now().minusSeconds(20);
        Map<String, Instant> users = viewersByPage.getOrDefault(pageId, new HashMap<>());
        users.entrySet().removeIf(e -> e.getValue().isBefore(threshold));
        return new ArrayList<>(users.keySet());
    }
}
