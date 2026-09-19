/* ====== 任务安排工作台 · 静态配置（业务数据全部来自后端 /api） ====== */

const NAV = [
  { key: 'home',    label: '首页',     badge: 0, emoji: '🏠', icon: '<svg viewBox="0 0 24 24" width="20" height="20"><path d="M3 11l9-7 9 7v9h-6v-6H9v6H3z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/></svg>' },
  { key: 'tasks',   label: '任务管理', badge: 0, emoji: '✅', icon: '<svg viewBox="0 0 24 24" width="20" height="20"><rect x="3" y="3" width="18" height="18" rx="4" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M7 12.2l3.2 3.2L17 8.5" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>' },
  { key: 'project', label: '项目进度', badge: 0, emoji: '📊', icon: '<svg viewBox="0 0 24 24" width="20" height="20"><rect x="3" y="4" width="18" height="5" rx="1.5" fill="none" stroke="currentColor" stroke-width="1.8"/><rect x="3" y="11.5" width="18" height="5" rx="1.5" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>' },
  { key: 'waiting', label: '等待回复', badge: 'wait', emoji: '💬', icon: '<svg viewBox="0 0 24 24" width="20" height="20"><path d="M4 5h16v11H9l-5 4z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/></svg>' },
  { key: 'files',   label: '资料整理', badge: 0, emoji: '📁', icon: '<svg viewBox="0 0 24 24" width="20" height="20"><path d="M4 6a2 2 0 012-2h4l2 2.5h6a2 2 0 012 2V18a2 2 0 01-2 2H6a2 2 0 01-2-2z" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>' },
  { key: 'meeting', label: '会议纪要', badge: 0, emoji: '📝', icon: '<svg viewBox="0 0 24 24" width="20" height="20"><rect x="4" y="3" width="16" height="18" rx="2" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M8 8h8M8 12h8M8 16h5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>' },
  { key: 'schedule',label: '日程安排', badge: 0, emoji: '📅', icon: '<svg viewBox="0 0 24 24" width="20" height="20"><rect x="3.5" y="5" width="17" height="16" rx="2" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M3.5 9.5h17M8 3v4M16 3v4" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>' },
  { key: 'tag',     label: '标签分类', badge: 0, emoji: '🏷️', icon: '<svg viewBox="0 0 24 24" width="20" height="20"><path d="M3 12V4h8l10 10-8 8z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><circle cx="7.5" cy="8.5" r="1.5" fill="currentColor"/></svg>' },
  { key: 'history', label: '历史记录', badge: 0, emoji: '🕘', icon: '<svg viewBox="0 0 24 24" width="20" height="20"><circle cx="12" cy="12" r="9" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M12 7v5l3.5 2" stroke="currentColor" stroke-width="1.8" fill="none" stroke-linecap="round"/></svg>' },
  { key: 'setting', label: '设置',     badge: 0, emoji: '⚙️', icon: '<svg viewBox="0 0 24 24" width="20" height="20"><circle cx="12" cy="12" r="3.2" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M12 2.8v3M12 18.2v3M2.8 12h3M18.2 12h3M5.5 5.5l2.1 2.1M16.4 16.4l2.1 2.1M18.5 5.5l-2.1 2.1M7.6 16.4l-2.1 2.1" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>' }
];

const PROJECT_NAMES = ['数据分析', '日常工作', '客户维护', '市场活动', '团队管理', '门店筹备'];
