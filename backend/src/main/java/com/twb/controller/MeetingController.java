package com.twb.controller;

import com.twb.entity.Meeting;
import com.twb.repository.MeetingRepository;
import com.twb.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/** 会议纪要 */
@RestController
@RequestMapping("/api")
public class MeetingController {

    private final MeetingRepository repo;
    private final ActivityService activity;

    public MeetingController(MeetingRepository repo, ActivityService activity) {
        this.repo = repo;
        this.activity = activity;
    }

    @GetMapping("/meetings")
    public List<Meeting> list() {
        List<Meeting> list = repo.findAll();
        list.sort(Comparator.comparing(Meeting::getDate).reversed());
        return list;
    }

    @PostMapping("/meetings")
    public Meeting create(@RequestBody Meeting m) {
        m.setId(null);
        Meeting saved = repo.save(m);
        activity.log("meeting", "整理会议纪要：" + saved.getTitle(), saved.getOrganizer());
        return saved;
    }

    @PutMapping("/meetings/{id}")
    public Meeting update(@PathVariable Long id, @RequestBody Meeting body) {
        Meeting m = repo.findById(id).orElseThrow(() -> new NoSuchElementException("纪要不存在"));
        if (body.getTitle() != null) m.setTitle(body.getTitle());
        if (body.getDate() != null) m.setDate(body.getDate());
        if (body.getOrganizer() != null) m.setOrganizer(body.getOrganizer());
        if (body.getAttendees() != null) m.setAttendees(body.getAttendees());
        if (body.getSummary() != null) m.setSummary(body.getSummary());
        if (body.getTag() != null) m.setTag(body.getTag());
        return repo.save(m);
    }

    @DeleteMapping("/meetings/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return Collections.singletonMap("ok", true);
    }
}
