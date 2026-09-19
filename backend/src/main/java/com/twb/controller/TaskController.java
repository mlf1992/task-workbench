package com.twb.controller;

import com.twb.entity.*;
import com.twb.repository.*;
import com.twb.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务 / 首页待办 / 已完成 / 重点事项
 */
@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskRepository taskRepo;
    private final HomeTodoRepository homeRepo;
    private final DoneTaskRepository doneRepo;
    private final FocusRepository focusRepo;
    private final ActivityService activity;

    public TaskController(TaskRepository taskRepo, HomeTodoRepository homeRepo,
                          DoneTaskRepository doneRepo, FocusRepository focusRepo,
                          ActivityService activity) {
        this.taskRepo = taskRepo;
        this.homeRepo = homeRepo;
        this.doneRepo = doneRepo;
        this.focusRepo = focusRepo;
        this.activity = activity;
    }

    /* ---------- 任务 ---------- */
    @GetMapping("/tasks")
    public List<Task> tasks() {
        return taskRepo.findAll().stream().sorted(Comparator.comparing(Task::getSortTs)).collect(Collectors.toList());
    }

    @PostMapping("/tasks")
    public Task createTask(@RequestBody Task t) {
        t.setId(null);
        if (t.getSortTs() == null) t.setSortTs(99);
        if (t.getStatus() == null) t.setStatus("pending");
        if (t.getUrgent() == null) t.setUrgent(false);
        Task saved = taskRepo.save(t);
        activity.log("task", "新建任务：" + saved.getName(), saved.getOwner());
        return saved;
    }

    @PutMapping("/tasks/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task body) {
        Task t = taskRepo.findById(id).orElseThrow(() -> new NoSuchElementException("任务不存在"));
        body.setId(id);
        return taskRepo.save(mergeTask(t, body));
    }

    @PatchMapping("/tasks/{id}/status")
    public Task patchStatus(@PathVariable Long id, @RequestParam String status) {
        Task t = taskRepo.findById(id).orElseThrow(() -> new NoSuchElementException("任务不存在"));
        t.setStatus(status);
        Task saved = taskRepo.save(t);
        activity.log("task", ("done".equals(status) ? "完成任务：" : "更新任务状态：") + t.getName(), t.getOwner());
        return saved;
    }

    @DeleteMapping("/tasks/{id}")
    public Map<String, Object> deleteTask(@PathVariable Long id) {
        Task t = taskRepo.findById(id).orElse(null);
        taskRepo.deleteById(id);
        if (t != null) activity.log("task", "删除任务：" + t.getName(), t.getOwner());
        return Collections.singletonMap("ok", true);
    }

    private Task mergeTask(Task t, Task b) {
        if (b.getName() != null) t.setName(b.getName());
        if (b.getPriority() != null) t.setPriority(b.getPriority());
        if (b.getDeadline() != null) t.setDeadline(b.getDeadline());
        if (b.getSortTs() != null) t.setSortTs(b.getSortTs());
        if (b.getStatus() != null) t.setStatus(b.getStatus());
        if (b.getOwner() != null) t.setOwner(b.getOwner());
        if (b.getColor() != null) t.setColor(b.getColor());
        if (b.getNext() != null) t.setNext(b.getNext());
        if (b.getProject() != null) t.setProject(b.getProject());
        if (b.getTags() != null) t.setTags(b.getTags());
        if (b.getUrgent() != null) t.setUrgent(b.getUrgent());
        return t;
    }

    /* ---------- 首页待办 ---------- */
    @GetMapping("/home-todos")
    public List<HomeTodo> homeTodos() { return homeRepo.findAll(); }

    @PostMapping("/home-todos")
    public HomeTodo createHome(@RequestBody HomeTodo t) {
        t.setId(null);
        return homeRepo.save(t);
    }

    @PatchMapping("/home-todos/{id}")
    public HomeTodo patchHome(@PathVariable Long id, @RequestBody HomeTodo body) {
        HomeTodo t = homeRepo.findById(id).orElseThrow(() -> new NoSuchElementException("待办不存在"));
        if (body.getDone() != null) t.setDone(body.getDone());
        if (body.getStatus() != null) t.setStatus(body.getStatus());
        if (body.getName() != null) t.setName(body.getName());
        return homeRepo.save(t);
    }

    @DeleteMapping("/home-todos/{id}")
    public Map<String, Object> deleteHome(@PathVariable Long id) {
        homeRepo.deleteById(id);
        return Collections.singletonMap("ok", true);
    }

    /* ---------- 已完成任务 ---------- */
    @GetMapping("/done-tasks")
    public List<DoneTask> doneTasks() {
        return doneRepo.findAll().stream()
                .sorted(Comparator.comparing(DoneTask::getFinishTime).reversed())
                .collect(Collectors.toList());
    }

    @PostMapping("/done-tasks")
    public DoneTask createDone(@RequestBody DoneTask t) {
        t.setId(null);
        return doneRepo.save(t);
    }

    @DeleteMapping("/done-tasks/{id}")
    public Map<String, Object> deleteDone(@PathVariable Long id) {
        doneRepo.deleteById(id);
        return Collections.singletonMap("ok", true);
    }

    /* ---------- 重点事项 ---------- */
    @GetMapping("/focus")
    public List<FocusItem> focus(@RequestParam(defaultValue = "home") String scope) {
        return focusRepo.findByScope(scope);
    }

    @PostMapping("/focus")
    public FocusItem addFocus(@RequestBody FocusItem item) {
        item.setId(null);
        if (item.getScope() == null) item.setScope("home");
        return focusRepo.save(item);
    }

    @DeleteMapping("/focus/{id}")
    public Map<String, Object> delFocus(@PathVariable Long id) {
        focusRepo.deleteById(id);
        return Collections.singletonMap("ok", true);
    }
}
