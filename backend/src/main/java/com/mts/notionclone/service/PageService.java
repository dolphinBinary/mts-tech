package com.mts.notionclone.service;

import com.mts.notionclone.model.Page;
import com.mts.notionclone.repo.PageRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PageService {
    private static final Pattern PAGE_LINK_PATTERN = Pattern.compile("\\[\\[page:([a-zA-Z0-9-_]+)\\]\\]");
    private final PageRepository pageRepository;

    public PageService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public Page getPage(String pageId) {
        return pageRepository.getOrCreate(pageId);
    }

    public Page savePage(String pageId, String title, String content, long version) {
        return pageRepository.save(pageId, title, content, version);
    }

    public List<String> backlinks(String pageId) {
        List<String> backlinks = new ArrayList<>();
        String marker = "[[page:" + pageId + "]]";
        for (Page page : pageRepository.all()) {
            if (!page.id().equals(pageId) && page.content().contains(marker)) {
                backlinks.add(page.id());
            }
        }
        return backlinks;
    }

    public List<String> outLinks(String content) {
        Matcher matcher = PAGE_LINK_PATTERN.matcher(content == null ? "" : content);
        List<String> ids = new ArrayList<>();
        while (matcher.find()) {
            ids.add(matcher.group(1));
        }
        return ids;
    }
}
