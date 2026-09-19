package com.twb.controller;

import com.twb.entity.Milestone;
import com.twb.entity.Project;
import com.twb.entity.ProjectRisk;
import com.twb.repository.MilestoneRepository;
import com.twb.repository.ProjectRepository;
import com.twb.repository.RiskRepository;
import com.twb.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 项目 / 里程碑 / 风险
 */
@RestController
@RequestMapping("/api")
public class ProjectController {

    private final ProjectRepository projectRepo;
    private final MilestoneRepository milestoneRepo;
    private final RiskRepository riskRepo;
    private final ActivityService activity;

    public ProjectController(ProjectRepository projectRepo, MilestoneRepository milestoneRepo,
                             RiskRepository riskRepo, ActivityService activity) {
        this.projectRepo = projectRepo;
        this.milestoneRepo = milestoneRepo;
        this.riskRepo = riskRepo;
        this.activity = activity;
    }

    @GetMapping("/projects")
    public List<Project> projects() { return projectRepo.findAll(); }

    @PostMapping("/projects")
    public Project create(@RequestBody Project p) {
        p.setId(null);
        if (p.getProgress() == null) p.setProgress(0);
        if (p.getStatus() == null) p.setStatus("doing");
        Project saved = projectRepo.save(p);
        activity.log("project", "新建项目：" + saved.getName(), saved.getOwner());
        return saved;
    }

    @PutMapping("/projects/{id}")
    public Project update(@PathVariable Long id, @RequestBody Project body) {
        Project p = projectRepo.findById(id).orElseThrow(() -> new NoSuchElementException("项目不存在"));
        if (body.getName() != null) p.setName(body.getName());
        if (body.getOwner() != null) p.setOwner(body.getOwner());
        if (body.getTone() != null) p.setTone(body.getTone());
        if (body.getProgress() != null) p.setProgress(body.getProgress());
        if (body.getStatus() != null) p.setStatus(body.getStatus());
        if (body.getStartDay() != null) p.setStartDay(body.getStartDay());
        if (body.getEndDay() != null) p.setEndDay(body.getEndDay());
        if (body.getTasksDone() != null) p.setTasksDone(body.getTasksDone());
        if (body.getTasksTotal() != null) p.setTasksTotal(body.getTasksTotal());
        if (body.getStages() != null) p.setStages(body.getStages());
        return projectRepo.save(p);
    }

    @DeleteMapping("/projects/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        projectRepo.deleteById(id);
        activity.log("project", "删除项目 #" + id, null);
        return Collections.singletonMap("ok", true);
    }

    /* ---------- 里程碑 ---------- */
    @GetMapping("/milestones")
    public List<Milestone> milestones() {
        List<Milestone> list = milestoneRepo.findAll();
        list.sort(Comparator.comparing(Milestone::getDay));
        return list;
    }

    @PostMapping("/milestones")
    public Milestone addMilestone(@RequestBody Milestone m) {
        m.setId(null);
        return milestoneRepo.save(m);
    }

    @DeleteMapping("/milestones/{id}")
    public Map<String, Object> delMilestone(@PathVariable Long id) {
        milestoneRepo.deleteById(id);
        return Collections.singletonMap("ok", true);
    }

    /* ---------- 风险 ---------- */
    @GetMapping("/risks")
    public List<ProjectRisk> risks() { return riskRepo.findAll(); }

    @PostMapping("/risks")
    public ProjectRisk addRisk(@RequestBody ProjectRisk r) {
        r.setId(null);
        return riskRepo.save(r);
    }

    @DeleteMapping("/risks/{id}")
    public Map<String, Object> delRisk(@PathVariable Long id) {
        riskRepo.deleteById(id);
        return Collections.singletonMap("ok", true);
    }
}
