package com.twb.controller;

import com.twb.entity.Tag;
import com.twb.repository.TagRepository;
import com.twb.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/** 标签分类 */
@RestController
@RequestMapping("/api")
public class TagController {

    private final TagRepository repo;
    private final ActivityService activity;

    public TagController(TagRepository repo, ActivityService activity) {
        this.repo = repo;
        this.activity = activity;
    }

    @GetMapping("/tags")
    public List<Tag> list() { return repo.findAll(); }

    @PostMapping("/tags")
    public Tag create(@RequestBody Tag t) {
        t.setId(null);
        if (t.getColor() == null) t.setColor("blue");
        Tag saved = repo.save(t);
        activity.log("tag", "新建标签：" + saved.getName(), null);
        return saved;
    }

    @PutMapping("/tags/{id}")
    public Tag update(@PathVariable Long id, @RequestBody Tag body) {
        Tag t = repo.findById(id).orElseThrow(() -> new NoSuchElementException("标签不存在"));
        if (body.getName() != null) t.setName(body.getName());
        if (body.getColor() != null) t.setColor(body.getColor());
        if (body.getTaskIds() != null) t.setTaskIds(body.getTaskIds());
        return repo.save(t);
    }

    @DeleteMapping("/tags/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return Collections.singletonMap("ok", true);
    }
}
