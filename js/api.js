/* ====== 后端接口封装（Spring Boot :8080，同源部署时 base 为空） ====== */
(function () {
  const BASE = '';

  async function request(method, url, body) {
    const opt = { method, headers: {} };
    if (body !== undefined) {
      opt.headers['Content-Type'] = 'application/json';
      opt.body = JSON.stringify(body);
    }
    const res = await fetch(BASE + url, opt);
    if (!res.ok) throw new Error(method + ' ' + url + ' -> ' + res.status);
    const ct = res.headers.get('content-type') || '';
    return ct.includes('application/json') ? res.json() : res.text();
  }

  const get = url => request('GET', url);
  const post = (url, body) => request('POST', url, body);
  const put = (url, body) => request('PUT', url, body);
  const patch = (url, body) => request('PATCH', url, body);
  const del = url => request('DELETE', url);

  window.API = {
    /* 聚合 */
    stats: () => get('/api/stats'),

    /* 任务 / 待办 / 完成 / 重点 */
    tasks: () => get('/api/tasks'),
    createTask: t => post('/api/tasks', t),
    updateTask: (id, t) => put('/api/tasks/' + id, t),
    taskStatus: (id, status) => patch(`/api/tasks/${id}/status?status=${status}`),
    deleteTask: id => del('/api/tasks/' + id),

    homeTodos: () => get('/api/home-todos'),
    patchHome: (id, body) => patch('/api/home-todos/' + id, body),
    deleteHome: id => del('/api/home-todos/' + id),

    doneTasks: () => get('/api/done-tasks'),
    deleteDone: id => del('/api/done-tasks/' + id),

    focus: scope => get('/api/focus?scope=' + scope),

    /* 项目 */
    projects: () => get('/api/projects'),
    createProject: p => post('/api/projects', p),
    updateProject: (id, p) => put('/api/projects/' + id, p),
    deleteProject: id => del('/api/projects/' + id),
    milestones: () => get('/api/milestones'),
    risks: () => get('/api/risks'),

    /* 日程 */
    events: () => get('/api/events'),
    createEvent: e => post('/api/events', e),
    deleteEvent: id => del('/api/events/' + id),

    /* 等待回复 */
    waiting: () => get('/api/waiting'),
    createWaiting: w => post('/api/waiting', w),
    updateWaiting: (id, w) => put('/api/waiting/' + id, w),
    waitingStatus: (id, status) => patch(`/api/waiting/${id}/status?status=${status}`),
    deleteWaiting: id => del('/api/waiting/' + id),

    /* 资料 */
    files: () => get('/api/files'),
    createFile: f => post('/api/files', f),
    deleteFile: id => del('/api/files/' + id),
    uploadUrl: '/api/files/upload',
    downloadUrl: id => `/api/files/${id}/raw`,

    /* 会议纪要 */
    meetings: () => get('/api/meetings'),
    createMeeting: m => post('/api/meetings', m),
    updateMeeting: (id, m) => put('/api/meetings/' + id, m),
    deleteMeeting: id => del('/api/meetings/' + id),

    /* 标签 */
    tags: () => get('/api/tags'),
    createTag: t => post('/api/tags', t),
    updateTag: (id, t) => put('/api/tags/' + id, t),
    deleteTag: id => del('/api/tags/' + id),

    /* 动态 / 设置 */
    activities: () => get('/api/activities'),
    settings: () => get('/api/settings'),
    saveSettings: m => put('/api/settings', m)
  };
})();
