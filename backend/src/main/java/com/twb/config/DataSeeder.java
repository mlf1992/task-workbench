package com.twb.config;

import com.twb.entity.*;
import com.twb.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 首次启动时初始化演示数据（库为空才写入，之后全部走真实增删改查）。
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final TaskRepository taskRepo;
    private final HomeTodoRepository homeRepo;
    private final ProjectRepository projectRepo;
    private final EventRepository eventRepo;
    private final WaitingRepository waitingRepo;
    private final FileAssetRepository fileRepo;
    private final DoneTaskRepository doneRepo;
    private final MilestoneRepository milestoneRepo;
    private final RiskRepository riskRepo;
    private final MeetingRepository meetingRepo;
    private final TagRepository tagRepo;
    private final ActivityRepository activityRepo;
    private final SettingRepository settingRepo;
    private final FocusRepository focusRepo;

    public DataSeeder(TaskRepository taskRepo, HomeTodoRepository homeRepo, ProjectRepository projectRepo,
                      EventRepository eventRepo, WaitingRepository waitingRepo, FileAssetRepository fileRepo,
                      DoneTaskRepository doneRepo, MilestoneRepository milestoneRepo, RiskRepository riskRepo,
                      MeetingRepository meetingRepo, TagRepository tagRepo, ActivityRepository activityRepo,
                      SettingRepository settingRepo, FocusRepository focusRepo) {
        this.taskRepo = taskRepo;
        this.homeRepo = homeRepo;
        this.projectRepo = projectRepo;
        this.eventRepo = eventRepo;
        this.waitingRepo = waitingRepo;
        this.fileRepo = fileRepo;
        this.doneRepo = doneRepo;
        this.milestoneRepo = milestoneRepo;
        this.riskRepo = riskRepo;
        this.meetingRepo = meetingRepo;
        this.tagRepo = tagRepo;
        this.activityRepo = activityRepo;
        this.settingRepo = settingRepo;
        this.focusRepo = focusRepo;
    }

    @Override
    public void run(String... args) {
        if (taskRepo.count() > 0) return;
        seedTasks();
        seedHomeTodos();
        seedProjects();
        seedEvents();
        seedWaiting();
        seedFiles();
        seedDoneTasks();
        seedMilestonesAndRisks();
        seedMeetings();
        seedTags();
        seedFocus();
        seedSettings();
        seedActivities();
    }

    private void seedTasks() {
        saveTask("跟进客户需求", "high", "今天 10:00", 1, "pending", "张磊", "c-blue", "准备方案", "客户维护", Arrays.asList("today"), true);
        saveTask("整理周例会材料", "mid", "今天 14:00", 2, "doing", "李雯", "c-green", "完善PPT", "日常工作", Arrays.asList("today"), true);
        saveTask("回复供应商报价", "high", "今天 17:00", 3, "waiting", "王强", "c-orange", "等供应商回复", "市场活动", Arrays.asList("today"), true);
        saveTask("修改项目方案V2", "mid", "明天 10:00", 4, "doing", "赵敏", "c-purple", "补充数据", "数据分析", Arrays.asList("today"), false);
        saveTask("确认活动设计稿", "high", "明天 15:00", 5, "waiting", "陈晨", "c-red", "等设计确认", "市场活动", Arrays.asList("today"), false);
        saveTask("准备门店开业物料", "mid", "9月17日", 6, "blocked", "刘洋", "c-cyan", "等供应商提供", "门店筹备", Arrays.asList(), false);
        saveTask("更新知识库文档", "low", "9月18日", 7, "doing", "孙倩", "c-pink", "补充案例", "团队管理", Arrays.asList(), false);
        saveTask("核对合同条款", "mid", "9月20日", 8, "pending", "周杰", "c-indigo", "法务确认", "客户维护", Arrays.asList("today"), false);
        saveTask("客户合同审核", "mid", "9月18日", 9, "waiting", "张磊", "c-blue", "跟进客户法务", "客户维护", Arrays.asList(), false);
        saveTask("门店选址反馈", "mid", "9月19日", 10, "waiting", "刘洋", "c-cyan", "等运营部反馈", "门店筹备", Arrays.asList(), false);
        saveTask("内容审核结果", "low", "9月19日", 11, "waiting", "孙倩", "c-pink", "等市场部回复", "市场活动", Arrays.asList(), false);
        saveTask("活动方案报批", "high", "9月16日", 12, "blocked", "赵敏", "c-purple", "等市场部负责人审批", "市场活动", Arrays.asList(), true);
        saveTask("系统权限开通", "mid", "9月15日", 13, "blocked", "周杰", "c-indigo", "等IT部门处理", "团队管理", Arrays.asList(), true);
    }

    private void saveTask(String name, String priority, String deadline, int sortTs, String status,
                          String owner, String color, String next, String project, List<String> tags, boolean urgent) {
        Task t = new Task();
        t.setName(name); t.setPriority(priority); t.setDeadline(deadline); t.setSortTs(sortTs);
        t.setStatus(status); t.setOwner(owner); t.setColor(color); t.setNext(next);
        t.setProject(project); t.setTags(tags); t.setUrgent(urgent);
        taskRepo.save(t);
    }

    private void seedHomeTodos() {
        saveHome("跟进客户需求", "high", "今天 10:00", "pending", true, false);
        saveHome("整理9月会议资料", "mid", "今天 14:00", "doing", true, false);
        saveHome("回复供应商报价", "high", "今天 17:00", "pending", true, false);
        saveHome("修改项目方案", "mid", "明天 10:00", "doing", false, false);
        saveHome("准备门店物料清单", "low", "明天 15:00", "pending", false, false);
        saveHome("更新知识库文档", "low", "9月17日", "done", false, true);
    }

    private void saveHome(String name, String p, String d, String s, boolean urgent, boolean done) {
        HomeTodo h = new HomeTodo();
        h.setName(name); h.setPriority(p); h.setDeadline(d); h.setStatus(s); h.setUrgent(urgent); h.setDone(done);
        homeRepo.save(h);
    }

    private void seedProjects() {
        Project p1 = project("客户A项目方案", "张磊", "blue", 65, "doing", 1, 25, 13, 20);
        p1.setStages(Arrays.asList(
                stage("需求调研", 1, 5, 100, "done", "张磊"),
                stage("方案设计", 6, 12, 100, "done", "赵敏"),
                stage("内部评审", 13, 18, 60, "doing", "周杰"),
                stage("客户确认", 19, 22, 0, "todo", "张磊"),
                stage("合同签订", 23, 25, 0, "todo", "张磊")));
        projectRepo.save(p1);

        Project p2 = project("门店开业筹备", "刘洋", "green", 42, "doing", 2, 30, 9, 22);
        p2.setStages(Arrays.asList(
                stage("选址确认", 2, 6, 100, "done", "刘洋"),
                stage("设计方案", 7, 13, 70, "doing", "陈晨"),
                stage("物料准备", 14, 22, 30, "blocked", "刘洋"),
                stage("人员培训", 23, 27, 0, "todo", "孙倩"),
                stage("开业预验收", 28, 30, 0, "todo", "刘洋")));
        projectRepo.save(p2);

        Project p3 = project("9月市场活动", "赵敏", "orange", 70, "risk", 1, 20, 14, 20);
        p3.setStages(Arrays.asList(
                stage("方案策划", 1, 8, 100, "done", "赵敏"),
                stage("预算审批", 9, 16, 80, "blocked", "赵敏"),
                stage("资源对接", 16, 18, 0, "todo", "王强"),
                stage("活动执行", 19, 20, 0, "todo", "赵敏")));
        projectRepo.save(p3);

        Project p4 = project("数据分析体系", "陈晨", "purple", 82, "doing", 1, 18, 18, 22);
        p4.setStages(Arrays.asList(
                stage("指标梳理", 1, 6, 100, "done", "陈晨"),
                stage("看板开发", 7, 14, 100, "done", "陈晨"),
                stage("数据校验", 15, 17, 50, "doing", "陈晨"),
                stage("上线验收", 18, 18, 0, "todo", "周杰")));
        projectRepo.save(p4);

        Project p5 = project("中秋活动复盘", "孙倩", "cyan", 25, "risk", 16, 28, 2, 8);
        p5.setStages(Arrays.asList(
                stage("数据收集", 16, 19, 25, "doing", "孙倩"),
                stage("复盘报告", 20, 25, 0, "todo", "孙倩"),
                stage("汇报评审", 26, 28, 0, "todo", "赵敏")));
        projectRepo.save(p5);

        Project p6 = project("团队培训计划", "周杰", "red", 100, "done", 3, 12, 6, 6);
        p6.setStages(Arrays.asList(
                stage("课程开发", 3, 8, 100, "done", "孙倩"),
                stage("培训实施", 9, 11, 100, "done", "周杰"),
                stage("考核总结", 12, 12, 100, "done", "周杰")));
        projectRepo.save(p6);
    }

    private Project project(String name, String owner, String tone, int progress, String status,
                            int start, int end, int tasksDone, int tasksTotal) {
        Project p = new Project();
        p.setName(name); p.setOwner(owner); p.setTone(tone); p.setProgress(progress); p.setStatus(status);
        p.setStartDay(start); p.setEndDay(end); p.setTasksDone(tasksDone); p.setTasksTotal(tasksTotal);
        return p;
    }

    private Stage stage(String name, int start, int end, int progress, String status, String owner) {
        Stage s = new Stage();
        s.setName(name); s.setStart(start); s.setEnd(end); s.setProgress(progress); s.setStatus(status); s.setOwner(owner);
        return s;
    }

    private void seedEvents() {
        event("2026-9-14", "14:00", "供应商电话沟通", "报价及交期确认", "30分钟", "gray");
        event("2026-9-14", "16:00", "方案V2内部评审", "会议室B · 内部讨论", "1小时", "blue");
        event("2026-9-15", "09:00", "团队晨会", "项目进展同步", "30分钟", "blue");
        event("2026-9-15", "10:00", "跟进客户需求", "电话沟通", "1小时", "red");
        event("2026-9-15", "11:30", "整理方案初稿", "内部讨论", "1小时", "gray");
        event("2026-9-15", "14:00", "9月会议资料整理", "汇总并提交", "1小时", "orange");
        event("2026-9-15", "15:30", "与供应商沟通", "报价及交期确认", "30分钟", "gray");
        event("2026-9-15", "17:00", "项目周报汇总", "提交管理层", "30分钟", "blue");
        event("2026-9-15", "19:00", "个人总结", "今日复盘", "30分钟", "gray");
        event("2026-9-16", "10:00", "活动方案报批跟进", "对接市场部审批", "30分钟", "red");
        event("2026-9-16", "15:00", "活动设计稿确认", "设计组联评", "1小时", "orange");
        event("2026-9-17", "09:30", "门店物料样品验收", "供应商到场", "1小时", "green");
        event("2026-9-17", "16:00", "周例会材料准备", "汇总本周进展", "30分钟", "gray");
        event("2026-9-18", "10:00", "客户A方案评审会", "会议室A · 方案评审", "1.5小时", "blue");
        event("2026-9-18", "14:00", "知识库更新评审", "团队内部评审", "1小时", "green");
        event("2026-9-20", "11:00", "合同条款核对会", "与法务逐条确认", "1小时", "red");
        event("2026-9-21", "10:30", "门店选址复盘", "运营部参与", "1小时", "purple");
        event("2026-9-23", "15:00", "供应商合同签订", "双方签字盖章", "1小时", "orange");
        event("2026-9-28", "10:00", "中秋活动复盘汇报", "向管理层汇报", "1小时", "blue");
        event("2026-9-30", "16:00", "9月项目结项汇总", "全员月度总结", "1小时", "purple");
    }

    private void event(String dkey, String start, String title, String desc, String dur, String color) {
        ScheduleEvent e = new ScheduleEvent();
        e.setDkey(dkey); e.setStart(start); e.setTitle(title); e.setDesc(desc); e.setDur(dur); e.setColor(color);
        eventRepo.save(e);
    }

    private void seedWaiting() {
        waiting("客户合同审批", "客户方 · 张磊", 2);
        waiting("供应商报价确认", "供应商", 1);
        waiting("门店设计方案反馈", "市场部", 3);
        waiting("活动资源支持", "运营部", 1);
        waiting("系统权限开通", "IT支持", 4);
    }

    private void waiting(String name, String source, int days) {
        WaitingItem w = new WaitingItem();
        w.setName(name); w.setSource(source); w.setDays(days); w.setStatus("waiting");
        waitingRepo.save(w);
    }

    private void seedFiles() {
        file("9月会议纪要.docx", "文档", "2026-09-15 11:20", "1.2 MB", "张磊", "c-blue", "W");
        file("门店物料清单.xlsx", "表格", "2026-09-15 09:48", "856 KB", "李雯", "c-green", "X");
        file("供应商报价单.pdf", "PDF", "2026-09-14 18:30", "2.4 MB", "王强", "c-orange", "PDF");
        file("项目方案V2.docx", "文档", "2026-09-14 16:20", "3.1 MB", "赵敏", "c-purple", "W");
        file("中秋活动方案.pptx", "演示文稿", "2026-09-13 14:05", "4.8 MB", "陈晨", "c-red", "P");
    }

    private void file(String name, String type, String time, String size, String owner, String color, String app) {
        FileAsset f = new FileAsset();
        f.setName(name); f.setType(type); f.setTime(time); f.setSize(size);
        f.setOwner(owner); f.setColor(color); f.setApp(app);
        fileRepo.save(f);
    }

    private void seedDoneTasks() {
        done("8月数据汇总分析", "2026-09-15 09:20", "2小时", "张磊", "c-blue", "数据分析");
        done("晨会同步与任务分派", "2026-09-15 08:40", "20分钟", "李雯", "c-green", "日常工作");
        done("客户邮件回复", "2026-09-15 08:10", "15分钟", "王强", "c-orange", "客户维护");
        done("昨日待办清零检查", "2026-09-15 07:55", "10分钟", "赵敏", "c-purple", "团队管理");
        done("会议纪要整理", "2026-09-14 18:30", "1小时", "李雯", "c-green", "日常工作");
        done("客户沟通记录", "2026-09-14 16:10", "1.5小时", "王强", "c-orange", "客户维护");
        done("活动预算明细", "2026-09-14 11:25", "2小时", "赵敏", "c-purple", "市场活动");
        done("团队周报提交", "2026-09-13 17:40", "1小时", "陈晨", "c-red", "团队管理");
    }

    private void done(String name, String time, String cost, String owner, String color, String project) {
        DoneTask d = new DoneTask();
        d.setName(name); d.setFinishTime(time); d.setCost(cost); d.setOwner(owner); d.setColor(color); d.setProject(project);
        doneRepo.save(d);
    }

    private void seedMilestonesAndRisks() {
        milestone(16, "活动方案报批截止", "9月市场活动", "risk");
        milestone(18, "客户A方案评审", "客户A项目方案", "plan");
        milestone(20, "供应商合同签订", "门店开业筹备", "plan");
        milestone(24, "门店开业预验收", "门店开业筹备", "plan");
        milestone(28, "中秋复盘汇报", "中秋活动复盘", "plan");
        milestone(30, "9月项目结项汇总", "全部项目", "done");

        risk("high", "活动方案报批受阻", "等待市场部负责人审批，已拖 3 天，将影响 9/19 活动执行", "9月市场活动");
        risk("mid", "门店物料未到位", "供应商未提供物料清单，已拖 2 天，物料准备节点延期风险", "门店开业筹备");
        risk("mid", "系统权限开通阻塞", "等待 IT 部门处理，已拖 5 天，影响团队培训安排", "团队培训计划");
    }

    private void milestone(int day, String title, String proj, String level) {
        Milestone m = new Milestone();
        m.setDay(day); m.setTitle(title); m.setProj(proj); m.setLevel(level);
        milestoneRepo.save(m);
    }

    private void risk(String level, String title, String desc, String proj) {
        ProjectRisk r = new ProjectRisk();
        r.setLevel(level); r.setTitle(title); r.setDesc(desc); r.setProj(proj);
        riskRepo.save(r);
    }

    private void seedMeetings() {
        meeting("9月第二次周例会", "2026-09-15", "张磊", "全体成员",
                "1. 同步各项目进展：客户A项目进入内部评审阶段；门店筹备物料受阻需升级。\n2. 本周重点：完成9月运营活动执行、供应商合同签订。\n3. 风险：活动方案报批仍在等待市场部确认，责任人赵敏，9/16前给出结论。",
                "例会");
        meeting("客户A项目需求沟通会", "2026-09-12", "张磊", "张磊、赵敏、客户方3人",
                "客户确认核心诉求：方案V2需补充数据看板模块；预算框架基本认可，要求9/18前完成评审版。",
                "客户");
        meeting("门店开业方案联评", "2026-09-10", "刘洋", "刘洋、陈晨、设计组",
                "门店设计方案整体通过，需调整门头灯箱尺寸；物料清单要求9/17前完成样品验收。",
                "评审");
    }

    private void meeting(String title, String date, String organizer, String attendees, String summary, String tag) {
        Meeting m = new Meeting();
        m.setTitle(title); m.setDate(date); m.setOrganizer(organizer);
        m.setAttendees(attendees); m.setSummary(summary); m.setTag(tag);
        meetingRepo.save(m);
    }

    private void seedTags() {
        tag("客户相关", "blue", Arrays.asList(1L, 8L, 9L));
        tag("本周冲刺", "red", Arrays.asList(1L, 2L, 3L, 5L));
        tag("供应商协同", "orange", Arrays.asList(3L, 6L));
        tag("文档与知识", "green", Arrays.asList(7L));
        tag("跨部门审批", "purple", Arrays.asList(5L, 12L, 13L));
    }

    private void tag(String name, String color, List<Long> taskIds) {
        Tag t = new Tag();
        t.setName(name); t.setColor(color); t.setTaskIds(taskIds);
        tagRepo.save(t);
    }

    private void seedFocus() {
        focus("home", "推进A项目需求评审", "doing");
        focus("home", "完成9月运营活动方案", "doing");
        focus("home", "门店开业物料准备", "todo");
        focus("home", "对接供应商并确认合同", "doing");
        focus("home", "整理中秋活动复盘报告", "todo");
        focus("home", "知识库内容更新（本周）", "doing");
        focus("home", "团队培训材料准备", "todo");
        focus("home", "下周工作计划与资源确认", "todo");

        focus("tasks", "门店开业筹备", "doing");
        focus("tasks", "客户A项目方案", "doing");
        focus("tasks", "供应商合同签订", "pending");
        focus("tasks", "9月市场活动执行", "doing");
        focus("tasks", "知识库内容更新", "doing");
    }

    private void focus(String scope, String name, String status) {
        FocusItem f = new FocusItem();
        f.setScope(scope); f.setName(name); f.setStatus(status);
        focusRepo.save(f);
    }

    private void seedSettings() {
        setting("userName", "我的工作台");
        setting("role", "任务管理员");
        setting("signature", "事情一多，先把要做的事理清楚");
        setting("workStart", "09:00");
        setting("weekStart", "1");
        setting("notifyBell", "true");
        setting("notifyEmail", "false");
        setting("overtimeRemind", "true");
        setting("dataStorage", "H2 文件数据库（./data/workbench）");
    }

    private void setting(String k, String v) {
        Setting s = new Setting();
        s.setKey(k); s.setValue(v);
        settingRepo.save(s);
    }

    private void seedActivities() {
        Activity a1 = new Activity();
        a1.setTime("2026-09-15 09:20"); a1.setType("task"); a1.setText("完成任务：8月数据汇总分析"); a1.setOperator("张磊");
        activityRepo.save(a1);
        Activity a2 = new Activity();
        a2.setTime("2026-09-15 09:48"); a2.setType("file"); a2.setText("上传资料：门店物料清单.xlsx"); a2.setOperator("李雯");
        activityRepo.save(a2);
        Activity a3 = new Activity();
        a3.setTime("2026-09-15 11:20"); a3.setType("meeting"); a3.setText("整理会议纪要：9月第二次周例会"); a3.setOperator("张磊");
        activityRepo.save(a3);
        Activity a4 = new Activity();
        a4.setTime("2026-09-14 18:30"); a4.setType("file"); a4.setText("上传资料：供应商报价单.pdf"); a4.setOperator("王强");
        activityRepo.save(a4);
        Activity a5 = new Activity();
        a5.setTime("2026-09-14 16:10"); a5.setType("task"); a5.setText("完成任务：客户沟通记录"); a5.setOperator("王强");
        activityRepo.save(a5);
    }
}
