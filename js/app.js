/* ====== 任务安排工作台 · 前端应用（数据全部来自 Spring Boot 后端） ====== */
const { createApp } = Vue;

createApp({
  data() {
    return {
      page: 'home',
      navItems: NAV,
      navMap: Object.fromEntries(NAV.map(n => [n.key, n])),

      keyword: '',
      showNotice: false,
      loaded: false,

      /* ---- 后端数据 ---- */
      stats: { home: {}, tasks: {}, projects: {} },
      tasks: [],
      homeTodos: [],
      doneTasks: [],
      projects: [],
      milestones: [],
      risks: [],
      events: [],
      waitingList: [],
      files: [],
      meetings: [],
      tags: [],
      activities: [],
      focusHome: [],
      focusTasks: [],
      settings: {},

      /* ---- 首页 ---- */
      homeTab: 'all',

      /* ---- 任务管理 ---- */
      taskTab: 'all',
      projectFilter: '全部项目',
      sortMode: '按截止时间',
      projectNames: PROJECT_NAMES,

      /* ---- 项目进度 ---- */
      selectedProjectId: null,
      projectFilterTab: 'all',
      projStatusText: { doing: '进行中', risk: '有风险', done: '已完成', todo: '未启动' },
      stageStatusText: { done: '已完成', doing: '进行中', blocked: '已阻塞', todo: '未开始' },
      msLevelText: { risk: '临近风险', plan: '计划中', done: '已完成' },

      /* ---- 日程安排 ---- */
      weekNames: ['一', '二', '三', '四', '五', '六', '日'],
      calYear: 2026, calMonth: 9,
      selYear: 2026, selMonth: 9, selDay: 15,

      /* ---- 等待回复 ---- */
      waitingTab: 'all',
      showWaitingForm: false,
      wform: { id: null, name: '', source: '', days: 1 },

      /* ---- 资料整理 ---- */
      fileType: '全部',
      fileKeyword: '',
      showFileForm: false,
      fform: { name: '', type: '文档', owner: '我' },

      /* ---- 会议纪要 ---- */
      meetingTag: '全部',
      showMeetingForm: false,
      mform: { id: null, title: '', date: '2026-09-18', organizer: '', attendees: '', summary: '', tag: '例会' },
      viewingMeeting: null,

      /* ---- 标签分类 ---- */
      showTagForm: false,
      tagForm: { id: null, name: '', color: 'blue', taskIds: [] },

      /* ---- 设置 ---- */
      settingsForm: { userName: '', role: '', signature: '', workStart: '09:00', weekStart: '1', notifyBell: true, notifyEmail: false, overtimeRemind: true },
      settingSavedTip: false,

      /* ---- 通用弹窗 / 下拉 ---- */
      showCreate: false,
      form: { name: '', priority: 'mid', status: 'pending', deadline: '', owner: '', project: '', next: '' },
      showProjectForm: false,
      pform: { name: '', owner: '', status: 'doing', startDate: '2026-09-20', endDate: '2026-09-28', desc: '' },
      showEventForm: false,
      eform: { title: '', date: '2026-09-15', start: '09:00', dur: '1小时', color: 'blue', desc: '' },
      dropdown: { show: false, type: '', options: [], x: 0, y: 0 },

      priText: { high: '高', mid: '中', low: '低' },
      statusText: { pending: '待处理', doing: '进行中', waiting: '等待回复', blocked: '已拖住', done: '已完成', todo: '待开始' },
      activityMeta: {
        task: { label: '任务', cls: 'blue' }, event: { label: '日程', cls: 'orange' },
        project: { label: '项目', cls: 'purple' }, waiting: { label: '等待', cls: 'red' },
        file: { label: '资料', cls: 'green' }, meeting: { label: '会议', cls: 'cyan' },
        tag: { label: '标签', cls: 'pink' }, system: { label: '系统', cls: 'gray' }
      }
    };
  },

  computed: {
    waitingBadge() { return this.waitingList.filter(w => w.status !== 'done').length; },

    /* 首页 Tab 计数 */
    homeTabs() {
      const list = this.homeTodos;
      return [
        { k: 'all', n: list.length, label: '全部' },
        { k: 'pending', n: list.filter(t => !t.done && t.status === 'pending').length, label: '待处理' },
        { k: 'doing', n: list.filter(t => !t.done && t.status === 'doing').length, label: '进行中' },
        { k: 'done', n: list.filter(t => t.done).length, label: '已完成' }
      ];
    },
    filteredHomeTodos() {
      let list = this.homeTodos;
      if (this.homeTab === 'pending') list = list.filter(t => !t.done && t.status === 'pending');
      else if (this.homeTab === 'doing') list = list.filter(t => !t.done && t.status === 'doing');
      else if (this.homeTab === 'done') list = list.filter(t => t.done);
      if (this.keyword.trim()) list = list.filter(t => t.name.includes(this.keyword.trim()));
      return list;
    },
    schedules() { return this.events.filter(e => e.dkey === '2026-9-15'); },

    /* 任务管理 Tab（数字来自后端统计） */
    taskTabs() {
      const s = this.stats.tasks || {};
      return [
        { k: 'all', n: s.all || 0, label: '全部' },
        { k: 'today', n: s.today || 0, label: '今天先做' },
        { k: 'waiting', n: (this.tasks.filter(t => t.status === 'waiting').length) || 0, label: '等待回复' },
        { k: 'blocked', n: (this.tasks.filter(t => t.status === 'blocked').length) || 0, label: '已拖住' },
        { k: 'done', n: s.done || 0, label: '已完成' }
      ];
    },
    filteredTasks() {
      let list = this.tasks.slice();
      if (this.taskTab === 'today') list = list.filter(t => (t.tags || []).includes('today') && t.status !== 'done');
      else if (this.taskTab === 'waiting') list = list.filter(t => t.status === 'waiting');
      else if (this.taskTab === 'blocked') list = list.filter(t => t.status === 'blocked');
      else if (this.taskTab === 'done') list = list.filter(t => t.status === 'done');
      if (this.projectFilter !== '全部项目') list = list.filter(t => t.project === this.projectFilter);
      if (this.keyword.trim()) list = list.filter(t => t.name.includes(this.keyword.trim()));
      list.sort((a, b) => this.sortMode === '按截止时间' ? (a.sortTs || 99) - (b.sortTs || 99) : (b.sortTs || 99) - (a.sortTs || 99));
      return list;
    },
    allChecked() {
      const l = this.filteredTasks;
      return l.length > 0 && l.every(t => t.status === 'done');
    },
    todayDo() { return this.tasks.filter(t => (t.tags || []).includes('today')).slice(0, 6); },
    sortLabel() { return this.sortMode; },

    /* 右侧快到期 / 已拖住（由任务数据派生） */
    dueSoon() {
      const leftMap = { 1: '剩余 2 小时', 2: '剩余 6 小时', 3: '剩余 9 小时', 4: '剩余 1 天' };
      const levelMap = { 1: 'red', 2: 'red', 3: 'orange', 4: 'green' };
      return [1, 2, 3, 4].map(id => {
        const t = this.tasks.find(x => x.id === id);
        return t ? { id, name: t.name, time: t.deadline, left: leftMap[id], level: levelMap[id] } : null;
      }).filter(Boolean);
    },
    blockedList() {
      const meta = {
        6: { no: 1, days: 2, desc: '等待供应商提供物料清单' },
        12: { no: 2, days: 3, desc: '等待市场部负责人审批' },
        13: { no: 4, days: 5, desc: '等待IT部门处理' }
      };
      return this.tasks.filter(t => t.status === 'blocked').map(t => ({
        id: t.id, name: t.name, ...(meta[t.id] || { no: 0, days: 1, desc: t.next })
      })).sort((a, b) => a.no - b.no);
    },

    /* ============ 项目进度 ============ */
    projectTabs() {
      return [
        { k: 'all', n: this.projects.length, label: '全部' },
        { k: 'doing', n: this.projects.filter(p => p.status === 'doing').length, label: '进行中' },
        { k: 'risk', n: this.projects.filter(p => p.status === 'risk').length, label: '有风险' },
        { k: 'done', n: this.projects.filter(p => p.status === 'done').length, label: '已完成' }
      ];
    },
    filteredProjects() {
      if (this.projectFilterTab === 'all') return this.projects;
      return this.projects.filter(p => p.status === this.projectFilterTab);
    },
    selectedProject() {
      return this.projects.find(p => p.id === this.selectedProjectId) || this.projects[0] || null;
    },

    /* ============ 日程 ============ */
    eventMap() {
      const map = {};
      this.events.forEach(e => { (map[e.dkey] = map[e.dkey] || []).push(e); });
      Object.values(map).forEach(list => list.sort((a, b) => a.start.localeCompare(b.start)));
      return map;
    },
    calendarCells() {
      const y = this.calYear, m = this.calMonth;
      const offset = (new Date(y, m - 1, 1).getDay() + 6) % 7;
      const days = new Date(y, m, 0).getDate();
      const prevDays = new Date(y, m - 1, 0).getDate();
      const cells = [];
      for (let i = 0; i < 42; i++) {
        let day, cy = y, cm = m, other = false;
        if (i < offset) { day = prevDays - offset + 1 + i; cm = m - 1; if (cm < 1) { cm = 12; cy = y - 1; } other = true; }
        else if (i >= offset + days) { day = i - (offset + days) + 1; cm = m + 1; if (cm > 12) { cm = 1; cy = y + 1; } other = true; }
        else day = i - offset + 1;
        cells.push({
          day, year: cy, month: cm,
          weekend: i % 7 >= 5,
          today: cy === 2026 && cm === 9 && day === 15,
          otherMonth: other,
          events: other ? [] : (this.eventMap[`${cy}-${cm}-${day}`] || [])
        });
      }
      return cells;
    },
    selectedDayEvents() { return this.eventMap[`${this.selYear}-${this.selMonth}-${this.selDay}`] || []; },
    selectedDayTasks() {
      if (this.selYear !== 2026 || this.selMonth !== 9) return [];
      return this.tasks.filter(t => this.deadlineDay(t.deadline) === this.selDay);
    },
    selWeekday() {
      return ['日', '一', '二', '三', '四', '五', '六'][new Date(this.selYear, this.selMonth - 1, this.selDay).getDay()];
    },
    isSelectedToday() { return this.selYear === 2026 && this.selMonth === 9 && this.selDay === 15; },
    upcomingEvents() {
      return this.events
        .filter(e => { const [y, m, d] = e.dkey.split('-').map(Number); return y === 2026 && m === 9 && d >= 15; })
        .sort((a, b) => a.dkey.localeCompare(b.dkey) || a.start.localeCompare(b.start))
        .slice(0, 7);
    },

    /* ============ 等待回复 ============ */
    filteredWaiting() {
      let list = this.waitingList;
      if (this.waitingTab === 'waiting') list = list.filter(w => w.status !== 'done');
      else if (this.waitingTab === 'done') list = list.filter(w => w.status === 'done');
      return list;
    },
    waitingAvgDays() {
      const act = this.waitingList.filter(w => w.status !== 'done');
      if (!act.length) return 0;
      return Math.round(act.reduce((s, w) => s + (w.days || 0), 0) / act.length * 10) / 10;
    },

    /* ============ 资料 ============ */
    fileTypes() { return ['全部', ...new Set(this.files.map(f => f.type))]; },
    filteredFiles() {
      let list = this.files;
      if (this.fileType !== '全部') list = list.filter(f => f.type === this.fileType);
      if (this.fileKeyword.trim()) list = list.filter(f => f.name.includes(this.fileKeyword.trim()));
      return list;
    },

    /* ============ 会议纪要 ============ */
    meetingTags() { return ['全部', ...new Set(this.meetings.map(m => m.tag || '例会'))]; },
    filteredMeetings() {
      let list = this.meetings;
      if (this.meetingTag !== '全部') list = list.filter(m => (m.tag || '例会') === this.meetingTag);
      return list;
    },

    /* ============ 历史 ============ */
    activityGroups() {
      const groups = {};
      this.activities.forEach(a => {
        const day = (a.time || '').slice(0, 10);
        (groups[day] = groups[day] || []).push(a);
      });
      return Object.keys(groups).sort().reverse().map(day => ({ day, items: groups[day] }));
    }
  },

  methods: {
    async refresh(part) {
      try {
        if (!part || part === 'stats') this.stats = await API.stats();
        if (!part || part === 'tasks') this.tasks = await API.tasks();
        if (!part || part === 'home') this.homeTodos = await API.homeTodos();
        if (!part || part === 'done') this.doneTasks = await API.doneTasks();
        if (!part || part === 'projects') {
          this.projects = await API.projects();
          this.milestones = await API.milestones();
          this.risks = await API.risks();
          if (this.selectedProjectId == null && this.projects.length) this.selectedProjectId = this.projects[0].id;
        }
        if (!part || part === 'events') this.events = await API.events();
        if (!part || part === 'waiting') this.waitingList = await API.waiting();
        if (!part || part === 'files') this.files = await API.files();
        if (!part || part === 'meetings') this.meetings = await API.meetings();
        if (!part || part === 'tags') this.tags = await API.tags();
        if (!part || part === 'activities') this.activities = await API.activities();
        if (!part || part === 'focus') {
          this.focusHome = await API.focus('home');
          this.focusTasks = await API.focus('tasks');
        }
        if (!part || part === 'settings') {
          this.settings = await API.settings();
          this.settingsForm = {
            userName: this.settings.userName || '',
            role: this.settings.role || '',
            signature: this.settings.signature || '',
            workStart: this.settings.workStart || '09:00',
            weekStart: this.settings.weekStart || '1',
            notifyBell: this.settings.notifyBell !== 'false',
            notifyEmail: this.settings.notifyEmail === 'true',
            overtimeRemind: this.settings.overtimeRemind !== 'false'
          };
        }
      } catch (e) {
        console.error('加载数据失败', e);
      }
    },

    go(p) {
      this.page = p;
      this.showNotice = false;
      if (p === 'tasks') this.taskTab = 'all';
      const el = document.querySelector('.content');
      if (el) el.scrollTop = 0;
    },

    /* ============ 首页待办 ============ */
    async onToggleHome(row) {
      row.done = !row.done;
      row.status = row.done ? 'done' : 'pending';
      await API.patchHome(row.id, { done: row.done, status: row.status });
      this.refresh('stats');
    },
    async removeHome(row) {
      await API.deleteHome(row.id);
      this.homeTodos = this.homeTodos.filter(t => t.id !== row.id);
    },
    homeStatus(row) { return row.done ? 'done' : row.status; },

    /* ============ 任务 ============ */
    async toggleTask(row) {
      const next = row.status === 'done' ? 'doing' : 'done';
      row.status = next;
      await API.taskStatus(row.id, next);
      this.refresh('stats');
      this.refresh('activities');
    },
    async toggleAll(e) {
      const checked = e.target.checked;
      for (const t of this.filteredTasks) {
        const ns = checked ? 'done' : 'doing';
        if (t.status !== ns) { t.status = ns; await API.taskStatus(t.id, ns); }
      }
      this.refresh('stats');
    },
    async removeTask(row) {
      await API.deleteTask(row.id);
      this.tasks = this.tasks.filter(t => t.id !== row.id);
      this.refresh('stats');
      this.refresh('activities');
    },
    rankColor(i) { return ['red', 'orange', 'blue', 'gray', 'gray', 'gray', 'gray', 'gray'][i] || 'gray'; },
    todayRankColor(i) { return ['red', 'orange', 'blue', 'red-light', 'orange', 'orange'][i] || 'gray'; },

    /* ============ 项目 ============ */
    barStyle(p) {
      return { left: ((p.startDay - 1) / 30 * 100) + '%', width: ((p.endDay - p.startDay + 1) / 30 * 100) + '%' };
    },
    projTag(s) { return s === 'risk' ? 'waiting' : s; },
    stageTag(s) { return s; },
    toneText(tone) {
      return { blue: 'blue', green: 'green', orange: 'orange', purple: 'purple', cyan: 'green', red: 'red' }[tone] || 'blue';
    },
    weekday(day) { return ['日', '一', '二', '三', '四', '五', '六'][new Date(2026, 8, day).getDay()]; },
    openProjectForm() {
      this.pform = { name: '', owner: '', status: 'doing', startDate: '2026-09-20', endDate: '2026-09-28', desc: '' };
      this.showProjectForm = true;
    },
    async saveProject() {
      if (!this.pform.name) { alert('请输入项目名称'); return; }
      const sd = new Date(this.pform.startDate), ed = new Date(this.pform.endDate);
      const start = sd.getMonth() === 8 ? sd.getDate() : 20;
      const end = ed.getMonth() === 8 ? ed.getDate() : 28;
      const tones = ['blue', 'green', 'orange', 'purple', 'cyan', 'red'];
      await API.createProject({
        name: this.pform.name, owner: this.pform.owner || '我',
        tone: tones[this.projects.length % tones.length], progress: 0,
        status: this.pform.status, startDay: Math.min(start, end), endDay: Math.max(start, end),
        tasksDone: 0, tasksTotal: 0, stages: []
      });
      this.showProjectForm = false;
      this.projectFilterTab = 'all';
      await this.refresh('projects');
      this.refresh('activities');
      const np = this.projects[this.projects.length - 1];
      if (np) this.selectedProjectId = np.id;
    },

    /* ============ 日程 ============ */
    deadlineDay(text) {
      if (!text) return null;
      if (text.startsWith('今天')) return 15;
      if (text.startsWith('明天')) return 16;
      const m = text.match(/9月(\d+)日/);
      return m ? Number(m[1]) : null;
    },
    prevMonth() { if (this.calMonth === 1) { this.calMonth = 12; this.calYear--; } else this.calMonth--; },
    nextMonth() { if (this.calMonth === 12) { this.calMonth = 1; this.calYear++; } else this.calMonth++; },
    selectDay(c) { this.selYear = c.year; this.selMonth = c.month; this.selDay = c.day; this.calYear = c.year; this.calMonth = c.month; },
    goToday() { this.calYear = 2026; this.calMonth = 9; this.selYear = 2026; this.selMonth = 9; this.selDay = 15; },
    openEventForm() {
      const pad = n => String(n).padStart(2, '0');
      this.eform = { title: '', date: `${this.selYear}-${pad(this.selMonth)}-${pad(this.selDay)}`, start: '09:00', dur: '1小时', color: 'blue', desc: '' };
      this.showEventForm = true;
    },
    async saveEvent() {
      if (!this.eform.title) { alert('请输入日程标题'); return; }
      const [y, m, d] = this.eform.date.split('-').map(Number);
      if (!y || !m || !d) { alert('请选择日期'); return; }
      await API.createEvent({ dkey: `${y}-${m}-${d}`, start: this.eform.start || '09:00', title: this.eform.title, desc: this.eform.desc || '—', dur: this.eform.dur, color: this.eform.color });
      this.calYear = y; this.calMonth = m; this.selYear = y; this.selMonth = m; this.selDay = d;
      this.showEventForm = false;
      await this.refresh('events');
      this.refresh('activities');
    },

    /* ============ 新建任务 ============ */
    openCreate() {
      this.form = { name: '', priority: 'mid', status: 'pending', deadline: '', owner: '', project: '', next: '' };
      this.showCreate = true;
    },
    async saveTask() {
      if (!this.form.name) { alert('请输入任务名称'); return; }
      let label = '待定', ts = 99;
      if (this.form.deadline) {
        const d = new Date(this.form.deadline);
        const m = d.getMonth() + 1, day = d.getDate(), hh = String(d.getHours()).padStart(2, '0'), mm = String(d.getMinutes()).padStart(2, '0');
        const diff = Math.round((new Date(2026, m - 1, day) - new Date(2026, 8, 15)) / 86400000);
        if (diff === 0) label = `今天 ${hh}:${mm}`;
        else if (diff === 1) label = `明天 ${hh}:${mm}`;
        else label = `${m}月${day}日`;
        ts = Math.max(diff, 0) * 10 + (d.getHours() || 9);
      }
      const colors = ['c-blue', 'c-green', 'c-orange', 'c-purple', 'c-red'];
      await API.createTask({
        name: this.form.name, priority: this.form.priority, deadline: label, sortTs: ts,
        status: this.form.status, owner: this.form.owner || '我',
        color: colors[this.tasks.length % colors.length], next: this.form.next || '—',
        project: this.form.project || '日常工作', tags: ['today'], urgent: false
      });
      this.showCreate = false;
      this.page = 'tasks'; this.taskTab = 'all';
      await this.refresh('tasks');
      this.refresh('stats');
      this.refresh('activities');
    },

    /* ============ 等待回复页 ============ */
    openWaitingForm(w) {
      if (w) this.wform = { id: w.id, name: w.name, source: w.source, days: w.days };
      else this.wform = { id: null, name: '', source: '', days: 1 };
      this.showWaitingForm = true;
    },
    async saveWaiting() {
      if (!this.wform.name) { alert('请输入事项名称'); return; }
      if (this.wform.id) {
        const w = this.waitingList.find(x => x.id === this.wform.id);
        await API.updateWaiting(this.wform.id, { ...w, name: this.wform.name, source: this.wform.source, days: Number(this.wform.days) });
      } else {
        await API.createWaiting({ name: this.wform.name, source: this.wform.source || '—', days: Number(this.wform.days) || 0, status: 'waiting' });
      }
      this.showWaitingForm = false;
      await this.refresh('waiting');
      this.refresh('stats');
      this.refresh('activities');
    },
    async setWaitingStatus(w, status) {
      await API.waitingStatus(w.id, status);
      await this.refresh('waiting');
      this.refresh('stats');
    },
    async removeWaiting(w) {
      await API.deleteWaiting(w.id);
      this.waitingList = this.waitingList.filter(x => x.id !== w.id);
      this.refresh('stats');
    },

    /* ============ 资料页 ============ */
    openFileForm() {
      this.fform = { name: '', type: '文档', owner: '我' };
      this.showFileForm = true;
    },
    async saveFile() {
      if (!this.fform.name) { alert('请输入文件名称'); return; }
      const now = new Date();
      const pad = n => String(n).padStart(2, '0');
      const appMap = { '文档': 'W', '表格': 'X', 'PDF': 'PDF', '演示文稿': 'P', '图片': 'F' };
      const colorMap = { '文档': 'c-blue', '表格': 'c-green', 'PDF': 'c-orange', '演示文稿': 'c-red', '图片': 'c-purple' };
      await API.createFile({
        name: this.fform.name, type: this.fform.type, owner: this.fform.owner || '我',
        time: `2026-09-18 ${pad(now.getHours())}:${pad(now.getMinutes())}`,
        size: '—', app: appMap[this.fform.type] || 'F', color: colorMap[this.fform.type] || 'c-blue'
      });
      this.showFileForm = false;
      await this.refresh('files');
    },
    triggerUpload() { this.$refs.fileInput.click(); },
    async uploadFile(e) {
      const file = e.target.files[0];
      if (!file) return;
      const fd = new FormData();
      fd.append('file', file);
      const res = await fetch(API.uploadUrl, { method: 'POST', body: fd });
      if (!res.ok) { alert('上传失败'); return; }
      e.target.value = '';
      await this.refresh('files');
      this.refresh('activities');
    },
    downloadFile(f) {
      if (f.path) window.open(API.downloadUrl(f.id), '_blank');
      else alert('该资料为登记记录，没有实际上传文件');
    },
    async removeFile(f) {
      await API.deleteFile(f.id);
      this.files = this.files.filter(x => x.id !== f.id);
    },

    /* ============ 会议纪要 ============ */
    openMeetingForm(m) {
      if (m) this.mform = { id: m.id, title: m.title, date: m.date, organizer: m.organizer, attendees: m.attendees, summary: m.summary, tag: m.tag || '例会' };
      else this.mform = { id: null, title: '', date: '2026-09-18', organizer: '', attendees: '', summary: '', tag: '例会' };
      this.showMeetingForm = true;
    },
    async saveMeeting() {
      if (!this.mform.title) { alert('请输入会议主题'); return; }
      if (this.mform.id) await API.updateMeeting(this.mform.id, this.mform);
      else await API.createMeeting(this.mform);
      this.showMeetingForm = false;
      await this.refresh('meetings');
      this.refresh('activities');
    },
    async removeMeeting(m) {
      await API.deleteMeeting(m.id);
      this.viewingMeeting = null;
      this.meetings = this.meetings.filter(x => x.id !== m.id);
    },

    /* ============ 标签 ============ */
    tagTasks(tag) { return (tag.taskIds || []).map(id => this.tasks.find(t => t.id === id)).filter(Boolean); },
    openTagForm(tag) {
      if (tag) this.tagForm = { id: tag.id, name: tag.name, color: tag.color, taskIds: [...(tag.taskIds || [])] };
      else this.tagForm = { id: null, name: '', color: 'blue', taskIds: [] };
      this.showTagForm = true;
    },
    toggleTagTask(id) {
      const i = this.tagForm.taskIds.indexOf(id);
      if (i >= 0) this.tagForm.taskIds.splice(i, 1); else this.tagForm.taskIds.push(id);
    },
    async saveTag() {
      if (!this.tagForm.name) { alert('请输入标签名称'); return; }
      if (this.tagForm.id) await API.updateTag(this.tagForm.id, this.tagForm);
      else await API.createTag(this.tagForm);
      this.showTagForm = false;
      await this.refresh('tags');
    },
    async removeTag(tag) {
      await API.deleteTag(tag.id);
      this.tags = this.tags.filter(t => t.id !== tag.id);
    },

    /* ============ 设置 ============ */
    async saveSettings() {
      const body = {};
      Object.keys(this.settingsForm).forEach(k => body[k] = String(this.settingsForm[k]));
      await API.saveSettings(body);
      await this.refresh('settings');
      this.refresh('activities');
      this.settingSavedTip = true;
      setTimeout(() => { this.settingSavedTip = false; }, 2200);
    },

    /* ============ 顶部下拉 ============ */
    openDropdown(type, ev) {
      const rect = ev.currentTarget.getBoundingClientRect();
      if (type === 'proj') this.dropdown.options = ['全部项目', ...PROJECT_NAMES];
      else this.dropdown.options = ['按截止时间', '按创建时间'];
      this.dropdown.type = type;
      this.dropdown.x = rect.right - 150;
      this.dropdown.y = rect.bottom + 6;
      this.dropdown.show = true;
    },
    pickDropdown(op) {
      if (this.dropdown.type === 'proj') this.projectFilter = op;
      else this.sortMode = op;
      this.dropdown.show = false;
    }
  },

  async mounted() {
    document.addEventListener('click', () => {
      this.dropdown.show = false;
      this.showNotice = false;
    });
    await this.refresh();
    this.loaded = true;
  }
}).mount('#app');
