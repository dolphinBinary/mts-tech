package com.mts.notionclone.service;

import com.mts.notionclone.api.EditorController.AiActionResponse;
import org.springframework.stereotype.Service;

@Service
public class AiActionService {

    public AiActionResponse apply(String url, String action) {
        String result;
        if ("shorten".equalsIgnoreCase(action)) {
            result = "Кратко: материал по ссылке " + url + " был сжат в 2-3 тезиса (заглушка).";
        } else if ("rewrite".equalsIgnoreCase(action)) {
            result = "Переписано: содержание ссылки " + url + " преобразовано в более простой стиль (заглушка).";
        } else {
            result = "Неизвестное действие: " + action;
        }

        return new AiActionResponse(action, result);
    }
}
