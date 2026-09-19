package com.twb.controller;

import com.twb.entity.*;
import com.twb.repository.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/** 首页/任务页统计、历史动态、系统设置 */
@RestController
@RequestMapping("/api")
public class SystemController {

    private final TaskRepository taskRepo;
    private final HomeTodoRepository homeRepo;
    private final DoneTaskRepository doneRepo;
    private final WaitingRepository waitingRepo;
    private final ProjectRepository projectRepo;
    private final FocusRepository focusRepo;
    private final ActivityRepository activityRepo;
    private final SettingRepository settingRepo;

    public SystemController(TaskRepository taskRepo, HomeTodoRepository homeRepo,
                            DoneTaskRepository doneRepo, WaitingRepository waitingRepo,
                            ProjectRepository projectRepo, FocusRepository focusRepo,
                            ActivityRepository activityRepo, SettingRepository settingRepo) {
        this.taskRepo = taskRepo;
        this.homeRepo = homeRepo;
        this.doneRepo = doneRepo;
        this.waitingRepo = waitingRepo;
        this.projectRepo = projectRepo;
        this.focusRepo = focusRepo;
        this.activityRepo = activityRepo;
        this.settingRepo = settingRepo;
    }

    /** 聚合统计：前端各页面卡片数字全部来自这里 */
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        List<Task> tasks = taskRepo.findAll();
        List<HomeTodo> homeTodos = homeRepo.findAll();
        List<DoneTask> doneTasks = doneRepo.findAll();
        List<WaitingItem> waiting = waitingRepo.findAll();
        List<Project> projects = projectRepo.findAll();
        List<FocusItem> homeFocus = focusRepo.findByScope("home");

        List<WaitingItem> waitingActive = waiting.stream()
                .filter(w -> !"done".equals(w.getStatus())).collect(Collectors.toList());
        double avgWait = waitingActive.stream().mapToInt(WaitingItem::getDays).average().orElse(0);
        long todayDone = doneTasks.stream().filter(d -> d.getFinishTime() != null && d.getFinishTime().startsWith("2026-09-15")).count();
        long nearDeadline = homeTodos.stream().filter(h -> Boolean.TRUE.equals(h.getUrgent()) && !Boolean.TRUE.equals(h.getDone())).count();

        Map<String, Object> home = new LinkedHashMap<>();
        home.put("todayTodo", homeTodos.size());
        home.put("nearDeadline", nearDeadline);
        home.put("weekFocus", homeFocus.size());
        home.put("weekDone", homeFocus.stream().filter(f -> "done".equals(f.getStatus())).count());
        home.put("waiting", waitingActive.size());
        home.put("avgWait", Math.round(avgWait * 10) / 10.0);
        home.put("done", doneTasks.size());
        home.put("todayDone", todayDone);

        long taskToday = tasks.stream().filter(t -> t.getTags() != null && t.getTags().contains("today") && !"done".equals(t.getStatus())).count();
        Map<String, Object> taskStats = new LinkedHashMap<>();
        taskStats.put("all", tasks.size());
        taskStats.put("doing", tasks.stream().filter(t -> "doing".equals(t.getStatus())).count());
        taskStats.put("today", taskToday);
        taskStats.put("waiting", tasks.stream().filter(t -> "waiting".equals(t.getStatus())).count());
        taskStats.put("blocked", tasks.stream().filter(t -> "blocked".equals(t.getStatus())).count());
        taskStats.put("done", tasks.stream().filter(t -> "done".equals(t.getStatus())).count() + doneTasks.size());

        Map<String, Object> projectStats = new LinkedHashMap<>();
        projectStats.put("all", projects.size());
        projectStats.put("healthy", projects.stream().filter(p -> "doing".equals(p.getStatus())).count());
        projectStats.put("risk", projects.stream().filter(p -> "risk".equals(p.getStatus())).count());
        projectStats.put("done", projects.stream().filter(p -> "done".equals(p.getStatus())).count());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("home", home);
        result.put("tasks", taskStats);
        result.put("projects", projectStats);
        return result;
    }

    /** 历史动态（最新在前） */
    @GetMapping("/activities")
    public List<Activity> activities() {
        return activityRepo.findAll().stream()
                .sorted(Comparator.comparing(Activity::getId).reversed())
                .collect(Collectors.toList());
    }

    /** 读取全部设置 */
    @GetMapping("/settings")
    public Map<String, String> settings() {
        Map<String, String> map = new LinkedHashMap<>();
        settingRepo.findAll().forEach(s -> map.put(s.getKey(), s.getValue()));
        return map;
    }

    /** 批量保存设置 */
    @PutMapping("/settings")
    public Map<String, Object> saveSettings(@RequestBody Map<String, String> body) {
        body.forEach((k, v) -> {
            Setting s = settingRepo.findById(k).orElseGet(() -> {
                Setting ns = new Setting();
                ns.setKey(k);
                return ns;
            });
            s.setValue(v);
            settingRepo.save(s);
        });
        Activity a = new Activity();
        a.setType("system");
        a.setText("更新了系统设置");
        a.setOperator("我");
        a.setTime(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        activityRepo.save(a);
        return Collections.singletonMap("ok", true);
    }
}
