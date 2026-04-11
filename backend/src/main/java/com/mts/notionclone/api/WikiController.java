package com.mts.notionclone.api;

import com.mts.notionclone.model.Page;
import com.mts.notionclone.model.TableEmbed;
import com.mts.notionclone.service.AiActionService;
import com.mts.notionclone.service.PageService;
import com.mts.notionclone.service.PresenceService;
import com.mts.notionclone.service.TableIntegrationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WikiController {

    private final PageService pageService;
    private final TableIntegrationService tableIntegrationService;
    private final PresenceService presenceService;
    private final AiActionService aiActionService;

    public WikiController(PageService pageService,
                          TableIntegrationService tableIntegrationService,
                          PresenceService presenceService,
                          AiActionService aiActionService) {
        this.pageService = pageService;
        this.tableIntegrationService = tableIntegrationService;
        this.presenceService = presenceService;
        this.aiActionService = aiActionService;
    }

    @GetMapping("/pages/{id}")
    public PageDto page(@PathVariable String id) {
        return PageDto.from(pageService.getPage(id), pageService.backlinks(id));
    }

    @PutMapping("/pages/{id}")
    public PageDto save(@PathVariable String id, @RequestBody SavePageRequest request) {
        Page page = pageService.savePage(id, request.title(), request.content(), request.version());
        return PageDto.from(page, pageService.backlinks(id));
    }

    @GetMapping("/pages/{id}/backlinks")
    public List<String> backlinks(@PathVariable String id) {
        return pageService.backlinks(id);
    }

    @PostMapping("/tables/embed")
    public TableEmbed embed(@RequestBody TableEmbedRequest request) {
        return tableIntegrationService.fetchTable(request.tableId());
    }

    @PostMapping("/pages/{id}/presence")
    public List<String> presence(@PathVariable String id, @RequestBody PresenceRequest request) {
        presenceService.heartbeat(id, request.userId());
        return presenceService.activeUsers(id);
    }

    @PostMapping("/ai/action")
    public AiActionResponse aiAction(@RequestBody AiActionRequest request) {
        var result = aiActionService.apply(request.url(), request.action());
        return new AiActionResponse(result.action(), result.result(), "AI-ответ демонстрационный. Нужна LLM-интеграция.");
    }

    public record SavePageRequest(String title, String content, long version) {}

    public record TableEmbedRequest(String tableId) {}

    public record PresenceRequest(String userId) {}

    public record AiActionRequest(String url, String action) {}

    public record AiActionResponse(String action, String result, String limitations) {}

    public record PageDto(String id, String title, String content, long version, List<String> backlinks) {
        static PageDto from(Page page, List<String> backlinks) {
            return new PageDto(page.id(), page.title(), page.content(), page.version(), backlinks);
        }
    }
}
