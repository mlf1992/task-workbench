package com.twb.controller;

import com.twb.entity.ScheduleEvent;
import com.twb.repository.EventRepository;
import com.twb.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/** 日程事件 */
@RestController
@RequestMapping("/api")
public class EventController {

    private final EventRepository repo;
    private final ActivityService activity;

    public EventController(EventRepository repo, ActivityService activity) {
        this.repo = repo;
        this.activity = activity;
    }

    @GetMapping("/events")
    public List<ScheduleEvent> list() {
        List<ScheduleEvent> list = repo.findAll();
        list.sort((a, b) -> (a.getDkey() + a.getStart()).compareTo(b.getDkey() + b.getStart()));
        return list;
    }

    @PostMapping("/events")
    public ScheduleEvent create(@RequestBody ScheduleEvent e) {
        e.setId(null);
        ScheduleEvent saved = repo.save(e);
        activity.log("event", "新建日程：" + saved.getTitle(), null);
        return saved;
    }

    @PutMapping("/events/{id}")
    public ScheduleEvent update(@PathVariable Long id, @RequestBody ScheduleEvent body) {
        ScheduleEvent e = repo.findById(id).orElseThrow(() -> new NoSuchElementException("日程不存在"));
        if (body.getDkey() != null) e.setDkey(body.getDkey());
        if (body.getStart() != null) e.setStart(body.getStart());
        if (body.getTitle() != null) e.setTitle(body.getTitle());
        if (body.getDesc() != null) e.setDesc(body.getDesc());
        if (body.getDur() != null) e.setDur(body.getDur());
        if (body.getColor() != null) e.setColor(body.getColor());
        return repo.save(e);
    }

    @DeleteMapping("/events/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        ScheduleEvent e = repo.findById(id).orElse(null);
        repo.deleteById(id);
        if (e != null) activity.log("event", "删除日程：" + e.getTitle(), null);
        return Collections.singletonMap("ok", true);
    }
}
