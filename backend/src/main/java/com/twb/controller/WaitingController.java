package com.twb.controller;

import com.twb.entity.WaitingItem;
import com.twb.repository.WaitingRepository;
import com.twb.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/** 等待回复 */
@RestController
@RequestMapping("/api")
public class WaitingController {

    private final WaitingRepository repo;
    private final ActivityService activity;

    public WaitingController(WaitingRepository repo, ActivityService activity) {
        this.repo = repo;
        this.activity = activity;
    }

    @GetMapping("/waiting")
    public List<WaitingItem> list() { return repo.findAll(); }

    @PostMapping("/waiting")
    public WaitingItem create(@RequestBody WaitingItem item) {
        item.setId(null);
        if (item.getStatus() == null) item.setStatus("waiting");
        if (item.getDays() == null) item.setDays(0);
        WaitingItem saved = repo.save(item);
        activity.log("waiting", "新增等待事项：" + saved.getName(), null);
        return saved;
    }

    @PutMapping("/waiting/{id}")
    public WaitingItem update(@PathVariable Long id, @RequestBody WaitingItem body) {
        WaitingItem w = repo.findById(id).orElseThrow(() -> new NoSuchElementException("事项不存在"));
        if (body.getName() != null) w.setName(body.getName());
        if (body.getSource() != null) w.setSource(body.getSource());
        if (body.getDays() != null) w.setDays(body.getDays());
        if (body.getStatus() != null) w.setStatus(body.getStatus());
        return repo.save(w);
    }

    @PatchMapping("/waiting/{id}/status")
    public WaitingItem patch(@PathVariable Long id, @RequestParam String status) {
        WaitingItem w = repo.findById(id).orElseThrow(() -> new NoSuchElementException("事项不存在"));
        w.setStatus(status);
        WaitingItem saved = repo.save(w);
        if ("done".equals(status)) activity.log("waiting", "已收到回复：" + w.getName(), null);
        return saved;
    }

    @DeleteMapping("/waiting/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return Collections.singletonMap("ok", true);
    }
}
