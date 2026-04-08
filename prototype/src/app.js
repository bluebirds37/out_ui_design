const CORE_NAV = [
  { route: "welcome", label: "欢迎" },
  { route: "discover", label: "发现" },
  { route: "search", label: "搜索" },
  { route: "route-detail", label: "路线详情" },
  { route: "record-live", label: "轨迹记录" },
  { route: "profile", label: "我的" }
];

const SUPPORT_NAV = [
  { route: "login", label: "登录" },
  { route: "register", label: "注册" },
  { route: "profile-edit", label: "编辑资料" },
  { route: "record-prepare", label: "记录准备" },
  { route: "add-waypoint", label: "新增点位" },
  { route: "route-edit", label: "路线编辑" },
  { route: "publish-preview", label: "发布预览" },
  { route: "settings", label: "设置" }
];

const state = {
  drawerOpen: false,
  modal: null,
  toast: "",
  favorites: {
    routes: new Set(["north-ridge"]),
    authors: new Set(),
    waypoints: new Set(["ridge-viewpoint"])
  },
  forms: {
    login: {
      account: "hiker@trailnote.app",
      password: "",
      touched: {}
    },
    register: {
      nickname: "",
      account: "",
      password: "",
      confirmPassword: "",
      agreed: false,
      touched: {}
    },
    recordPrepare: {
      title: "北岭雾海穿越线",
      visibility: "公开",
      offlineReminder: true,
      touched: {}
    },
    waypoint: {
      type: "观景点",
      expandedTypes: false,
      title: "",
      description: "",
      touched: {}
    },
    comment: {
      text: "",
      touched: {}
    },
    profileEdit: {
      nickname: "景野",
      bio: "偏爱山脊线、湖边营地与日出路线记录",
      city: "上海",
      level: "轻中度徒步",
      interests: ["山脊线", "湖边营地", "日出路线"],
      touched: {}
    }
  },
  recording: {
    status: "idle",
    elapsed: "00:00:00",
    distanceKm: 0,
    ascentM: 0,
    altitudeM: 0,
    waypointCount: 0,
    pendingWaypoints: 0
  }
};

const app = document.getElementById("app");
const desktopCoreNav = document.getElementById("desktop-core-nav");
const desktopSupportNav = document.getElementById("desktop-support-nav");

const routeMeta = {
  "north-ridge": {
    id: "north-ridge",
    title: "北岭雾海穿越线",
    meta: "11.4km · 5h48m · 点位 9 · 收藏 3.4k"
  },
  "lake-trail": {
    id: "lake-trail",
    title: "环湖松林轻徒步",
    meta: "6.4km · 爬升 320m · 新手可走"
  },
  "rock-wind": {
    id: "rock-wind",
    title: "岩壁风口观景线",
    meta: "9.8km · 爬升 690m · 经验者适合"
  }
};

function route() {
  return window.location.hash.replace(/^#\/?/, "") || "welcome";
}

function navigate(nextRoute) {
  window.location.hash = nextRoute;
}

function topStatusBar(inverse = false, right = "5G 88%") {
  return `
    <div class="status-bar ${inverse ? "status-bar--inverse" : ""}">
      <span>9:41</span>
      <div class="status-icons"><span>${right}</span></div>
    </div>
  `;
}

function topbar({ left = "←", title = "", right = "", inverse = false, rightType = "text" } = {}) {
  const leftClass = inverse ? "nav-icon nav-icon--inverse" : "nav-icon";
  const rightMarkup =
    rightType === "pill"
      ? `<div class="${inverse ? "pill pill--inverse" : "pill"}">${right}</div>`
      : right
        ? `<button class="${inverse ? "nav-icon nav-icon--inverse" : "nav-icon"}">${right}</button>`
        : `<div style="width:42px;"></div>`;
  return `
    <div class="topbar">
      <button class="${leftClass}" data-route-back>${left}</button>
      ${title ? `<div class="list-card__title" style="margin:0; font-size:18px;">${title}</div>` : `<div></div>`}
      ${rightMarkup}
    </div>
  `;
}

function bottomNav(active) {
  return `
    <nav class="bottom-nav">
      ${[
        ["discover", "⌂", "发现"],
        ["search", "⌕", "搜索"],
        ["record-live", "◎", "记录"],
        ["profile", "◌", "我的"]
      ]
        .map(
          ([routeName, glyph, label]) => `
            <button class="bottom-nav__item ${active === routeName ? "is-active" : ""}" data-route="${routeName}">
              <span class="bottom-nav__glyph">${glyph}</span>
              <span>${label}</span>
            </button>
          `
        )
        .join("")}
    </nav>
  `;
}

function pageSwitcherDrawer() {
  const pages = [...CORE_NAV, ...SUPPORT_NAV];
  return `
    <div class="prototype-drawer ${state.drawerOpen ? "is-open" : ""}">
      <button class="prototype-drawer__scrim" data-close-drawer></button>
      <div class="prototype-drawer__sheet">
        <div class="prototype-drawer__handle"></div>
        <div class="section-row" style="margin-bottom:14px;">
          <h3 class="section-title section-title--sm">页面切换</h3>
          <button class="button-ghost" data-close-drawer>关闭</button>
        </div>
        <div class="prototype-drawer__grid">
          ${pages
            .map(
              (item) => `
                <button data-route="${item.route}">${item.label}</button>
              `
            )
            .join("")}
        </div>
      </div>
    </div>
  `;
}

function favoriteButton(kind, id, label = "收藏") {
  const active = state.favorites[kind].has(id);
  return `
    <button class="favorite-toggle ${active ? "is-active" : ""}" data-favorite-kind="${kind}" data-favorite-id="${id}">
      <span>${active ? "★" : "☆"}</span>
      <span>${active ? "已收藏" : label}</span>
    </button>
  `;
}

function routeCard({ id, title, meta, chips = [], routeName = "route-detail", cover }) {
  const coverStyle = cover ? `style="background:${cover}"` : "";
  return `
    <button class="route-card" data-route="${routeName}">
      <div class="route-card__cover" ${coverStyle}></div>
      <div class="route-card__title">${title}</div>
      <div class="meta-text">${meta}</div>
      ${chips.length ? `<div class="chips" style="margin-top:12px;">${chips.join("")}</div>` : ""}
      <div style="margin-top:12px;">${favoriteButton("routes", id)}</div>
    </button>
  `;
}

function listCard({ title, meta, submeta = "", routeName = "", thumbStyle = "", extraAction = "" }) {
  return `
    <button class="list-card" ${routeName ? `data-route="${routeName}"` : ""}>
      <div class="list-card__thumb" ${thumbStyle ? `style="background:${thumbStyle}"` : ""}></div>
      <div class="list-card__body">
        <div class="list-card__title">${title}</div>
        <div class="meta-text">${meta}</div>
        ${submeta ? `<div class="chips" style="margin-top:10px;">${submeta}</div>` : ""}
        ${extraAction ? `<div style="margin-top:10px;">${extraAction}</div>` : ""}
      </div>
    </button>
  `;
}

function renderChip(label, kind = "") {
  return `<span class="chip ${kind}">${label}</span>`;
}

function renderSelectableChip(form, field, value, kind = "") {
  const selected = state.forms[form][field].includes(value);
  return `<button class="chip ${selected ? "chip--selected" : kind}" data-selectable-chip="${value}" data-chip-form="${form}" data-chip-field="${field}">${value}</button>`;
}

function renderWaypointTypeButton(type, kind = "") {
  const selected = state.forms.waypoint.type === type;
  const selectedClass = selected ? "chip--selected" : kind;
  return `<button class="chip ${selectedClass}" data-waypoint-type="${type}">${type}</button>`;
}

function safe(content, { withFab = true } = {}) {
  return `
    <div class="screen">
      <div class="safe-area">
        ${content}
      </div>
      ${withFab ? `<button class="fab" data-open-drawer>页</button>` : ""}
      ${pageSwitcherDrawer()}
      ${renderModal()}
      ${renderToast()}
    </div>
  `;
}

function renderToast() {
  if (!state.toast) return "";
  return `<div class="prototype-toast">${state.toast}</div>`;
}

function setToast(message) {
  state.toast = message;
  render();
  window.clearTimeout(setToast._timer);
  setToast._timer = window.setTimeout(() => {
    state.toast = "";
    render();
  }, 1800);
}

function openModal(name, payload = {}) {
  state.modal = { name, payload };
  render();
}

function closeModal() {
  state.modal = null;
  render();
}

function renderModal() {
  if (!state.modal) return "";

  const { name, payload } = state.modal;
  let title = "";
  let body = "";
  let actions = "";

  if (name === "favorite-route") {
    title = state.favorites.routes.has(payload.id) ? "已加入收藏" : "移出收藏";
    body = state.favorites.routes.has(payload.id)
      ? `路线“${payload.title}”已加入收藏夹，后续可在收藏夹快速查看。`
      : `要将“${payload.title}”从收藏夹中移除吗？`;
    actions = state.favorites.routes.has(payload.id)
      ? `<button class="button-secondary" data-modal-close>继续浏览</button>
         <button class="button-primary" data-route="favorites" data-modal-close>查看收藏夹</button>`
      : `<button class="button-secondary" data-modal-close>取消</button>
         <button class="button-primary" data-confirm-unfavorite="${payload.id}" data-modal-close>确认移除</button>`;
  }

  if (name === "record-stop") {
    title = "结束本次记录";
    body = `当前已记录 ${state.recording.distanceKm.toFixed(1)}km、${state.recording.waypointCount} 个点位，仍有 ${state.recording.pendingWaypoints} 个点位待补充。`;
    actions = `
      <button class="button-secondary" data-modal-close>继续记录</button>
      <button class="button-primary" data-record-transition="ended" data-route="route-edit" data-modal-close>结束并整理</button>
    `;
  }

  if (name === "record-pause") {
    title = "暂停记录";
    body = "暂停后将停止轨迹采样，但会保留当前记录成果，你可以稍后继续。";
    actions = `
      <button class="button-secondary" data-modal-close>取消</button>
      <button class="button-primary" data-record-transition="paused" data-modal-close>确认暂停</button>
    `;
  }

  if (name === "comment-success") {
    title = "评论已发布";
    body = "你的补充会帮助后来的徒步者判断路线与路况。";
    actions = `
      <button class="button-primary" data-modal-close>知道了</button>
    `;
  }

  return `
    <div class="prototype-modal is-open">
      <button class="prototype-modal__scrim" data-modal-close></button>
      <div class="prototype-modal__sheet">
        <div class="prototype-drawer__handle"></div>
        <div class="section-title section-title--sm" style="font-size:28px;">${title}</div>
        <p class="body-copy" style="margin-top:10px;">${body}</p>
        <div class="prototype-modal__actions">
          ${actions}
        </div>
      </div>
    </div>
  `;
}

function getFieldError(formName, fieldName) {
  const form = state.forms[formName];
  if (!form?.touched?.[fieldName]) return "";

  const value = form[fieldName];
  if (formName === "login") {
    if (fieldName === "account" && !String(value).trim()) return "请输入手机号或邮箱";
    if (fieldName === "password" && String(value).length < 6) return "密码至少 6 位";
  }

  if (formName === "register") {
    if (fieldName === "nickname" && !String(value).trim()) return "请输入昵称";
    if (fieldName === "account" && !String(value).trim()) return "请输入手机号或邮箱";
    if (fieldName === "password" && String(value).length < 8) return "密码至少 8 位";
    if (fieldName === "confirmPassword" && value !== form.password) return "两次输入密码不一致";
  }

  if (formName === "recordPrepare") {
    if (fieldName === "title" && !String(value).trim()) return "路线名称不能为空";
  }

  if (formName === "waypoint") {
    if (fieldName === "title" && !String(value).trim()) return "请填写点位标题";
    if (fieldName === "description" && !String(value).trim()) return "建议补充点位说明";
  }

  if (formName === "comment") {
    if (fieldName === "text" && !String(value).trim()) return "请输入评论内容";
  }

  if (formName === "profileEdit") {
    if (fieldName === "nickname" && !String(value).trim()) return "请输入昵称";
    if (fieldName === "bio" && !String(value).trim()) return "请输入个人简介";
    if (fieldName === "city" && !String(value).trim()) return "请输入常驻城市";
    if (fieldName === "level" && !String(value).trim()) return "请输入经验等级";
  }

  return "";
}

function formField({ form, field, label, placeholder = "", type = "text", rows = 1 }) {
  const value = state.forms[form][field];
  const error = getFieldError(form, field);
  const fieldClass = error ? "field field--error" : value ? "field field--focus" : "field";
  const inputMarkup =
    rows > 1
      ? `<textarea class="field-input field-input--textarea" rows="${rows}" data-form="${form}" data-field="${field}" placeholder="${placeholder}">${value || ""}</textarea>`
      : `<input class="field-input" type="${type}" value="${value || ""}" data-form="${form}" data-field="${field}" placeholder="${placeholder}" />`;
  return `
    <label class="${fieldClass}">
      <span class="field__label">${label}</span>
      ${inputMarkup}
      ${error ? `<span class="field__error">${error}</span>` : ""}
    </label>
  `;
}

function validateForm(formName, fields) {
  const touched = state.forms[formName].touched;
  fields.forEach((field) => {
    touched[field] = true;
  });
  const errors = fields.map((field) => getFieldError(formName, field)).filter(Boolean);
  return errors.length === 0;
}

function updateRecordingMetrics(status) {
  if (status === "recording") {
    state.recording.elapsed = "01:46:22";
    state.recording.distanceKm = 6.8;
    state.recording.ascentM = 541;
    state.recording.altitudeM = 1286;
    state.recording.waypointCount = 5;
    state.recording.pendingWaypoints = 1;
  }

  if (status === "paused") {
    state.recording.elapsed = "01:46:22";
  }

  if (status === "ended") {
    state.recording.elapsed = "05:48:18";
    state.recording.distanceKm = 11.4;
    state.recording.ascentM = 860;
    state.recording.altitudeM = 1388;
    state.recording.waypointCount = 9;
    state.recording.pendingWaypoints = 0;
  }
}

function setRecordingStatus(status) {
  state.recording.status = status;
  updateRecordingMetrics(status);
  const statusMessage = {
    idle: "记录待开始",
    recording: "已开始记录",
    paused: "记录已暂停",
    ended: "记录已结束，进入整理"
  };
  setToast(statusMessage[status]);
}

function discoverScreen() {
  return safe(`
    ${topStatusBar(false, "定位已开 86%")}
    <div class="topbar">
      <div>
        <div class="meta-text">上海 · 12°C · 云层薄</div>
        <h1 class="section-title">发现路线</h1>
      </div>
      <button class="nav-icon" data-route="settings">⚙</button>
    </div>
    <button class="searchbar" data-route="search">⌕ 搜索路线、点位、作者</button>
    <div class="hero-card" style="margin-top:16px;">
      <span class="pill pill--inverse">今日热门</span>
      <h2 class="hero-card__title" style="font-size:34px;">雾线谷地<br/>晨光穿越</h2>
      <p class="hero-card__body">4 位分享者共同标注了 12 个关键点位，含 8 段视频记录与补给提醒。</p>
    </div>
    <div class="section-row" style="margin:18px 0 12px;">
      <h2 class="section-title section-title--sm">周边可去</h2>
      <button class="inline-link" data-route="search">查看全部</button>
    </div>
    <div class="grid-2">
      ${routeCard({
        id: "lake-trail",
        title: "环湖松林轻徒步",
        meta: "6.4km · 爬升 320m · 新手可走",
        chips: [renderChip("收藏 2.1k"), renderChip("点位 5", "chip--alt")]
      })}
      ${routeCard({
        id: "rock-wind",
        title: "岩壁风口观景线",
        meta: "9.8km · 爬升 690m · 经验者适合",
        chips: [renderChip("视频 14"), renderChip("关注 981", "chip--alt")],
        cover:
          "linear-gradient(180deg, rgba(17, 23, 20, 0.04), rgba(17, 23, 20, 0.46)), url('https://images.unsplash.com/photo-1464822759844-d150ad6d0f50?auto=format&fit=crop&w=900&q=80') center/cover"
      })}
    </div>
    ${bottomNav("discover")}
  `);
}

const screens = {
  welcome() {
    return safe(`
      ${topStatusBar(false, "5G 88%")}
      <div class="hero-card stack-14">
        <span class="pill pill--inverse">TRAILNOTE</span>
        <h1 class="hero-card__title">记录每一步<br/>也分享给后来者</h1>
        <p class="hero-card__body">徒步轨迹、关键点位、照片与视频统一沉淀，让路线不只是一条线。</p>
      </div>
      <div class="stack-12" style="margin-top:18px;">
        <button class="button-primary" data-route="discover">开始探索</button>
        <button class="button-secondary" data-route="login">已有账号，去登录</button>
      </div>
      <div class="sheet stack-14" style="margin-top:18px;">
        <div class="section-row">
          <h2 class="section-title section-title--sm">今天适合出发</h2>
          <button class="inline-link" data-route="search">查看附近</button>
        </div>
        <div class="grid-2">
          <div class="metric-card"><div class="metric-card__label">热门路线</div><div class="metric-card__value">148</div></div>
          <div class="metric-card"><div class="metric-card__label">新增点位</div><div class="metric-card__value">624</div></div>
        </div>
        ${listCard({
          title: "龙脊东线晨雾路线",
          meta: "8.2km · 4h20m · 观景点 6 个",
          routeName: "route-detail",
          submeta: `${renderChip("新手友好")}${renderChip("视频路线", "chip--alt")}`
        })}
      </div>
      <div style="flex:1;"></div>
    `);
  },
  login() {
    const valid = !getFieldError("login", "account") && !getFieldError("login", "password") && state.forms.login.account && state.forms.login.password.length >= 6;
    return safe(`
      ${topStatusBar(false, "5G 89%")}
      ${topbar({ right: "账号安全", rightType: "pill" })}
      <div>
        <h1 class="section-title">欢迎回来</h1>
        <p class="body-copy" style="margin-top:8px;">继续记录你的路线，或者查看最近收藏的徒步目的地。</p>
      </div>
      <div class="sheet stack-16" style="margin-top:18px;">
        ${formField({ form: "login", field: "account", label: "手机号 / 邮箱", placeholder: "请输入手机号或邮箱" })}
        ${formField({ form: "login", field: "password", label: "密码", placeholder: "请输入密码", type: "password" })}
        <div class="section-row">
          <span class="meta-text">已阅读并同意服务协议</span>
          <button class="inline-link" data-route="forgot-password">忘记密码</button>
        </div>
        <button class="button-primary" data-submit="login" ${valid ? "" : ""}>登录</button>
      </div>
      <div class="sheet stack-12" style="margin-top:14px;">
        <div class="section-row">
          <span class="meta-text">其他登录方式</span>
          <span class="inline-link">帮助</span>
        </div>
        <div class="grid-2">
          <button class="button-secondary">Apple</button>
          <button class="button-secondary">微信</button>
        </div>
      </div>
      <div style="flex:1;"></div>
      <div class="meta-text" style="text-align:center;">还没有账号？<button class="inline-link" data-route="register">立即注册</button></div>
    `);
  },
  "forgot-password"() {
    return safe(`
      ${topStatusBar(false, "短信验证 88%")}
      ${topbar({ title: "找回密码", right: "安全验证", rightType: "pill" })}
      <div class="sheet stack-14">
        <div class="field"><span class="field__label">手机号 / 邮箱</span><div class="field__value">138 0000 2468</div></div>
        <div class="grid-2">
          <div class="field"><span class="field__label">验证码</span><div class="field__value">628491</div></div>
          <button class="button-secondary" style="min-height:72px;">重新发送<br/>56s</button>
        </div>
        <div class="field"><span class="field__label">新密码</span><div class="field__value">至少 8 位，建议包含大小写</div></div>
        <div class="meta-text">若 5 分钟内未收到验证码，可切换邮箱找回或联系支持。</div>
        <button class="button-primary" data-route="login">重置密码</button>
      </div>
    `);
  },
  register() {
    return safe(`
      ${topStatusBar(false, "5G 90%")}
      ${topbar({ title: "创建账号", right: "1 / 2", rightType: "pill" })}
      <div class="hero-card hero-card--compact" style="background:
        linear-gradient(180deg, rgba(7, 12, 10, 0.02), rgba(7, 12, 10, 0.46)),
        linear-gradient(140deg, rgba(23, 53, 44, 0.8), rgba(197, 157, 95, 0.38)),
        url('https://images.unsplash.com/photo-1501554728187-ce583db33af7?auto=format&fit=crop&w=900&q=80') center/cover;">
        <span class="pill pill--inverse">NEW HIKER</span>
        <h2 class="hero-card__title" style="font-size:36px; margin-top:26px;">把真实路线<br/>沉淀成可参考的经验</h2>
      </div>
      <div class="sheet stack-14" style="margin-top:18px;">
        ${formField({ form: "register", field: "nickname", label: "昵称", placeholder: "请输入昵称" })}
        ${formField({ form: "register", field: "account", label: "手机号 / 邮箱", placeholder: "请输入手机号或邮箱" })}
        ${formField({ form: "register", field: "password", label: "密码", placeholder: "至少 8 位", type: "password" })}
        ${formField({ form: "register", field: "confirmPassword", label: "确认密码", placeholder: "再次输入密码", type: "password" })}
        <label class="agree-row">
          <input type="checkbox" ${state.forms.register.agreed ? "checked" : ""} data-form-toggle="register" data-field="agreed" />
          <span>注册即表示你同意 TrailNote 服务协议与隐私政策。</span>
        </label>
        <button class="button-primary" data-submit="register">注册并继续</button>
      </div>
    `);
  },
  "complete-profile"() {
    return safe(`
      ${topStatusBar(false, "新用户引导 92%")}
      ${topbar({ title: "完善资料" })}
      <div class="sheet stack-14">
        <div class="user-row">
          <div class="avatar avatar--lg">+</div>
          <div>
            <div class="list-card__title" style="font-size:18px; margin:0 0 6px;">添加头像与昵称</div>
            <div class="meta-text">让你的路线分享更容易建立信任感</div>
          </div>
        </div>
        <div class="field"><span class="field__label">昵称</span><div class="field__value">山野风</div></div>
        <div class="grid-2">
          <div class="field"><span class="field__label">常驻城市</span><div class="field__value">上海</div></div>
          <div class="field"><span class="field__label">经验等级</span><div class="field__value">轻中度徒步</div></div>
        </div>
        <div class="field">
          <span class="field__label">感兴趣主题</span>
          <div class="chips">
            ${renderChip("山脊线")}
            ${renderChip("湖边营地", "chip--alt")}
            ${renderChip("日出路线")}
            ${renderChip("新手友好", "chip--warn")}
          </div>
        </div>
        <button class="button-primary" data-route="discover">完成并进入首页</button>
      </div>
    `);
  },
  discover: discoverScreen,
  search() {
    return safe(`
      ${topStatusBar(false, "在线 84%")}
      <button class="searchbar">⌕ 搜索 “武功山”</button>
      <div class="chips" style="margin-top:12px;">
        ${renderChip("路线")}
        ${renderChip("点位", "chip--alt")}
        ${renderChip("作者")}
        <button class="chip chip--warn" data-route="map-results">地图模式</button>
      </div>
      <div class="chips" style="margin-top:10px;">
        ${renderChip("8km 以内")}
        ${renderChip("新手友好")}
        ${renderChip("有补给", "chip--alt")}
        ${renderChip("春季推荐")}
      </div>
      <div class="stack-12" style="margin-top:14px;">
        ${listCard({
          title: "金顶日出轻装上行",
          meta: "7.3km · 3h50m · 点位 7 · 收藏 4.8k",
          routeName: "route-detail",
          submeta: `${renderChip("热度高")}${renderChip("视频充足", "chip--alt")}`,
          extraAction: favoriteButton("routes", "north-ridge")
        })}
        ${listCard({
          title: "穿云栈道夜宿线",
          meta: "12.8km · 爬升 980m · 危险提示 2 条",
          routeName: "route-detail",
          submeta: `${renderChip("需经验", "chip--warn")}${renderChip("营地位点")}`
        })}
      </div>
      ${bottomNav("search")}
    `);
  },
  "map-results"() {
    return safe(`
      ${topStatusBar(false, "GPS 精准 82%")}
      <div class="map-card" style="flex:1; min-height:auto;">
        <div class="topbar" style="margin-bottom:0;">
          <button class="nav-icon" data-route-back>←</button>
          <button class="searchbar" style="width:230px; min-height:42px;">⌕ 搜索结果</button>
          <button class="nav-icon">≋</button>
        </div>
        <div class="map-path" style="margin-top:138px;">
          <span class="marker marker--start"></span>
          <span class="marker marker--mid"></span>
          <span class="marker marker--warn"></span>
          <span class="marker marker--end"></span>
        </div>
        <div class="sheet" style="position:absolute; left:18px; right:18px; bottom:18px;">
          <div class="chips" style="margin-bottom:10px;">
            ${renderChip("地图模式")}
            ${renderChip("3 条路线", "chip--alt")}
          </div>
          ${listCard({
            title: "云中草甸东侧折返线",
            meta: "8.6km · 爬升 550m · 风景指数高",
            routeName: "route-detail",
            submeta: `${renderChip("点位 6")}${renderChip("视频 4", "chip--alt")}${renderChip("1.2k 收藏")}`,
            extraAction: favoriteButton("routes", "lake-trail")
          })}
        </div>
      </div>
    `);
  },
  "route-detail"() {
    const routeId = "north-ridge";
    return safe(`
      ${topStatusBar(false, "离线地图已缓存 81%")}
      <div class="hero-card" style="min-height:224px;">
        <div class="section-row">
          <button class="nav-icon nav-icon--inverse" data-route-back>←</button>
          <div class="hero-actions">
            ${favoriteButton("routes", routeId)}
          </div>
        </div>
        <h1 class="hero-card__title" style="font-size:34px; margin-top:58px;">北岭雾海穿越线</h1>
        <p class="hero-card__body">浙江 · 2026.03 发布 · 含 9 个点位，补给与危险提示完整。</p>
      </div>
      <div class="grid-3" style="margin-top:16px;">
        <div class="metric-card"><div class="metric-card__label">里程</div><div class="metric-card__value">11.4</div></div>
        <div class="metric-card"><div class="metric-card__label">时长</div><div class="metric-card__value">5.8</div></div>
        <div class="metric-card"><div class="metric-card__label">爬升</div><div class="metric-card__value">860</div></div>
      </div>
      <div class="user-row" style="margin-top:16px;">
        <div class="avatar">JY</div>
        <div style="flex:1;">
          <div class="list-card__title" style="margin:0;">景野</div>
          <div class="meta-text">发布 21 条路线 · 擅长山脊线与露营点分享</div>
        </div>
        ${favoriteButton("authors", "jingye", "关注")}
      </div>
      <div class="map-card" style="margin-top:16px;">
        <div class="section-row">
          <div class="list-card__title" style="margin:0;">轨迹与点位</div>
          <button class="inline-link" data-route="map-results">全屏地图</button>
        </div>
        <div class="map-path">
          <span class="marker marker--start"></span>
          <span class="marker marker--mid"></span>
          <span class="marker marker--warn"></span>
          <span class="marker marker--end"></span>
        </div>
      </div>
      <div class="timeline" style="margin-top:16px;">
        <button class="timeline-item" data-route="waypoint-detail">
          <div class="timeline-item__icon">景</div>
          <div class="stack-10">
            <div class="list-card__title" style="margin:0;">山脊观景点</div>
            <div class="meta-text">09:42 · 云海出现概率高，建议停留 10 分钟拍摄。</div>
            <div class="media-thumb" style="width:100%;"></div>
          </div>
        </button>
      </div>
      <div class="footer-actions">
        <button class="action-tile" data-favorite-kind="routes" data-favorite-id="${routeId}" data-favorite-title="北岭雾海穿越线">收藏</button>
        <button class="action-tile" data-route="comments">评论</button>
        <button class="action-tile action-tile--primary" data-route="map-results">开始参考</button>
      </div>
    `);
  },
  "author-profile"() {
    return safe(`
      ${topStatusBar(false, "作者主页 83%")}
      ${topbar({ title: "作者", right: "↗" })}
      <div class="sheet stack-14">
        <div class="user-row">
          <div class="avatar avatar--lg">JY</div>
          <div style="flex:1;">
            <div class="list-card__title" style="font-size:20px; margin:0 0 4px;">景野</div>
            <div class="meta-text">擅长高山风景线与轻露营路线分享</div>
          </div>
        </div>
        <div class="grid-3">
          <div class="metric-card"><div class="metric-card__label">发布</div><div class="metric-card__value">21</div></div>
          <div class="metric-card"><div class="metric-card__label">粉丝</div><div class="metric-card__value">8.6k</div></div>
          <div class="metric-card"><div class="metric-card__label">累计收藏</div><div class="metric-card__value">31k</div></div>
        </div>
        <div class="chips">
          ${renderChip("山脊穿越")}
          ${renderChip("营地位点", "chip--alt")}
          ${renderChip("日出机位")}
        </div>
        ${favoriteButton("authors", "jingye", "关注作者")}
      </div>
    `);
  },
  "waypoint-detail"() {
    return safe(`
      ${topStatusBar(false, "点位详情 80%")}
      ${topbar({ title: "点位详情", right: "观景点", rightType: "pill" })}
      <div class="media-thumb" style="width:100%; height:210px; border-radius:26px;"></div>
      <div class="sheet stack-14" style="margin-top:16px;">
        <div class="section-row">
          <div>
            <div class="list-card__title" style="font-size:20px; margin:0 0 8px;">风口观景平台</div>
            <div class="meta-text">10:18 · 海拔 1328m · 路线中段</div>
          </div>
          ${favoriteButton("waypoints", "ridge-viewpoint", "收藏点位")}
        </div>
        <div class="chips">
          ${renderChip("云海概率高")}
          ${renderChip("风大注意保暖", "chip--warn")}
        </div>
        <div class="body-copy">此点位位于主轨迹右侧突出的观景平台，晴天适合拍延时，雨后石面较滑，建议停留拍摄时背包靠内侧放置。</div>
      </div>
    `);
  },
  comments() {
    return safe(`
      ${topStatusBar(false, "评论 128 81%")}
      ${topbar({ title: "路线评论", right: "高价值优先", rightType: "pill" })}
      <div class="sheet stack-12">
        <div class="user-row">
          <div class="avatar">LM</div>
          <div style="flex:1;">
            <div class="list-card__title" style="margin:0 0 4px;">林木</div>
            <div class="meta-text">昨天 18:42</div>
          </div>
          <span class="inline-link">赞 28</span>
        </div>
        <div class="body-copy">雨后第二个风口段碎石有些滑，登山杖非常有用。作者标注的补给点目前仍然营业，买到热水了。</div>
      </div>
      <div class="sheet stack-12" style="margin-top:12px;">
        ${formField({ form: "comment", field: "text", label: "补充路况或体验建议", placeholder: "例如：雨后第二个风口段较滑，建议登山杖", rows: 3 })}
        <button class="button-primary" data-submit="comment">发布评论</button>
      </div>
    `);
  },
  "record-prepare"() {
    return safe(`
      ${topStatusBar(false, "准备出发 87%")}
      ${topbar({ title: "开始记录" })}
      <div class="hero-card hero-card--compact" style="background:
        linear-gradient(180deg, rgba(7, 12, 10, 0.02), rgba(7, 12, 10, 0.46)),
        linear-gradient(140deg, rgba(23, 53, 44, 0.82), rgba(58, 124, 165, 0.34)),
        url('https://images.unsplash.com/photo-1551632811-561732d1e306?auto=format&fit=crop&w=900&q=80') center/cover;">
        <span class="pill pill--inverse">出发前检查</span>
        <h2 class="hero-card__title" style="font-size:34px; margin-top:30px;">让记录轻一点<br/>让分享完整一点</h2>
      </div>
      <div class="sheet stack-14" style="margin-top:18px;">
        ${formField({ form: "recordPrepare", field: "title", label: "路线名称", placeholder: "请输入路线名称" })}
        <div class="field"><span class="field__label">起点位置</span><div class="field__value">自动定位到北岭停车场入口</div></div>
        <div class="grid-2">
          <div class="field"><span class="field__label">公开范围</span><div class="field__value">${state.forms.recordPrepare.visibility}</div></div>
          <div class="field"><span class="field__label">海拔记录</span><div class="field__value">已开启</div></div>
        </div>
        <label class="switch-row">
          <span>离线提醒与本地保存</span>
          <input type="checkbox" ${state.forms.recordPrepare.offlineReminder ? "checked" : ""} data-form-toggle="recordPrepare" data-field="offlineReminder" />
        </label>
        <button class="button-primary" data-submit="recordPrepare">开始记录</button>
      </div>
    `);
  },
  "record-live"() {
    const statusLabel = {
      idle: "待开始",
      recording: `记录中 ${state.recording.elapsed}`,
      paused: `已暂停 ${state.recording.elapsed}`,
      ended: "已结束"
    }[state.recording.status];
    const rightBadge =
      state.recording.status === "recording"
        ? `<span class="stat-badge"><span class="pulse"></span><span>REC</span></span><span>79%</span>`
        : `${state.recording.status === "paused" ? "PAUSED" : "READY"} 79%`;
    return safe(`
      ${topStatusBar(false, rightBadge)}
      <div class="topbar">
        <button class="nav-icon" data-route-back>←</button>
        <span class="pill" style="${state.recording.status === "recording" ? "background:rgba(190,77,63,0.12); color:var(--color-feedback-error);" : ""}">${statusLabel}</span>
        <button class="nav-icon" data-route="record-prepare">⋯</button>
      </div>
      <div class="map-card">
        <div class="section-row">
          <span class="pill">GPS 精度良好</span>
          <span class="pill">${state.forms.recordPrepare.offlineReminder ? "离线保护开启" : "离线保护关闭"}</span>
        </div>
        <div class="map-path" style="margin-top:104px;">
          <span class="marker marker--start"></span>
          <span class="marker marker--mid"></span>
          <span class="marker marker--warn"></span>
          <span class="marker marker--end"></span>
        </div>
      </div>
      <div class="grid-2" style="margin-top:16px;">
        <div class="metric-card"><div class="metric-card__label">已走距离</div><div class="metric-card__value">${state.recording.distanceKm.toFixed(1)}</div></div>
        <div class="metric-card"><div class="metric-card__label">累计爬升</div><div class="metric-card__value">${state.recording.ascentM}</div></div>
        <div class="metric-card"><div class="metric-card__label">当前海拔</div><div class="metric-card__value">${state.recording.altitudeM}</div></div>
        <div class="metric-card"><div class="metric-card__label">记录点位</div><div class="metric-card__value">${state.recording.waypointCount}</div></div>
      </div>
      <div class="toolbar-floating" style="margin-top:16px;">
        <button class="toolbar-floating__item" data-route="add-waypoint"><span class="toolbar-floating__glyph">⊕</span><span>点位</span></button>
        <button class="toolbar-floating__item" data-record-action="pause"><span class="toolbar-floating__glyph">Ⅱ</span><span>${state.recording.status === "paused" ? "继续" : "暂停"}</span></button>
        <button class="toolbar-floating__item" data-record-action="end"><span class="toolbar-floating__glyph">■</span><span>结束</span></button>
      </div>
      <div class="sheet" style="margin-top:16px;">
        <div class="section-row">
          <div class="list-card__title" style="margin:0;">最近新增点位</div>
          <button class="inline-link" data-route="add-waypoint">全部查看</button>
        </div>
        ${listCard({
          title: "溪边补给点",
          meta: "09:18 · 已附 2 张照片 · 描述可稍后补全",
          routeName: "add-waypoint"
        })}
      </div>
      ${bottomNav("record-live")}
    `);
  },
  "camera-capture"() {
    return `
      <div class="screen is-camera">
        <div class="safe-area">
          ${topStatusBar(true, "HDR 77%")}
          <div class="camera-stage">
            <div class="camera-stage__controls">
              <div class="camera-stage__row">
                <button class="nav-icon nav-icon--inverse" data-route-back>×</button>
                <span class="pill pill--inverse">附加到当前点位</span>
                <button class="nav-icon nav-icon--inverse">↺</button>
              </div>
            </div>
            <div class="camera-stage__bottom">
              <div class="camera-stage__row">
                <div class="media-thumb"></div>
                <div class="chips">
                  <span class="chip" style="background:rgba(255,255,255,0.16); color:white;">照片</span>
                  <span class="chip" style="background:rgba(255,255,255,0.08); color:rgba(255,255,255,0.76);">视频</span>
                </div>
                <button class="nav-icon nav-icon--inverse">⚡</button>
              </div>
              <button class="shutter" data-capture-waypoint></button>
              <div class="meta-text" style="color:rgba(255,255,255,0.78); text-align:center;">拍摄后自动进入点位补充页，你可以稍后再写描述。</div>
            </div>
          </div>
        </div>
        <button class="fab" data-open-drawer>页</button>
        ${pageSwitcherDrawer()}
        ${renderModal()}
        ${renderToast()}
      </div>
    `;
  },
  "add-waypoint"() {
    const primaryTypes = [
      renderWaypointTypeButton("观景点"),
      renderWaypointTypeButton("补给点", "chip--alt"),
      renderWaypointTypeButton("危险提醒", "chip--warn")
    ];
    const extraTypes = state.forms.waypoint.expandedTypes
      ? [
          renderWaypointTypeButton("起点"),
          renderWaypointTypeButton("终点"),
          renderWaypointTypeButton("岔路口"),
          renderWaypointTypeButton("休息点"),
          renderWaypointTypeButton("营地点", "chip--alt"),
          renderWaypointTypeButton("拍摄机位")
        ].join("")
      : "";
    return safe(`
      ${topStatusBar(false, "草稿已保存 78%")}
      ${topbar({ title: "新增点位" })}
      <div class="media-row">
        <div class="media-thumb"></div>
        <div class="media-thumb" style="background:linear-gradient(180deg, rgba(17, 23, 20, 0.04), rgba(17, 23, 20, 0.28)), url('https://images.unsplash.com/photo-1464820453369-31d2c0b651af?auto=format&fit=crop&w=500&q=80') center/cover;"></div>
        <button class="media-thumb" style="display:grid; place-items:center; background:rgba(40,84,67,0.08); color:var(--color-brand-primary); font-weight:700;">+ 视频</button>
      </div>
      <div class="grid-2" style="margin-top:12px;">
        <button class="button-secondary" data-route="camera-capture">拍照添加</button>
        <button class="button-secondary" data-route="camera-capture">录像添加</button>
      </div>
      <div class="sheet stack-14" style="margin-top:16px;">
        <div class="field">
          <span class="field__label">点位类型</span>
          <div class="chips">
            ${primaryTypes.join("")}
            <button class="chip chip--more" data-waypoint-expand>${state.forms.waypoint.expandedTypes ? "收起类型" : "展开更多"}</button>
          </div>
          ${state.forms.waypoint.expandedTypes ? `<div class="chips" style="margin-top:10px;">${extraTypes}</div>` : ""}
        </div>
        ${formField({ form: "waypoint", field: "title", label: "标题", placeholder: "例如：山脊观景平台" })}
        ${formField({ form: "waypoint", field: "description", label: "描述", placeholder: "补充为什么值得停留，或提醒后来者注意事项", rows: 3 })}
        <div class="grid-2">
          <div class="field"><span class="field__label">时间</span><div class="field__value">10:18</div></div>
          <div class="field"><span class="field__label">海拔</span><div class="field__value">1328m</div></div>
        </div>
        <button class="button-primary" data-submit="waypoint">保存点位</button>
      </div>
    `);
  },
  "route-edit"() {
    return safe(`
      ${topStatusBar(false, "草稿保存于刚刚 76%")}
      ${topbar({ title: "整理路线", right: "预览", rightType: "pill" })}
      ${listCard({
        title: "北岭雾海穿越线",
        meta: "11.4km · 5h48m · 点位 9 个",
        submeta: `${renderChip("封面已选择")}${renderChip("轨迹完整", "chip--alt")}`
      })}
      <div class="sheet stack-12" style="margin-top:14px;">
        <div class="field"><span class="field__label">路线简介</span><div class="field__value">这是一条适合春季清晨出发的山脊雾海路线，中段风口较大，建议带防风层。</div></div>
        <div class="grid-2">
          <div class="field"><span class="field__label">难度</span><div class="field__value">中等</div></div>
          <div class="field"><span class="field__label">最佳季节</span><div class="field__value">3-5 月</div></div>
        </div>
      </div>
      <button class="button-primary" style="margin-top:16px;" data-route="publish-preview">进入发布预览</button>
    `);
  },
  "publish-preview"() {
    return safe(`
      ${topStatusBar(false, "发布前检查 75%")}
      ${topbar({ title: "发布预览", right: "已完整 92%", rightType: "pill" })}
      ${listCard({
        title: "北岭雾海穿越线",
        meta: "11.4km · 5h48m · 点位 9 · 媒体 17",
        submeta: `${renderChip("预览模式")}${renderChip("接近用户展示", "chip--alt")}`
      })}
      <div class="map-card" style="margin-top:14px;">
        <div class="section-row">
          <div class="list-card__title" style="margin:0;">路线摘要</div>
          <span class="inline-link">编辑封面</span>
        </div>
        <div class="map-path">
          <span class="marker marker--start"></span>
          <span class="marker marker--mid"></span>
          <span class="marker marker--warn"></span>
          <span class="marker marker--end"></span>
        </div>
      </div>
      <div class="sheet stack-12" style="margin-top:14px;">
        <div class="list-card__title" style="margin:0;">发布前建议</div>
        <div class="meta-text">安全提醒已填写，但“装备建议”仍可更具体。当前有 1 个点位描述较短，可考虑补充。</div>
      </div>
      <div class="footer-actions">
        <button class="action-tile" data-route="route-edit">返回编辑</button>
        <button class="action-tile">存草稿</button>
        <button class="action-tile action-tile--primary" data-route="publish-success">确认发布</button>
      </div>
    `);
  },
  "publish-success"() {
    return safe(`
      <div style="flex:1; display:grid; place-items:center;">
        <div class="sheet stack-16" style="text-align:center; width:100%;">
          <div style="width:96px; height:96px; margin:0 auto; display:grid; place-items:center; border-radius:28px; color:var(--color-brand-primary); font-size:42px; background:linear-gradient(180deg, rgba(61,122,97,0.18), rgba(23,53,44,0.2));">✓</div>
          <h1 class="section-title">路线已发布</h1>
          <p class="body-copy">你的记录已整理成可被搜索、收藏和参考的路线内容。</p>
          <div class="grid-2">
            <div class="metric-card"><div class="metric-card__label">点位</div><div class="metric-card__value">9</div></div>
            <div class="metric-card"><div class="metric-card__label">媒体</div><div class="metric-card__value">17</div></div>
          </div>
          <button class="button-primary" data-route="route-detail">查看路线详情</button>
          <button class="button-secondary" data-route="discover">分享给朋友</button>
          <button class="button-ghost" data-route="record-prepare">继续记录新路线</button>
        </div>
      </div>
    `);
  },
  profile() {
    const profile = state.forms.profileEdit;
    return safe(`
      ${topStatusBar(false, "同步成功 85%")}
      <div class="topbar">
        <h1 class="section-title">我的</h1>
        <button class="nav-icon" data-route="settings">⚙</button>
      </div>
      <div class="sheet stack-14">
        <div class="user-row">
          <div class="avatar avatar--lg">JY</div>
          <div style="flex:1;">
            <div class="list-card__title" style="font-size:20px; margin:0 0 6px;">${profile.nickname}</div>
            <div class="meta-text">${profile.bio}</div>
          </div>
          <button class="button-secondary" data-route="profile-edit">编辑</button>
        </div>
        <div class="grid-3">
          <div class="metric-card"><div class="metric-card__label">发布路线</div><div class="metric-card__value">21</div></div>
          <div class="metric-card"><div class="metric-card__label">累计里程</div><div class="metric-card__value">486</div></div>
          <div class="metric-card"><div class="metric-card__label">收藏</div><div class="metric-card__value">${state.favorites.routes.size + state.favorites.waypoints.size}</div></div>
        </div>
      </div>
      <div class="sheet stack-12" style="margin-top:14px;">
        <div class="section-row">
          <h2 class="section-title section-title--sm">我的路线</h2>
          <button class="inline-link" data-route="my-routes">全部</button>
        </div>
        ${listCard({
          title: "北岭雾海穿越线",
          meta: "已发布 · 收藏 1.4k · 评论 86",
          routeName: "route-detail"
        })}
        ${listCard({
          title: "未完成草稿：西岭雪线",
          meta: "上次编辑于昨天 · 还有 2 个点位待补充",
          routeName: "my-routes"
        })}
      </div>
      <div class="grid-2" style="margin-top:14px;">
        <button class="action-tile" data-route="my-routes">草稿箱</button>
        <button class="action-tile" data-route="favorites">收藏夹</button>
      </div>
      ${bottomNav("profile")}
    `);
  },
  "profile-edit"() {
    return safe(`
      ${topStatusBar(false, "编辑资料 86%")}
      ${topbar({ title: "编辑个人资料", right: "资料", rightType: "pill" })}
      <div class="sheet stack-14">
        <div class="user-row">
          <div class="avatar avatar--lg">JY</div>
          <div style="flex:1;">
            <div class="list-card__title" style="font-size:18px; margin:0 0 6px;">头像与昵称</div>
            <div class="meta-text">当前先以原型结构承载，后续可继续接入头像上传与裁剪。</div>
          </div>
        </div>
        ${formField({ form: "profileEdit", field: "nickname", label: "昵称", placeholder: "请输入昵称" })}
        ${formField({ form: "profileEdit", field: "bio", label: "个人简介", placeholder: "介绍你的徒步偏好与路线风格", rows: 3 })}
        <div class="grid-2">
          ${formField({ form: "profileEdit", field: "city", label: "常驻城市", placeholder: "请输入城市" })}
          ${formField({ form: "profileEdit", field: "level", label: "经验等级", placeholder: "例如：轻中度徒步" })}
        </div>
        <div class="field">
          <span class="field__label">兴趣标签</span>
          <div class="chips">
            ${renderSelectableChip("profileEdit", "interests", "山脊线")}
            ${renderSelectableChip("profileEdit", "interests", "湖边营地", "chip--alt")}
            ${renderSelectableChip("profileEdit", "interests", "日出路线")}
            ${renderSelectableChip("profileEdit", "interests", "新手友好", "chip--warn")}
            ${renderSelectableChip("profileEdit", "interests", "露营过夜")}
            ${renderSelectableChip("profileEdit", "interests", "摄影机位", "chip--alt")}
          </div>
        </div>
        <button class="button-primary" data-submit="profileEdit">保存资料</button>
      </div>
      <div class="sheet stack-12" style="margin-top:14px;">
        <div class="list-card__title" style="margin:0;">保存后影响</div>
        <div class="meta-text">会同步更新“我的”页展示内容，并作为后续推荐和作者主页的基础信息。</div>
      </div>
    `);
  },
  "my-routes"() {
    return safe(`
      ${topStatusBar(false, "我的路线 84%")}
      ${topbar({ title: "我的路线", right: "＋" })}
      <div class="chips">
        ${renderChip("已发布")}
        ${renderChip("草稿", "chip--alt")}
        ${renderChip("私密")}
      </div>
      <div class="stack-12" style="margin-top:14px;">
        ${listCard({
          title: "北岭雾海穿越线",
          meta: "已发布 · 收藏 1.4k · 更新于 2 天前",
          routeName: "route-detail",
          submeta: `${renderChip("查看数据")}${renderChip("再次编辑", "chip--alt")}`
        })}
        ${listCard({
          title: "西岭雪线草稿",
          meta: "未发布 · 缺少封面和 2 个点位描述",
          routeName: "route-edit",
          submeta: `${renderChip("继续编辑", "chip--warn")}${renderChip("删除")}`
        })}
      </div>
    `);
  },
  favorites() {
    return safe(`
      ${topStatusBar(false, "收藏夹 82%")}
      ${topbar({ title: "收藏夹", right: `共 ${state.favorites.routes.size + state.favorites.authors.size + state.favorites.waypoints.size} 项`, rightType: "pill" })}
      <div class="chips">
        ${renderChip("路线")}
        ${renderChip("点位", "chip--alt")}
        ${renderChip("作者")}
      </div>
      <div class="stack-12" style="margin-top:14px;">
        ${state.favorites.routes.size ? listCard({
          title: "北岭雾海穿越线",
          meta: "收藏于今天 · 点位 9 · 评论 86",
          routeName: "route-detail",
          extraAction: favoriteButton("routes", "north-ridge")
        }) : `<div class="sheet"><div class="meta-text">目前还没有收藏路线。</div></div>`}
        ${state.favorites.waypoints.size ? `
          <button class="list-card" data-route="waypoint-detail">
            <div class="timeline-item__icon">景</div>
            <div class="list-card__body">
              <div class="list-card__title">风口观景平台</div>
              <div class="meta-text">收藏于路线“北岭雾海穿越线” · 视频 1 段</div>
              <div style="margin-top:10px;">${favoriteButton("waypoints", "ridge-viewpoint", "收藏点位")}</div>
            </div>
          </button>` : ""}
        ${state.favorites.authors.size ? `
          <button class="list-card" data-route="author-profile">
            <div class="avatar">JY</div>
            <div class="list-card__body">
              <div class="list-card__title">景野</div>
              <div class="meta-text">擅长山脊线、露营点和日出机位路线</div>
              <div style="margin-top:10px;">${favoriteButton("authors", "jingye", "关注作者")}</div>
            </div>
          </button>` : ""}
      </div>
    `);
  },
  settings() {
    return safe(`
      ${topStatusBar(false, "设置 80%")}
      ${topbar({ title: "设置", right: "?" })}
      <div class="sheet stack-12">
        <div class="list-card__title" style="margin:0;">账号与安全</div>
        <button class="list-card" data-route="forgot-password">
          <div class="timeline-item__icon">密</div>
          <div class="list-card__body">
            <div class="list-card__title">修改密码</div>
            <div class="meta-text">最近更新于 30 天前</div>
          </div>
        </button>
      </div>
      <button class="button-secondary" style="margin-top:16px;" data-route="login">退出登录</button>
    `);
  }
};

function renderDesktopNav(container, items) {
  const current = route();
  container.innerHTML = items
    .map(
      (item) => `
        <button class="${item.route === current ? "is-active" : ""}" data-route="${item.route}">
          ${item.label}
        </button>
      `
    )
    .join("");
}

function render() {
  const current = route();
  const screen = screens[current] || screens.welcome;
  app.innerHTML = screen();
  renderDesktopNav(desktopCoreNav, CORE_NAV);
  renderDesktopNav(desktopSupportNav, SUPPORT_NAV);
}

function handleFavoriteToggle(kind, id, title = "") {
  const collection = state.favorites[kind];
  const active = collection.has(id);
  if (active && kind === "routes") {
    openModal("favorite-route", { id, title: title || routeMeta[id]?.title || "当前路线" });
    return;
  }

  if (active) {
    collection.delete(id);
    setToast("已取消收藏");
  } else {
    collection.add(id);
    setToast("已加入收藏");
  }
  render();
}

function submitLogin() {
  const valid = validateForm("login", ["account", "password"]);
  render();
  if (!valid) return;
  setToast("登录成功");
  navigate("discover");
}

function submitRegister() {
  const valid = validateForm("register", ["nickname", "account", "password", "confirmPassword"]);
  if (!state.forms.register.agreed) {
    setToast("请先同意服务协议");
    render();
    return;
  }
  render();
  if (!valid) return;
  setToast("注册成功");
  navigate("complete-profile");
}

function submitRecordPrepare() {
  const valid = validateForm("recordPrepare", ["title"]);
  render();
  if (!valid) return;
  setRecordingStatus("recording");
  navigate("record-live");
}

function submitWaypoint() {
  const valid = validateForm("waypoint", ["title", "description"]);
  render();
  if (!valid) return;
  state.recording.waypointCount += 1;
  state.recording.pendingWaypoints = Math.max(0, state.recording.pendingWaypoints - 1);
  state.forms.waypoint.title = "";
  state.forms.waypoint.description = "";
  state.forms.waypoint.touched = {};
  setToast("点位已保存");
  navigate("record-live");
}

function submitComment() {
  const valid = validateForm("comment", ["text"]);
  render();
  if (!valid) return;
  state.forms.comment.text = "";
  state.forms.comment.touched = {};
  openModal("comment-success");
}

function submitProfileEdit() {
  const valid = validateForm("profileEdit", ["nickname", "bio", "city", "level"]);
  render();
  if (!valid) return;
  setToast("个人资料已更新");
  navigate("profile");
}

function onRecordAction(action) {
  if (action === "pause") {
    if (state.recording.status === "paused") {
      setRecordingStatus("recording");
      render();
      return;
    }
    openModal("record-pause");
    return;
  }

  if (action === "end") {
    openModal("record-stop");
  }
}

function captureWaypoint() {
  state.forms.waypoint.title = "新拍摄点位";
  state.forms.waypoint.description = "";
  state.forms.waypoint.touched = {};
  state.recording.pendingWaypoints += 1;
  setToast("媒体已挂载到点位草稿");
  navigate("add-waypoint");
}

document.addEventListener("input", (event) => {
  const field = event.target.closest("[data-form][data-field]");
  if (!field) return;
  const { form, field: fieldName } = field.dataset;
  state.forms[form][fieldName] = field.value;
  render();
});

document.addEventListener("change", (event) => {
  const toggle = event.target.closest("[data-form-toggle][data-field]");
  if (!toggle) return;
  const { formToggle, field } = toggle.dataset;
  state.forms[formToggle][field] = toggle.checked;
  render();
});

document.addEventListener("blur", (event) => {
  const field = event.target.closest("[data-form][data-field]");
  if (!field) return;
  const { form, field: fieldName } = field.dataset;
  state.forms[form].touched[fieldName] = true;
  render();
}, true);

document.addEventListener("click", (event) => {
  const routeTarget = event.target.closest("[data-route]");
  if (routeTarget) {
    state.drawerOpen = false;
    navigate(routeTarget.dataset.route);
    return;
  }

  const backTarget = event.target.closest("[data-route-back]");
  if (backTarget) {
    state.drawerOpen = false;
    window.history.length > 1 ? window.history.back() : navigate("discover");
    return;
  }

  if (event.target.closest("[data-open-drawer]")) {
    state.drawerOpen = true;
    render();
    return;
  }

  if (event.target.closest("[data-close-drawer]") || event.target.closest("[data-modal-close]")) {
    state.drawerOpen = false;
    closeModal();
    return;
  }

  const favoriteTarget = event.target.closest("[data-favorite-kind][data-favorite-id]");
  if (favoriteTarget) {
    handleFavoriteToggle(
      favoriteTarget.dataset.favoriteKind,
      favoriteTarget.dataset.favoriteId,
      favoriteTarget.dataset.favoriteTitle || ""
    );
    return;
  }

  const submitTarget = event.target.closest("[data-submit]");
  if (submitTarget) {
    const submitMap = {
      login: submitLogin,
      register: submitRegister,
      recordPrepare: submitRecordPrepare,
      waypoint: submitWaypoint,
      comment: submitComment,
      profileEdit: submitProfileEdit
    };
    submitMap[submitTarget.dataset.submit]?.();
    return;
  }

  const recordAction = event.target.closest("[data-record-action]");
  if (recordAction) {
    onRecordAction(recordAction.dataset.recordAction);
    return;
  }

  const recordTransition = event.target.closest("[data-record-transition]");
  if (recordTransition) {
    setRecordingStatus(recordTransition.dataset.recordTransition);
    closeModal();
    if (recordTransition.dataset.route) navigate(recordTransition.dataset.route);
    return;
  }

  const waypointType = event.target.closest("[data-waypoint-type]");
  if (waypointType) {
    state.forms.waypoint.type = waypointType.dataset.waypointType;
    render();
    return;
  }

  if (event.target.closest("[data-waypoint-expand]")) {
    state.forms.waypoint.expandedTypes = !state.forms.waypoint.expandedTypes;
    render();
    return;
  }

  if (event.target.closest("[data-capture-waypoint]")) {
    captureWaypoint();
    return;
  }

  const selectableChip = event.target.closest("[data-selectable-chip][data-chip-form][data-chip-field]");
  if (selectableChip) {
    const { chipForm, chipField, selectableChip: value } = selectableChip.dataset;
    const list = state.forms[chipForm][chipField];
    const index = list.indexOf(value);
    if (index >= 0) {
      list.splice(index, 1);
    } else {
      list.push(value);
    }
    render();
    return;
  }

  const unfavorite = event.target.closest("[data-confirm-unfavorite]");
  if (unfavorite) {
    state.favorites.routes.delete(unfavorite.dataset.confirmUnfavorite);
    setToast("已从收藏夹移除");
    closeModal();
    return;
  }
});

window.addEventListener("hashchange", render);
window.addEventListener("DOMContentLoaded", () => {
  updateRecordingMetrics("idle");
  render();
});

render();
