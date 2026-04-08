const screens = {
  welcome: {
    title: "欢迎页",
    subtitle: "用自然探索感开场，建立品牌辨识度，并将“记录”与“参考路线”两个核心价值同时说清楚。",
    tags: ["Shared iOS / Android", "Brand Landing", "Outdoor Editorial"],
    notes: [
      "大图 + 地形线纹理建立沉浸感，避免默认应用商店模板式欢迎页。",
      "主按钮强调开始探索，次按钮承接登录，适合新用户首次进入。",
      "底部展示路线数据和真实内容暗示，让用户知道这不是纯社交晒图产品。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-18">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>5G</span><span>88%</span></div>
          </div>
          <div class="hero-card">
            <span class="capsule">TRAILNOTE</span>
            <h1>记录每一步<br/>也分享给后来者</h1>
            <p>徒步轨迹、关键点位、照片与视频统一沉淀，让路线不只是一条线。</p>
          </div>
          <div class="stack-12">
            <button class="button-primary">开始探索</button>
            <button class="button-secondary">已有账号，去登录</button>
          </div>
          <div class="sheet stack-14">
            <div class="section-header">
              <h3 class="section-title">今天适合出发</h3>
              <span class="small-link">查看附近</span>
            </div>
            <div class="dual-grid">
              <div class="metric-card">
                <div class="metric-label">热门路线</div>
                <div class="metric-value">148<span class="metric-unit">条</span></div>
              </div>
              <div class="metric-card">
                <div class="metric-label">新增点位</div>
                <div class="metric-value">624<span class="metric-unit">个</span></div>
              </div>
            </div>
            <div class="list-card">
              <div class="list-thumb"></div>
              <div class="grow stack-10">
                <div>
                  <div class="route-title">龙脊东线晨雾路线</div>
                  <div class="tiny">8.2km · 4h20m · 观景点 6 个</div>
                </div>
                <div class="chips">
                  <span class="chip">新手友好</span>
                  <span class="chip alt">视频路线</span>
                </div>
              </div>
            </div>
          </div>
          <div class="spacer"></div>
        </div>
      </div>
    `
  },
  login: {
    title: "登录页",
    subtitle: "采用偏克制的工具型表单，不使用 Material 浮动标签，减少视觉噪音并提升输入稳定感。",
    tags: ["Authentication", "Form System", "Minimal Utility"],
    notes: [
      "固定顶部标题标签更适合中文字段长度，也更利于弱网和系统字体放大场景。",
      "第三方登录区域被处理为次层，不抢占主任务。",
      "背景保留极弱品牌纹理，让认证页仍属于同一产品宇宙。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-20">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>5G</span><span>89%</span></div>
          </div>
          <div class="topbar">
            <div class="nav-icon">←</div>
            <span class="capsule">账号安全</span>
          </div>
          <div>
            <div class="section-title" style="font-size:40px;">欢迎回来</div>
            <p class="tiny" style="font-size:14px; margin-top:8px;">继续记录你的路线，或者查看最近收藏的徒步目的地。</p>
          </div>
          <div class="form-card sheet stack-16">
            <div class="field">
              <span class="field-label">手机号 / 邮箱</span>
              <div class="field-value">hiker@trailnote.app</div>
            </div>
            <div class="field">
              <span class="field-label">密码</span>
              <div class="field-value">••••••••••</div>
            </div>
            <div class="section-header">
              <span class="tiny">已阅读并同意服务协议</span>
              <span class="small-link">忘记密码</span>
            </div>
            <button class="button-primary">登录</button>
          </div>
          <div class="sheet stack-14">
            <div class="section-header">
              <span class="tiny">其他登录方式</span>
              <span class="small-link">帮助</span>
            </div>
            <div class="dual-grid">
              <button class="button-secondary">Apple</button>
              <button class="button-secondary">微信</button>
            </div>
          </div>
          <div class="spacer"></div>
          <div class="tiny" style="text-align:center;">还没有账号？<span class="emphasis">立即注册</span></div>
        </div>
      </div>
    `
  },
  register: {
    title: "注册页",
    subtitle: "注册流程强调轻量与可信，字段够用即可，成功后再进入完善资料与兴趣偏好。",
    tags: ["Authentication", "Progressive Disclosure", "Shared Form Pattern"],
    notes: [
      "把协议勾选、密码规则、创建按钮放在同一区域，降低扫视成本。",
      "用浅色卡片与自然色强调，避免过度“金融化”或“社交娱乐化”。",
      "推荐将邀请码作为可选字段，不阻塞首个关键转化动作。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-18">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>5G</span><span>90%</span></div>
          </div>
          <div class="topbar">
            <div class="nav-icon">←</div>
            <div class="topbar-title">创建账号</div>
            <div class="capsule">1 / 2</div>
          </div>
          <div class="hero-card" style="min-height:170px; background:
            linear-gradient(180deg, rgba(7, 12, 10, 0.02), rgba(7, 12, 10, 0.46)),
            linear-gradient(140deg, rgba(23, 53, 44, 0.8), rgba(197, 157, 95, 0.38)),
            url('https://images.unsplash.com/photo-1501554728187-ce583db33af7?auto=format&fit=crop&w=900&q=80') center/cover;">
            <span class="capsule" style="background:rgba(255,255,255,0.14); color:white;">NEW HIKER</span>
            <h1 style="font-size:36px; margin-top:26px;">把真实路线<br/>沉淀成可参考的经验</h1>
          </div>
          <div class="sheet stack-14">
            <div class="field"><span class="field-label">昵称</span><div class="field-value">山野风</div></div>
            <div class="field"><span class="field-label">手机号 / 邮箱</span><div class="field-value">yourname@example.com</div></div>
            <div class="field"><span class="field-label">密码</span><div class="field-value">至少 8 位，包含数字</div></div>
            <div class="field"><span class="field-label">确认密码</span><div class="field-value">再次输入密码</div></div>
            <div class="tiny">注册即表示你同意 TrailNote 服务协议与隐私政策。</div>
            <button class="button-primary">注册并继续</button>
          </div>
          <div class="spacer"></div>
        </div>
      </div>
    `
  },
  discover_home: {
    title: "发现首页",
    subtitle: "把路线推荐、附近可去、作者内容和搜索起点放在同一个沉浸但高效率的首页里。",
    tags: ["Discovery", "Content First", "Editorial + Utility"],
    notes: [
      "顶部搜索条与天气入口服务户外决策，不只是内容浏览。",
      "推荐卡强调真实数据与点位数量，帮助用户快速判断内容质量。",
      "底部导航中间“记录”突出，但整体仍维持克制的工具感。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-16">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>定位已开</span><span>86%</span></div>
          </div>
          <div class="topbar">
            <div>
              <div class="tiny">上海 · 12°C · 云层薄</div>
              <div class="section-title" style="font-size:34px;">发现路线</div>
            </div>
            <div class="nav-icon">◎</div>
          </div>
          <div class="searchbar">⌕ 搜索路线、点位、作者</div>
          <div class="hero-card">
            <span class="capsule" style="background:rgba(255,255,255,0.14); color:white;">今日热门</span>
            <h1 style="font-size:34px;">雾线谷地<br/>晨光穿越</h1>
            <p>4 位分享者共同标注了 12 个关键点位，含 8 段视频记录与补给提醒。</p>
          </div>
          <div class="section-header">
            <h3 class="section-title">周边可去</h3>
            <span class="small-link">查看全部</span>
          </div>
          <div class="dual-grid">
            <div class="route-card">
              <div class="route-cover"></div>
              <div class="route-title">环湖松林轻徒步</div>
              <div class="route-meta">6.4km · 爬升 320m · 新手可走</div>
              <div class="route-stats"><span>收藏 2.1k</span><span>点位 5</span></div>
            </div>
            <div class="route-card">
              <div class="route-cover" style="background:
                linear-gradient(180deg, rgba(17, 23, 20, 0.04), rgba(17, 23, 20, 0.46)),
                url('https://images.unsplash.com/photo-1464822759844-d150ad6d0f50?auto=format&fit=crop&w=900&q=80') center/cover;"></div>
              <div class="route-title">岩壁风口观景线</div>
              <div class="route-meta">9.8km · 爬升 690m · 经验者适合</div>
              <div class="route-stats"><span>视频 14</span><span>关注 981</span></div>
            </div>
          </div>
          <div class="bottom-nav">
            <div class="nav-item active"><div class="nav-glyph">⌂</div><span>发现</span></div>
            <div class="nav-item"><div class="nav-glyph">⌕</div><span>搜索</span></div>
            <div class="nav-item active"><div class="nav-glyph">◎</div><span>记录</span></div>
            <div class="nav-item"><div class="nav-glyph">◌</div><span>我的</span></div>
          </div>
        </div>
      </div>
    `
  },
  search: {
    title: "搜索页",
    subtitle: "搜索同时支持路线、点位、作者三类对象，并让地图模式成为随时可切换的结果视角。",
    tags: ["Search", "Filter System", "Map Toggle"],
    notes: [
      "默认态先承接最近搜索和热门目的地，避免空白页。",
      "结果页的筛选标签用自然色编码，避免廉价标签墙观感。",
      "列表与地图双视图并存，适合路线探索这种强地理属性内容。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-16">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>在线</span><span>84%</span></div>
          </div>
          <div class="searchbar">⌕ 搜索 “武功山”</div>
          <div class="chips">
            <span class="chip">路线</span>
            <span class="chip alt">点位</span>
            <span class="chip">作者</span>
            <span class="chip warn">地图模式</span>
          </div>
          <div class="chips">
            <span class="chip">8km 以内</span>
            <span class="chip">新手友好</span>
            <span class="chip alt">有补给</span>
            <span class="chip">春季推荐</span>
          </div>
          <div class="stack-12">
            <div class="list-card">
              <div class="list-thumb"></div>
              <div class="grow stack-10">
                <div class="route-title">金顶日出轻装上行</div>
                <div class="tiny">7.3km · 3h50m · 点位 7 · 收藏 4.8k</div>
                <div class="chips"><span class="chip">热度高</span><span class="chip alt">视频充足</span></div>
              </div>
            </div>
            <div class="list-card">
              <div class="list-thumb" style="background:
                linear-gradient(180deg, rgba(17, 23, 20, 0.02), rgba(17, 23, 20, 0.24)),
                url('https://images.unsplash.com/photo-1465311530779-5241f5a29892?auto=format&fit=crop&w=500&q=80') center/cover;"></div>
              <div class="grow stack-10">
                <div class="route-title">穿云栈道夜宿线</div>
                <div class="tiny">12.8km · 爬升 980m · 危险提示 2 条</div>
                <div class="chips"><span class="chip warn">需经验</span><span class="chip">营地位点</span></div>
              </div>
            </div>
          </div>
          <div class="sheet stack-12">
            <div class="section-header">
              <h3 class="section-title">热门目的地</h3>
              <span class="small-link">更多</span>
            </div>
            <div class="chips">
              <span class="chip">四姑娘山</span>
              <span class="chip">雨崩</span>
              <span class="chip alt">太白山</span>
              <span class="chip">长白山</span>
            </div>
          </div>
          <div class="bottom-nav">
            <div class="nav-item"><div class="nav-glyph">⌂</div><span>发现</span></div>
            <div class="nav-item active"><div class="nav-glyph">⌕</div><span>搜索</span></div>
            <div class="nav-item"><div class="nav-glyph">◎</div><span>记录</span></div>
            <div class="nav-item"><div class="nav-glyph">◌</div><span>我的</span></div>
          </div>
        </div>
      </div>
    `
  },
  map_results: {
    title: "地图结果模式",
    subtitle: "全屏地图承载路线探索，底部结果卡与 marker 联动，适合做出发前比选。",
    tags: ["Map Exploration", "Route Comparison", "Geo-linked UI"],
    notes: [
      "地图并非单独页面，而是搜索结果的重要视角，因此保留搜索和筛选上下文。",
      "底部卡片滚动时高亮对应路线，减少地图点位认知负担。",
      "地图底板采用更淡的等高线纹理，保持主题统一。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>GPS 精准</span><span>82%</span></div>
          </div>
          <div class="map-panel" style="flex:1; min-height:auto;">
            <div class="map-header">
              <div class="nav-icon">←</div>
              <div class="searchbar" style="width:240px; height:42px;">⌕ 搜索结果</div>
              <div class="nav-icon">≋</div>
            </div>
            <div class="map-path" style="margin-top:128px;">
              <span class="marker start"></span>
              <span class="marker mid"></span>
              <span class="marker warn"></span>
              <span class="marker end"></span>
            </div>
            <div class="sheet" style="position:absolute; left:18px; right:18px; bottom:18px;">
              <div class="chips" style="margin-bottom:10px;">
                <span class="chip">地图模式</span>
                <span class="chip alt">3 条路线</span>
              </div>
              <div class="list-card" style="padding:0; background:transparent; border:none;">
                <div class="list-thumb" style="width:92px; height:92px;"></div>
                <div class="grow stack-10">
                  <div class="route-title">云中草甸东侧折返线</div>
                  <div class="tiny">8.6km · 爬升 550m · 风景指数高</div>
                  <div class="route-stats"><span>点位 6</span><span>视频 4</span><span>1.2k 收藏</span></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    `
  },
  route_detail: {
    title: "路线详情页",
    subtitle: "最核心的消费页，把封面、关键数据、地图轨迹、点位时间线和媒体内容串成一个可信的参考链。",
    tags: ["Detail Screen", "Trust Building", "Media + Map"],
    notes: [
      "路线价值判断需要首屏即看到难度、里程、爬升等关键数据。",
      "点位时间线是与普通地图产品拉开差异的核心模块。",
      "底部固定操作保留收藏、分享和开始参考，支持快速进入决策动作。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-16">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>离线地图已缓存</span><span>81%</span></div>
          </div>
          <div class="hero-card" style="min-height:224px;">
            <div class="section-header">
              <div class="nav-icon" style="background:rgba(255,255,255,0.18); color:white;">←</div>
              <div class="capsule" style="background:rgba(255,255,255,0.18); color:white;">收藏 3.4k</div>
            </div>
            <h1 style="font-size:34px; margin-top:58px;">北岭雾海穿越线</h1>
            <p>浙江 · 2026.03 发布 · 含 9 个点位，补给与危险提示完整。</p>
          </div>
          <div class="triple-grid">
            <div class="metric-card"><div class="metric-label">里程</div><div class="metric-value">11.4<span class="metric-unit">km</span></div></div>
            <div class="metric-card"><div class="metric-label">时长</div><div class="metric-value">5.8<span class="metric-unit">h</span></div></div>
            <div class="metric-card"><div class="metric-label">爬升</div><div class="metric-value">860<span class="metric-unit">m</span></div></div>
          </div>
          <div class="user-row">
            <div class="avatar">JY</div>
            <div class="grow">
              <div class="route-title" style="margin:0;">景野</div>
              <div class="tiny">发布 21 条路线 · 擅长山脊线与露营点分享</div>
            </div>
            <button class="button-secondary" style="min-height:40px;">关注</button>
          </div>
          <div class="map-panel">
            <div class="map-header">
              <div class="route-title" style="margin:0;">轨迹与点位</div>
              <span class="small-link">全屏地图</span>
            </div>
            <div class="map-path">
              <span class="marker start"></span>
              <span class="marker mid"></span>
              <span class="marker warn"></span>
              <span class="marker end"></span>
            </div>
          </div>
          <div class="timeline">
            <div class="timeline-card">
              <div class="timeline-icon">景</div>
              <div class="stack-10">
                <div class="route-title" style="margin:0;">山脊观景点</div>
                <div class="tiny">09:42 · 云海出现概率高，建议停留 10 分钟拍摄。</div>
                <div class="mini-media"></div>
              </div>
            </div>
          </div>
          <div class="footer-actions">
            <div class="action-tile">收藏</div>
            <div class="action-tile">分享</div>
            <div class="action-tile primary">开始参考</div>
          </div>
        </div>
      </div>
    `
  },
  waypoint_detail: {
    title: "点位详情页",
    subtitle: "点位不只是图文注释，而是路线价值的核心证据页，需清楚说明位置、时间、媒体与风险属性。",
    tags: ["Waypoint", "Media Proof", "Safety Clarity"],
    notes: [
      "点位类型、时间戳、海拔和媒体证据要在首屏清晰表达。",
      "危险点与补给点应使用不同功能色，但不能只依靠颜色区分。",
      "从时间线或地图进入时，都应保留返回原路线的上下文。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-16">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>点位详情</span><span>80%</span></div>
          </div>
          <div class="topbar">
            <div class="nav-icon">←</div>
            <div class="topbar-title">点位详情</div>
            <div class="capsule">观景点</div>
          </div>
          <div class="mini-media" style="height:210px; border-radius:26px;"></div>
          <div class="sheet stack-14">
            <div class="section-header">
              <div>
                <div class="route-title" style="font-size:20px; margin-bottom:8px;">风口观景平台</div>
                <div class="tiny">10:18 · 海拔 1328m · 路线中段</div>
              </div>
              <span class="chip alt">视频 02:14</span>
            </div>
            <div class="chips">
              <span class="chip">云海概率高</span>
              <span class="chip warn">风大注意保暖</span>
            </div>
            <div class="tiny" style="font-size:13px;">
              此点位位于主轨迹右侧突出的观景平台，晴天适合拍延时，雨后石面较滑，建议停留拍摄时背包靠内侧放置。
            </div>
          </div>
          <div class="map-panel" style="min-height:170px;">
            <div class="map-header">
              <div class="route-title" style="margin:0;">位置关系</div>
              <span class="small-link">查看整条路线</span>
            </div>
            <div class="map-path" style="margin-top:42px;">
              <span class="marker start"></span>
              <span class="marker mid" style="left:198px; top:22px;"></span>
            </div>
          </div>
          <div class="spacer"></div>
        </div>
      </div>
    `
  },
  record_live: {
    title: "轨迹记录中",
    subtitle: "这是分享者的核心生产界面，地图、关键数据和快速记录动作必须在强光环境中也一眼可见。",
    tags: ["Recording", "Outdoor Utility", "Low-friction Capture"],
    notes: [
      "拍照、录像、添加点位均是一键直达，不强迫先填复杂表单。",
      "暂停与结束必须明显区分，避免误触造成数据损失。",
      "GPS、时长、距离、爬升与点位数属于记录态的五个核心反馈。 "
    ],
    render: () => `
      <div class="screen" style="background:linear-gradient(180deg, #e6ebe7 0%, #dbe4dd 100%);">
        <div class="safe stack-16">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span class="dot"></span><span>REC</span><span>79%</span></div>
          </div>
          <div class="topbar">
            <div class="nav-icon">←</div>
            <div class="capsule" style="background:rgba(190,77,63,0.12); color:var(--error);">记录中 01:46:22</div>
            <div class="nav-icon">⋯</div>
          </div>
          <div class="map-panel" style="min-height:300px;">
            <div class="map-header">
              <span class="capsule">GPS 精度良好</span>
              <span class="capsule">海拔同步开启</span>
            </div>
            <div class="map-path" style="margin-top:100px;">
              <span class="marker start"></span>
              <span class="marker mid"></span>
              <span class="marker warn"></span>
              <span class="marker end"></span>
            </div>
          </div>
          <div class="dual-grid">
            <div class="metric-card"><div class="metric-label">已走距离</div><div class="metric-value">6.8<span class="metric-unit">km</span></div></div>
            <div class="metric-card"><div class="metric-label">累计爬升</div><div class="metric-value">541<span class="metric-unit">m</span></div></div>
            <div class="metric-card"><div class="metric-label">当前海拔</div><div class="metric-value">1286<span class="metric-unit">m</span></div></div>
            <div class="metric-card"><div class="metric-label">记录点位</div><div class="metric-value">5<span class="metric-unit">个</span></div></div>
          </div>
          <div class="toolbar-floating">
            <div class="tool"><div class="tool-glyph">⊕</div><span>点位</span></div>
            <div class="tool"><div class="tool-glyph">◉</div><span>拍照</span></div>
            <div class="tool"><div class="tool-glyph">▣</div><span>录像</span></div>
            <div class="tool"><div class="tool-glyph">Ⅱ</div><span>暂停</span></div>
            <div class="tool"><div class="tool-glyph">■</div><span>结束</span></div>
          </div>
          <div class="sheet">
            <div class="section-header">
              <div class="route-title" style="margin:0;">最近新增点位</div>
              <span class="small-link">全部查看</span>
            </div>
            <div class="list-card" style="margin-top:12px;">
              <div class="list-thumb" style="width:66px; height:66px;"></div>
              <div class="grow">
                <div class="route-title" style="margin-bottom:6px;">溪边补给点</div>
                <div class="tiny">09:18 · 已附 2 张照片 · 描述可稍后补全</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    `
  },
  camera_capture: {
    title: "拍照 / 录像页",
    subtitle: "拍摄界面尽量贴近系统相机习惯，但通过轻量的信息提示告诉用户内容会挂载到当前点位草稿。",
    tags: ["Capture", "System-adjacent", "Fast Media Input"],
    notes: [
      "模式切换、快门、缩略图和关闭都是高频动作，必须布局清晰。",
      "拍摄完成后不必重新选择路线上下文，媒体自动归属当前记录。",
      "视觉上保留少量品牌色，不破坏相机场景的专注度。 "
    ],
    render: () => `
      <div class="screen" style="background:#0e1411;">
        <div class="safe">
          <div class="statusbar" style="color:rgba(255,255,255,0.84);">
            <span>9:41</span>
            <div class="status-icons"><span>HDR</span><span>77%</span></div>
          </div>
          <div class="camera-panel">
            <div class="camera-controls">
              <div class="nav-icon" style="background:rgba(255,255,255,0.16); color:white;">×</div>
              <div class="capsule" style="background:rgba(255,255,255,0.14); color:white;">附加到当前点位</div>
              <div class="nav-icon" style="background:rgba(255,255,255,0.16); color:white;">↺</div>
            </div>
            <div class="camera-bottom">
              <div class="record-row">
                <div class="media-thumb"></div>
                <div class="chips">
                  <span class="chip" style="background:rgba(255,255,255,0.16); color:white;">照片</span>
                  <span class="chip" style="background:rgba(255,255,255,0.08); color:rgba(255,255,255,0.76);">视频</span>
                </div>
                <div class="nav-icon" style="background:rgba(255,255,255,0.16); color:white;">⚡</div>
              </div>
              <div class="shutter"></div>
              <div class="tiny" style="color:rgba(255,255,255,0.74); text-align:center;">
                拍摄后自动进入点位补充页，你可以稍后再写描述。
              </div>
            </div>
          </div>
        </div>
      </div>
    `
  },
  add_waypoint: {
    title: "添加点位页",
    subtitle: "支持先快速保存、后补充描述，降低记录过程中的认知与操作负担。",
    tags: ["Waypoint Authoring", "Progressive Details", "Fast Save"],
    notes: [
      "点位类型应当是首个决策项，便于后续浏览端快速理解路线结构。",
      "经纬度、时间戳、海拔等系统信息自动带入，避免用户手动维护。",
      "媒体编辑与文字说明拆开，减少野外输入摩擦。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-16">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>草稿已保存</span><span>78%</span></div>
          </div>
          <div class="topbar">
            <div class="nav-icon">←</div>
            <div class="topbar-title">新增点位</div>
            <div class="small-link">快速保存</div>
          </div>
          <div class="media-strip">
            <div class="media-thumb"></div>
            <div class="media-thumb" style="background:
              linear-gradient(180deg, rgba(17, 23, 20, 0.04), rgba(17, 23, 20, 0.28)),
              url('https://images.unsplash.com/photo-1464820453369-31d2c0b651af?auto=format&fit=crop&w=500&q=80') center/cover;"></div>
            <div class="media-thumb" style="display:grid; place-items:center; background:rgba(40,84,67,0.08); color:var(--pine-700); font-weight:700;">+ 视频</div>
          </div>
          <div class="sheet stack-14">
            <div class="field">
              <span class="field-label">点位类型</span>
              <div class="chips">
                <span class="chip">观景点</span>
                <span class="chip alt">补给点</span>
                <span class="chip warn">危险提醒</span>
              </div>
            </div>
            <div class="field"><span class="field-label">标题</span><div class="field-value">山脊观景平台</div></div>
            <div class="field"><span class="field-label">描述</span><div class="field-value" style="color:var(--stone-500);">补充为什么值得停留，或提醒后来者注意事项</div></div>
            <div class="dual-grid">
              <div class="field"><span class="field-label">时间</span><div class="field-value">10:18</div></div>
              <div class="field"><span class="field-label">海拔</span><div class="field-value">1328m</div></div>
            </div>
            <button class="button-primary">保存点位</button>
          </div>
          <div class="spacer"></div>
        </div>
      </div>
    `
  },
  route_edit: {
    title: "路线编辑页",
    subtitle: "结束记录后进入结构化整理阶段，重点补全路线描述、安全提醒、封面和点位顺序。",
    tags: ["Post-record Editing", "Publishing Prep", "Structured Metadata"],
    notes: [
      "把封面、难度、安全提醒和点位排序这些高价值字段优先展示。",
      "发布前预览是必要步骤，但编辑页本身也要支持随时草稿保存。",
      "鼓励用户补充装备建议与季节信息，强化路线的可参考价值。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-16">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>草稿保存于刚刚</span><span>76%</span></div>
          </div>
          <div class="topbar">
            <div class="nav-icon">←</div>
            <div class="topbar-title">整理路线</div>
            <div class="small-link">预览</div>
          </div>
          <div class="list-card">
            <div class="list-thumb" style="width:96px; height:96px;"></div>
            <div class="grow stack-10">
              <div class="route-title">北岭雾海穿越线</div>
              <div class="tiny">11.4km · 5h48m · 点位 9 个</div>
              <div class="chips"><span class="chip">封面已选择</span><span class="chip alt">轨迹完整</span></div>
            </div>
          </div>
          <div class="sheet stack-12">
            <div class="field"><span class="field-label">路线简介</span><div class="field-value">这是一条适合春季清晨出发的山脊雾海路线，中段风口较大，建议带防风层。</div></div>
            <div class="dual-grid">
              <div class="field"><span class="field-label">难度</span><div class="field-value">中等</div></div>
              <div class="field"><span class="field-label">最佳季节</span><div class="field-value">3-5 月</div></div>
            </div>
            <div class="field"><span class="field-label">装备建议</span><div class="field-value">防风外套、登山杖、1.5L 水、头灯</div></div>
            <div class="field"><span class="field-label">安全提醒</span><div class="field-value">风口段碎石较多，雾大时谨慎靠边拍摄。</div></div>
          </div>
          <div class="sheet stack-12">
            <div class="section-header">
              <div class="route-title" style="margin:0;">点位顺序</div>
              <span class="small-link">排序</span>
            </div>
            <div class="list-card" style="padding:10px 12px;">
              <div class="timeline-icon">1</div>
              <div class="grow">
                <div class="route-title" style="margin-bottom:4px;">山脚起点</div>
                <div class="tiny">07:12 · 起点说明已完成</div>
              </div>
            </div>
            <div class="list-card" style="padding:10px 12px;">
              <div class="timeline-icon">2</div>
              <div class="grow">
                <div class="route-title" style="margin-bottom:4px;">风口观景平台</div>
                <div class="tiny">10:18 · 含视频 1 段</div>
              </div>
            </div>
          </div>
          <button class="button-primary">进入发布预览</button>
        </div>
      </div>
    `
  },
  profile: {
    title: "我的主页",
    subtitle: "个人中心偏内容资产视角，突出已发布路线、累计里程、收藏与草稿管理，而不是社交头像页。",
    tags: ["Profile", "Content Management", "Personal Dashboard"],
    notes: [
      "统计项聚焦徒步内容沉淀价值，比普通粉丝数字更有产品意义。",
      "把草稿箱和继续编辑放在明显位置，鼓励生产链路闭环。",
      "视觉上延续自然工具感，不做娱乐型个人主页。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-16">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>同步成功</span><span>85%</span></div>
          </div>
          <div class="topbar">
            <div class="section-title" style="font-size:34px;">我的</div>
            <div class="nav-icon">⚙</div>
          </div>
          <div class="sheet stack-14">
            <div class="user-row">
              <div class="avatar large">JY</div>
              <div class="grow">
                <div class="route-title" style="font-size:20px; margin-bottom:6px;">景野</div>
                <div class="tiny">偏爱山脊线、湖边营地与日出路线记录</div>
              </div>
              <button class="button-secondary" style="min-height:40px;">编辑</button>
            </div>
            <div class="triple-grid">
              <div class="metric-card"><div class="metric-label">发布路线</div><div class="metric-value">21</div></div>
              <div class="metric-card"><div class="metric-label">累计里程</div><div class="metric-value">486<span class="metric-unit">km</span></div></div>
              <div class="metric-card"><div class="metric-label">收藏</div><div class="metric-value">3.8k</div></div>
            </div>
          </div>
          <div class="sheet stack-12">
            <div class="section-header">
              <h3 class="section-title">我的路线</h3>
              <span class="small-link">全部</span>
            </div>
            <div class="list-card">
              <div class="list-thumb"></div>
              <div class="grow stack-10">
                <div class="route-title">北岭雾海穿越线</div>
                <div class="tiny">已发布 · 收藏 1.4k · 评论 86</div>
              </div>
            </div>
            <div class="list-card">
              <div class="list-thumb" style="background:
                linear-gradient(180deg, rgba(17, 23, 20, 0.02), rgba(17, 23, 20, 0.24)),
                url('https://images.unsplash.com/photo-1454496522488-7a8e488e8606?auto=format&fit=crop&w=500&q=80') center/cover;"></div>
              <div class="grow stack-10">
                <div class="route-title">未完成草稿：西岭雪线</div>
                <div class="tiny">上次编辑于昨天 · 还有 2 个点位待补充</div>
              </div>
            </div>
          </div>
          <div class="dual-grid">
            <div class="action-tile">草稿箱</div>
            <div class="action-tile">收藏夹</div>
          </div>
          <div class="bottom-nav">
            <div class="nav-item"><div class="nav-glyph">⌂</div><span>发现</span></div>
            <div class="nav-item"><div class="nav-glyph">⌕</div><span>搜索</span></div>
            <div class="nav-item"><div class="nav-glyph">◎</div><span>记录</span></div>
            <div class="nav-item active"><div class="nav-glyph">◌</div><span>我的</span></div>
          </div>
        </div>
      </div>
    `
  },
  forgot_password: {
    title: "找回密码页",
    subtitle: "找回流程需要足够可信和清晰，强调验证方式与风险提示，而不是把页面做成普通注册表单的复制品。",
    tags: ["Authentication", "Account Recovery", "Trust-focused"],
    notes: [
      "找回密码的首要体验是降低焦虑，因此文案要明确、步骤要短。",
      "验证码发送、倒计时与风险说明应靠近主操作，避免用户反复搜索入口。",
      "延续认证体系的克制表单风格，确保整个 onboarding 体验一致。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-18">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>短信验证</span><span>88%</span></div>
          </div>
          <div class="topbar">
            <div class="nav-icon">←</div>
            <div class="topbar-title">找回密码</div>
            <div class="capsule">安全验证</div>
          </div>
          <div class="sheet stack-14">
            <div class="field"><span class="field-label">手机号 / 邮箱</span><div class="field-value">138 0000 2468</div></div>
            <div class="dual-grid">
              <div class="field"><span class="field-label">验证码</span><div class="field-value">628491</div></div>
              <button class="button-secondary" style="min-height:72px;">重新发送<br/>56s</button>
            </div>
            <div class="field"><span class="field-label">新密码</span><div class="field-value">至少 8 位，建议包含大小写</div></div>
            <div class="tiny">若 5 分钟内未收到验证码，可切换邮箱找回或联系支持。</div>
            <button class="button-primary">重置密码</button>
          </div>
          <div class="sheet stack-12">
            <div class="route-title" style="margin:0;">安全提示</div>
            <div class="tiny">修改密码后，旧设备登录状态将需要重新验证。</div>
          </div>
          <div class="spacer"></div>
        </div>
      </div>
    `
  },
  complete_profile: {
    title: "完善资料页",
    subtitle: "注册后用更轻量的偏好设置建立推荐基础，不把用户困在冗长 onboarding 中。",
    tags: ["Onboarding", "Preference Setup", "Lightweight Profile"],
    notes: [
      "头像、城市、经验等级和兴趣标签已经足以支撑初始推荐。",
      "兴趣标签采用清晰的主题分类，便于首页和搜索默认筛选。",
      "该页面应允许稍后补充，避免注册后流失。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-16">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>新用户引导</span><span>92%</span></div>
          </div>
          <div class="topbar">
            <div class="nav-icon">←</div>
            <div class="topbar-title">完善资料</div>
            <div class="small-link">稍后</div>
          </div>
          <div class="sheet stack-14">
            <div class="user-row">
              <div class="avatar large">+</div>
              <div class="grow">
                <div class="route-title" style="font-size:18px; margin-bottom:6px;">添加头像与昵称</div>
                <div class="tiny">让你的路线分享更容易建立信任感</div>
              </div>
            </div>
            <div class="field"><span class="field-label">昵称</span><div class="field-value">山野风</div></div>
            <div class="dual-grid">
              <div class="field"><span class="field-label">常驻城市</span><div class="field-value">上海</div></div>
              <div class="field"><span class="field-label">经验等级</span><div class="field-value">轻中度徒步</div></div>
            </div>
            <div class="field">
              <span class="field-label">感兴趣主题</span>
              <div class="chips">
                <span class="chip">山脊线</span>
                <span class="chip alt">湖边营地</span>
                <span class="chip">日出路线</span>
                <span class="chip warn">新手友好</span>
              </div>
            </div>
            <button class="button-primary">完成并进入首页</button>
          </div>
        </div>
      </div>
    `
  },
  author_profile: {
    title: "作者主页",
    subtitle: "作者主页要服务于内容信任，不是单纯展示头像和社交关系，而是强调其路线资产与擅长领域。",
    tags: ["Author Profile", "Trust Layer", "Content Portfolio"],
    notes: [
      "用户关注作者，本质上是在关注稳定输出的路线质量。",
      "应突出累计发布、擅长类型、代表路线和最近更新频率。",
      "视觉上比“我的主页”更偏浏览消费，不出现过多管理型入口。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-16">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>作者主页</span><span>83%</span></div>
          </div>
          <div class="topbar">
            <div class="nav-icon">←</div>
            <div class="topbar-title">作者</div>
            <div class="nav-icon">↗</div>
          </div>
          <div class="sheet stack-14">
            <div class="user-row">
              <div class="avatar large">JY</div>
              <div class="grow">
                <div class="route-title" style="font-size:20px; margin-bottom:4px;">景野</div>
                <div class="tiny">擅长高山风景线与轻露营路线分享</div>
              </div>
            </div>
            <div class="triple-grid">
              <div class="metric-card"><div class="metric-label">发布</div><div class="metric-value">21</div></div>
              <div class="metric-card"><div class="metric-label">粉丝</div><div class="metric-value">8.6k</div></div>
              <div class="metric-card"><div class="metric-label">累计收藏</div><div class="metric-value">31k</div></div>
            </div>
            <div class="chips">
              <span class="chip">山脊穿越</span>
              <span class="chip alt">营地位点</span>
              <span class="chip">日出机位</span>
            </div>
            <button class="button-primary">关注作者</button>
          </div>
          <div class="sheet stack-12">
            <div class="section-header">
              <h3 class="section-title">代表路线</h3>
              <span class="small-link">全部路线</span>
            </div>
            <div class="list-card">
              <div class="list-thumb"></div>
              <div class="grow">
                <div class="route-title">北岭雾海穿越线</div>
                <div class="tiny">11.4km · 收藏 1.4k · 点位 9</div>
              </div>
            </div>
            <div class="list-card">
              <div class="list-thumb" style="background:
                linear-gradient(180deg, rgba(17, 23, 20, 0.02), rgba(17, 23, 20, 0.24)),
                url('https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=700&q=80') center/cover;"></div>
              <div class="grow">
                <div class="route-title">晨风高地折返线</div>
                <div class="tiny">6.7km · 新手友好 · 视频 4</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    `
  },
  comments: {
    title: "评论页",
    subtitle: "评论是路线可信度的重要补充，重点不是社交热闹，而是补充真实体验、风险反馈和现场建议。",
    tags: ["Comments", "Community Trust", "Route Feedback"],
    notes: [
      "评论输入框固定底部，适合快速补充“雨后很滑”“补给点已关闭”这类关键信息。",
      "作者回复与高价值评论应更容易被看见，避免被闲聊淹没。",
      "整体层级需克制，评论不能压过路线主体内容。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-14">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>评论 128</span><span>81%</span></div>
          </div>
          <div class="topbar">
            <div class="nav-icon">←</div>
            <div class="topbar-title">路线评论</div>
            <div class="capsule">高价值优先</div>
          </div>
          <div class="sheet stack-12">
            <div class="user-row">
              <div class="avatar">LM</div>
              <div class="grow">
                <div class="route-title" style="margin-bottom:4px;">林木</div>
                <div class="tiny">昨天 18:42</div>
              </div>
              <span class="small-link">赞 28</span>
            </div>
            <div class="tiny" style="font-size:13px;">
              雨后第二个风口段碎石有些滑，登山杖非常有用。作者标注的补给点目前仍然营业，买到热水了。
            </div>
          </div>
          <div class="sheet stack-12">
            <div class="user-row">
              <div class="avatar">景</div>
              <div class="grow">
                <div class="route-title" style="margin-bottom:4px;">作者回复</div>
                <div class="tiny">感谢反馈，我已把雨后湿滑提醒加到详情里。</div>
              </div>
            </div>
          </div>
          <div class="sheet stack-12">
            <div class="user-row">
              <div class="avatar">ZZ</div>
              <div class="grow">
                <div class="route-title" style="margin-bottom:4px;">周周</div>
                <div class="tiny">3 天前 · 首次徒步</div>
              </div>
              <span class="small-link">赞 9</span>
            </div>
            <div class="tiny" style="font-size:13px;">
              新手也能完成，但一定要预留返程体力。点位视频很有帮助，岔路口完全没走错。
            </div>
          </div>
          <div class="spacer"></div>
          <div class="searchbar" style="margin-top:auto;">写下你的路况补充或体验建议</div>
        </div>
      </div>
    `
  },
  record_prepare: {
    title: "记录准备页",
    subtitle: "在真正开始轨迹记录前，只保留必要设置，确保用户能快速进入行进状态，不被配置拖慢。",
    tags: ["Pre-record", "Setup", "Low Friction"],
    notes: [
      "路线名称、起点、公开范围和离线提醒已足够支撑首轮记录。",
      "记录型产品必须避免把设置做成复杂表单，准备页应更像出发前检查单。",
      "开始记录是绝对主操作，保存草稿只是次操作。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-16">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>准备出发</span><span>87%</span></div>
          </div>
          <div class="topbar">
            <div class="nav-icon">←</div>
            <div class="topbar-title">开始记录</div>
            <div class="small-link">存草稿</div>
          </div>
          <div class="hero-card" style="min-height:180px; background:
            linear-gradient(180deg, rgba(7, 12, 10, 0.02), rgba(7, 12, 10, 0.46)),
            linear-gradient(140deg, rgba(23, 53, 44, 0.82), rgba(58, 124, 165, 0.34)),
            url('https://images.unsplash.com/photo-1551632811-561732d1e306?auto=format&fit=crop&w=900&q=80') center/cover;">
            <span class="capsule" style="background:rgba(255,255,255,0.14); color:white;">出发前检查</span>
            <h1 style="font-size:34px; margin-top:30px;">让记录轻一点<br/>让分享完整一点</h1>
          </div>
          <div class="sheet stack-14">
            <div class="field"><span class="field-label">路线名称</span><div class="field-value">北岭雾海穿越线</div></div>
            <div class="field"><span class="field-label">起点位置</span><div class="field-value">自动定位到北岭停车场入口</div></div>
            <div class="dual-grid">
              <div class="field"><span class="field-label">公开范围</span><div class="field-value">发布后公开</div></div>
              <div class="field"><span class="field-label">海拔记录</span><div class="field-value">已开启</div></div>
            </div>
            <div class="field"><span class="field-label">离线提醒</span><div class="field-value">信号弱时自动本地保存媒体</div></div>
            <button class="button-primary">开始记录</button>
          </div>
        </div>
      </div>
    `
  },
  publish_preview: {
    title: "发布预览页",
    subtitle: "发布前需要模拟真实消费页效果，让分享者用浏览者视角检查自己的路线是否足够可信和完整。",
    tags: ["Publish Flow", "Preview", "Quality Gate"],
    notes: [
      "预览不是简单确认弹窗，而是一次发布质量检查。",
      "缺失简介、安全提醒、封面或点位描述时要温和提示补全。",
      "页面应最大程度贴近真实详情页，减少发布后的心理落差。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-16">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>发布前检查</span><span>75%</span></div>
          </div>
          <div class="topbar">
            <div class="nav-icon">←</div>
            <div class="topbar-title">发布预览</div>
            <div class="capsule">已完整 92%</div>
          </div>
          <div class="list-card">
            <div class="list-thumb" style="width:96px; height:96px;"></div>
            <div class="grow">
              <div class="route-title">北岭雾海穿越线</div>
              <div class="tiny">11.4km · 5h48m · 点位 9 · 媒体 17</div>
              <div class="chips" style="margin-top:8px;"><span class="chip">预览模式</span><span class="chip alt">接近用户展示</span></div>
            </div>
          </div>
          <div class="map-panel">
            <div class="map-header">
              <div class="route-title" style="margin:0;">路线摘要</div>
              <span class="small-link">编辑封面</span>
            </div>
            <div class="map-path">
              <span class="marker start"></span>
              <span class="marker mid"></span>
              <span class="marker warn"></span>
              <span class="marker end"></span>
            </div>
          </div>
          <div class="sheet stack-12">
            <div class="route-title" style="margin:0;">发布前建议</div>
            <div class="tiny">安全提醒已填写，但“装备建议”仍可更具体。当前有 1 个点位描述较短，可考虑补充。</div>
          </div>
          <div class="footer-actions">
            <div class="action-tile">返回编辑</div>
            <div class="action-tile">存草稿</div>
            <div class="action-tile primary">确认发布</div>
          </div>
        </div>
      </div>
    `
  },
  publish_success: {
    title: "发布成功页",
    subtitle: "发布成功不仅是流程终点，也应该鼓励继续分享与传播，让用户感受到内容被真正建立与保存。",
    tags: ["Publish Flow", "Success State", "Momentum"],
    notes: [
      "成功页不应只有一个勾号，还要承接‘查看详情’和‘继续记录’两个下一步。",
      "可在此处强化“你已帮助后来者”的产品价值感。",
      "视觉适合更松弛、更有完成感，但仍保持品牌克制。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-20" style="justify-content:center;">
          <div class="sheet stack-16" style="text-align:center; padding:28px 22px;">
            <div style="width:96px; height:96px; border-radius:28px; margin:0 auto; background:linear-gradient(180deg, rgba(61,122,97,0.18), rgba(23,53,44,0.2)); display:grid; place-items:center; font-size:42px; color:var(--pine-700);">✓</div>
            <div class="section-title" style="font-size:38px;">路线已发布</div>
            <div class="tiny" style="font-size:14px;">你的记录已整理成可被搜索、收藏和参考的路线内容。</div>
            <div class="dual-grid">
              <div class="metric-card"><div class="metric-label">点位</div><div class="metric-value">9</div></div>
              <div class="metric-card"><div class="metric-label">媒体</div><div class="metric-value">17</div></div>
            </div>
            <button class="button-primary">查看路线详情</button>
            <button class="button-secondary">分享给朋友</button>
            <button class="button-ghost">继续记录新路线</button>
          </div>
        </div>
      </div>
    `
  },
  my_routes: {
    title: "我的路线页",
    subtitle: "我的路线页是内容管理中枢，需要清晰区分已发布、草稿和私密内容，并支持继续编辑未完成记录。",
    tags: ["Content Management", "My Routes", "Status Tabs"],
    notes: [
      "路线状态是最重要的分层维度，比单纯时间排序更重要。",
      "草稿内容应明显提示“还差什么”，帮助用户完成闭环。",
      "管理页需要比发现页更高的信息密度，但仍保持可扫视性。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-16">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>我的路线</span><span>84%</span></div>
          </div>
          <div class="topbar">
            <div class="nav-icon">←</div>
            <div class="topbar-title">我的路线</div>
            <div class="nav-icon">＋</div>
          </div>
          <div class="chips">
            <span class="chip">已发布</span>
            <span class="chip alt">草稿</span>
            <span class="chip">私密</span>
          </div>
          <div class="stack-12">
            <div class="list-card">
              <div class="list-thumb"></div>
              <div class="grow stack-10">
                <div class="route-title">北岭雾海穿越线</div>
                <div class="tiny">已发布 · 收藏 1.4k · 更新于 2 天前</div>
                <div class="chips"><span class="chip">查看数据</span><span class="chip alt">再次编辑</span></div>
              </div>
            </div>
            <div class="list-card">
              <div class="list-thumb" style="background:
                linear-gradient(180deg, rgba(17, 23, 20, 0.02), rgba(17, 23, 20, 0.24)),
                url('https://images.unsplash.com/photo-1482192596544-9eb780fc7f66?auto=format&fit=crop&w=500&q=80') center/cover;"></div>
              <div class="grow stack-10">
                <div class="route-title">西岭雪线草稿</div>
                <div class="tiny">未发布 · 缺少封面和 2 个点位描述</div>
                <div class="chips"><span class="chip warn">继续编辑</span><span class="chip">删除</span></div>
              </div>
            </div>
            <div class="list-card">
              <div class="list-thumb" style="background:
                linear-gradient(180deg, rgba(17, 23, 20, 0.02), rgba(17, 23, 20, 0.24)),
                url('https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=500&q=80') center/cover;"></div>
              <div class="grow stack-10">
                <div class="route-title">南坡营地线</div>
                <div class="tiny">私密 · 仅自己可见 · 上周整理</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    `
  },
  favorites: {
    title: "收藏夹页",
    subtitle: "收藏夹不应只是一份静态书签列表，而要支持路线、点位、作者三种收藏对象的并行管理。",
    tags: ["Favorites", "Saved Content", "Tri-category"],
    notes: [
      "路线、点位、作者的收藏动机不同，因此需要分栏而不是混排。",
      "收藏页是复访入口，需强调“下次出发时可快速回看”。",
      "适合加入最近浏览时间或收藏来源，但当前静态稿先聚焦结构。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-16">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>收藏夹</span><span>82%</span></div>
          </div>
          <div class="topbar">
            <div class="nav-icon">←</div>
            <div class="topbar-title">收藏夹</div>
            <div class="capsule">共 86 项</div>
          </div>
          <div class="chips">
            <span class="chip">路线</span>
            <span class="chip alt">点位</span>
            <span class="chip">作者</span>
          </div>
          <div class="list-card">
            <div class="list-thumb"></div>
            <div class="grow">
              <div class="route-title">武功山金顶日出轻装上行</div>
              <div class="tiny">收藏于上周 · 新手友好 · 点位 7</div>
            </div>
          </div>
          <div class="list-card">
            <div class="timeline-icon">景</div>
            <div class="grow">
              <div class="route-title">风口观景平台</div>
              <div class="tiny">收藏于路线“北岭雾海穿越线” · 视频 1 段</div>
            </div>
          </div>
          <div class="list-card">
            <div class="avatar">JY</div>
            <div class="grow">
              <div class="route-title">景野</div>
              <div class="tiny">擅长山脊线、露营点和日出机位路线</div>
            </div>
          </div>
          <div class="sheet stack-12">
            <div class="route-title" style="margin:0;">下次出发建议</div>
            <div class="tiny">你收藏的 6 条路线位于同一山系，可考虑整理成一个“周末出发清单”。</div>
          </div>
        </div>
      </div>
    `
  },
  settings: {
    title: "设置页",
    subtitle: "设置页强调账号安全、隐私、缓存与权限等真实高频项，而不是堆砌低使用率功能入口。",
    tags: ["Settings", "System Utility", "Privacy & Storage"],
    notes: [
      "该产品涉及定位、相机、视频与离线缓存，设置页的工具属性会非常明显。",
      "安全与隐私应前置，缓存和下载紧随其后，符合用户实际心智。",
      "分组卡片比传统长列表更有利于快速扫描。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-14">
          <div class="statusbar">
            <span>9:41</span>
            <div class="status-icons"><span>设置</span><span>80%</span></div>
          </div>
          <div class="topbar">
            <div class="nav-icon">←</div>
            <div class="topbar-title">设置</div>
            <div class="nav-icon">?</div>
          </div>
          <div class="sheet stack-12">
            <div class="route-title" style="margin:0;">账号与安全</div>
            <div class="list-card" style="padding:12px 14px;"><div class="timeline-icon">密</div><div class="grow"><div class="route-title" style="margin-bottom:4px;">修改密码</div><div class="tiny">最近更新于 30 天前</div></div></div>
            <div class="list-card" style="padding:12px 14px;"><div class="timeline-icon">设</div><div class="grow"><div class="route-title" style="margin-bottom:4px;">登录设备管理</div><div class="tiny">当前 2 台设备在线</div></div></div>
          </div>
          <div class="sheet stack-12">
            <div class="route-title" style="margin:0;">隐私与权限</div>
            <div class="list-card" style="padding:12px 14px;"><div class="timeline-icon">位</div><div class="grow"><div class="route-title" style="margin-bottom:4px;">定位权限</div><div class="tiny">始终允许，仅记录时使用高精度</div></div></div>
            <div class="list-card" style="padding:12px 14px;"><div class="timeline-icon">机</div><div class="grow"><div class="route-title" style="margin-bottom:4px;">相机与相册</div><div class="tiny">已允许，用于点位拍照和视频上传</div></div></div>
          </div>
          <div class="sheet stack-12">
            <div class="route-title" style="margin:0;">下载与缓存</div>
            <div class="list-card" style="padding:12px 14px;"><div class="timeline-icon">缓存</div><div class="grow"><div class="route-title" style="margin-bottom:4px;">离线地图与媒体</div><div class="tiny">当前占用 2.4GB，可清理</div></div></div>
          </div>
          <button class="button-secondary">退出登录</button>
        </div>
      </div>
    `
  },
  ds_foundations: {
    title: "设计系统 · Foundations",
    subtitle: "设计系统基础页明确颜色、字体、圆角、阴影与间距 token，是后续页面与组件统一的根基。",
    tags: ["Design System", "Foundations", "Tokens"],
    notes: [
      "这一页服务设计与开发协作，核心是把审美选择沉淀成可复用规则。",
      "当前产品的关键识别来自 Pine / Lake / Sand 三组自然色与地形线纹理。",
      "建议后续将这些 token 继续同步到 Figma Variables 与代码主题文件。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-16">
          <div class="statusbar">
            <span>System</span>
            <div class="status-icons"><span>Foundations</span><span>v1</span></div>
          </div>
          <div class="spec-panel">
            <div class="spec-card dark">
              <div class="spec-title">Color Palette</div>
              <div class="spec-grid-4">
                <div><div class="swatch" style="background:#17352c;"></div><div class="token-name">Pine 900</div><div class="token-value">#17352C</div></div>
                <div><div class="swatch" style="background:#3d7a61;"></div><div class="token-name">Pine 500</div><div class="token-value">#3D7A61</div></div>
                <div><div class="swatch" style="background:#3a7ca5;"></div><div class="token-name">Lake 500</div><div class="token-value">#3A7CA5</div></div>
                <div><div class="swatch" style="background:#c59d5f;"></div><div class="token-name">Sand 500</div><div class="token-value">#C59D5F</div></div>
              </div>
            </div>
            <div class="spec-grid-2">
              <div class="spec-card">
                <div class="spec-title">Typography Scale</div>
                <div class="type-row"><div class="type-sample">Aa</div><div><div class="route-title" style="margin:0;">Display</div><div class="tiny">Cormorant Garamond / 32-74</div></div><div class="tiny">Hero</div></div>
                <div class="type-row"><div class="type-sample" style="font-size:22px;">Aa</div><div><div class="route-title" style="margin:0;">Title</div><div class="tiny">Noto Sans SC / 18-20</div></div><div class="tiny">Section</div></div>
                <div class="type-row"><div class="type-sample" style="font-size:18px; font-family:var(--font-body);">Aa</div><div><div class="route-title" style="margin:0;">Body</div><div class="tiny">Noto Sans SC / 14-16</div></div><div class="tiny">Content</div></div>
              </div>
              <div class="spec-card">
                <div class="spec-title">Radius & Shadow</div>
                <div class="spec-grid-3">
                  <div class="spec-card" style="padding:18px; background:var(--mist-100);"><div class="tiny">Radius sm</div><div class="swatch" style="height:48px; background:white; border-radius:14px;"></div></div>
                  <div class="spec-card" style="padding:18px; background:var(--mist-100);"><div class="tiny">Radius md</div><div class="swatch" style="height:48px; background:white; border-radius:20px;"></div></div>
                  <div class="spec-card" style="padding:18px; background:var(--mist-100);"><div class="tiny">Radius lg</div><div class="swatch" style="height:48px; background:white; border-radius:28px;"></div></div>
                </div>
                <div class="tiny" style="margin-top:12px;">Card shadow: 0 18 45 rgba(17,23,20,0.15)</div>
                <div class="tiny">Floating shadow: 0 24 80 rgba(8,12,10,0.26)</div>
              </div>
            </div>
            <div class="spec-card">
              <div class="spec-title">Spacing Tokens</div>
              <div class="spec-grid-4">
                <div class="spec-card"><div class="token-name">space.8</div><div class="token-value">紧密信息间距</div></div>
                <div class="spec-card"><div class="token-name">space.12</div><div class="token-value">组件内辅助间距</div></div>
                <div class="spec-card"><div class="token-name">space.16</div><div class="token-value">主内容间距</div></div>
                <div class="spec-card"><div class="token-name">space.24</div><div class="token-value">模块区块间距</div></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    `
  },
  ds_components: {
    title: "设计系统 · Components",
    subtitle: "组件页沉淀高频控件的外观与组合原则，帮助业务页面保持同一种工具感与自然探索感。",
    tags: ["Design System", "Components", "Reusable UI"],
    notes: [
      "重点组件包括按钮、输入框、chips、路线卡、点位卡与底部导航。",
      "这里不追求穷举所有零件，而先覆盖产品中复用率最高的模块。",
      "后续可继续拆成更细的组件状态页和开发标注页。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-14">
          <div class="statusbar">
            <span>System</span>
            <div class="status-icons"><span>Components</span><span>v1</span></div>
          </div>
          <div class="spec-grid-2">
            <div class="spec-card stack-12">
              <div class="spec-title">Buttons</div>
              <button class="button-primary">Primary Action</button>
              <button class="button-secondary">Secondary Action</button>
              <button class="button-ghost" style="justify-self:start;">Ghost Action</button>
              <div class="action-tile primary" style="margin-top:4px;">Floating CTA</div>
            </div>
            <div class="spec-card stack-12">
              <div class="spec-title">Inputs & Chips</div>
              <div class="field"><span class="field-label">路线名称</span><div class="field-value">输入字段示例</div></div>
              <div class="chips">
                <span class="chip">难度中等</span>
                <span class="chip alt">视频路线</span>
                <span class="chip warn">危险提醒</span>
              </div>
            </div>
          </div>
          <div class="spec-grid-2">
            <div class="route-card">
              <div class="spec-title">Route Card</div>
              <div class="route-cover"></div>
              <div class="route-title">北岭雾海穿越线</div>
              <div class="route-meta">11.4km · 5h48m · 点位 9 · 收藏 1.4k</div>
              <div class="route-stats"><span>山脊线</span><span>视频 6</span></div>
            </div>
            <div class="spec-card stack-12">
              <div class="spec-title">Waypoint Card</div>
              <div class="timeline-card" style="padding:0; background:transparent; border:none;">
                <div class="timeline-icon">景</div>
                <div class="stack-10">
                  <div class="route-title" style="margin:0;">观景平台</div>
                  <div class="tiny">10:18 · 海拔 1328m · 视频 1 段</div>
                  <div class="mini-media"></div>
                </div>
              </div>
            </div>
          </div>
          <div class="spec-card">
            <div class="spec-title">Navigation</div>
            <div class="bottom-nav" style="margin-top:0;">
              <div class="nav-item active"><div class="nav-glyph">⌂</div><span>发现</span></div>
              <div class="nav-item"><div class="nav-glyph">⌕</div><span>搜索</span></div>
              <div class="nav-item active"><div class="nav-glyph">◎</div><span>记录</span></div>
              <div class="nav-item"><div class="nav-glyph">◌</div><span>我的</span></div>
            </div>
          </div>
        </div>
      </div>
    `
  },
  ds_states: {
    title: "设计系统 · States",
    subtitle: "状态页说明交互组件在默认、聚焦、禁用、成功、警告、错误等关键状态下的统一表现。",
    tags: ["Design System", "States", "Interaction Rules"],
    notes: [
      "状态统一是产品成熟度的重要来源，尤其对表单、上传与记录态产品更明显。",
      "所有状态都要做到颜色、文案与图形线索三重表达，不依赖单一颜色。",
      "这页是后续前端组件库映射的重要基础。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-14">
          <div class="statusbar">
            <span>System</span>
            <div class="status-icons"><span>States</span><span>v1</span></div>
          </div>
          <div class="spec-card stack-12">
            <div class="spec-title">Input States</div>
            <div class="state-row">
              <div class="field"><span class="field-label">默认</span><div class="field-value">请输入路线名称</div></div>
              <div class="field" style="border-color:rgba(40,84,67,0.22); box-shadow:0 0 0 3px rgba(61,122,97,0.12);"><span class="field-label" style="color:var(--pine-700);">聚焦</span><div class="field-value">北岭雾海穿越线</div></div>
              <div class="field" style="border-color:rgba(190,77,63,0.22);"><span class="field-label" style="color:var(--error);">错误</span><div class="field-value">标题不能为空</div></div>
              <div class="field" style="opacity:0.52;"><span class="field-label">禁用</span><div class="field-value">等待权限开启</div></div>
            </div>
          </div>
          <div class="spec-grid-2">
            <div class="spec-card stack-12">
              <div class="spec-title">Button States</div>
              <button class="button-primary">默认</button>
              <button class="button-primary" style="filter:brightness(1.06); transform:scale(0.99);">按下</button>
              <button class="button-primary" style="opacity:0.42;">禁用</button>
              <button class="button-secondary">次级</button>
            </div>
            <div class="spec-card stack-12">
              <div class="spec-title">Feedback States</div>
              <div class="state-item"><span class="emphasis">成功</span><div class="tiny">点位保存成功，将在结束记录后继续可编辑。</div></div>
              <div class="state-item"><span style="color:var(--warning); font-weight:700;">警告</span><div class="tiny">GPS 精度降低，建议暂时停留等待定位稳定。</div></div>
              <div class="state-item"><span style="color:var(--error); font-weight:700;">错误</span><div class="tiny">媒体上传失败，已保留本地草稿，支持稍后重试。</div></div>
            </div>
          </div>
        </div>
      </div>
    `
  },
  ds_empty_loading_errors: {
    title: "设计系统 · 空态与异常",
    subtitle: "空态、加载和错误态定义产品的下限体验，尤其在弱网、无结果和媒体上传失败场景里必须足够清楚。",
    tags: ["Design System", "Empty & Error", "Resilience"],
    notes: [
      "这个产品天然会遇到弱网、GPS 抖动、媒体失败与搜索无结果，因此这些状态不是边角料。",
      "空状态要给出明确下一步，而不是只有插画和一句空文案。",
      "异常态的原则是保留用户成果、说明原因、提供重试。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-14">
          <div class="statusbar">
            <span>System</span>
            <div class="status-icons"><span>Edge Cases</span><span>v1</span></div>
          </div>
          <div class="spec-grid-2">
            <div class="spec-card stack-12">
              <div class="spec-title">Search Empty</div>
              <div class="tiny-phone">
                <div class="tiny-screen">
                  <div class="tiny-safe">
                    <div class="tiny-bar"></div>
                    <div class="tiny-block hero" style="height:78px; background:linear-gradient(135deg, rgba(197,157,95,0.18), rgba(61,122,97,0.18));"></div>
                    <div class="tiny" style="text-align:center;">没有找到匹配路线，试试更宽松的筛选条件。</div>
                    <div class="button-secondary" style="min-height:42px; display:grid; place-items:center;">清空筛选</div>
                  </div>
                </div>
              </div>
            </div>
            <div class="spec-card stack-12">
              <div class="spec-title">Loading Skeleton</div>
              <div class="tiny-phone">
                <div class="tiny-screen">
                  <div class="tiny-safe">
                    <div class="tiny-bar"></div>
                    <div class="tiny-block hero"></div>
                    <div class="tiny-row"><div class="tiny-block line"></div><div class="tiny-block line"></div></div>
                    <div class="tiny-block map"></div>
                    <div class="tiny-block line"></div>
                    <div class="tiny-block line" style="width:68%;"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="spec-grid-2">
            <div class="spec-card stack-12">
              <div class="spec-title">Upload Failed</div>
              <div class="state-item"><span style="color:var(--error); font-weight:700;">媒体上传失败</span><div class="tiny">已本地保存 3 项内容，待网络恢复后自动重试。</div></div>
              <button class="button-secondary">立即重试</button>
            </div>
            <div class="spec-card stack-12">
              <div class="spec-title">GPS Weak</div>
              <div class="state-item"><span style="color:var(--warning); font-weight:700;">定位信号弱</span><div class="tiny">轨迹精度下降，建议在开阔区域停留数秒。</div></div>
              <button class="button-secondary">继续记录</button>
            </div>
          </div>
        </div>
      </div>
    `
  },
  ds_recording_exceptions: {
    title: "设计系统 · 记录异常场景",
    subtitle: "记录异常页聚焦徒步过程中的高风险状态，包括中断、低电量、离线、本地缓存和未完成点位。",
    tags: ["Design System", "Recording Exceptions", "Outdoor Critical"],
    notes: [
      "户外场景中的异常设计比一般内容产品更重要，因为它直接影响记录成果是否丢失。",
      "异常提示必须短、直接、可执行，不能写成长段说明。",
      "核心原则始终是先保留数据，再解释，再给下一步。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-14">
          <div class="statusbar">
            <span>System</span>
            <div class="status-icons"><span>Recording Risk</span><span>v1</span></div>
          </div>
          <div class="spec-card stack-12">
            <div class="spec-title">Critical Prompts</div>
            <div class="state-item"><span style="color:var(--warning); font-weight:700;">电量不足 10%</span><div class="tiny">建议开启省电暂停模式，轨迹和点位草稿已自动保存。</div></div>
            <div class="state-item"><span style="color:var(--error); font-weight:700;">记录意外中断</span><div class="tiny">检测到 App 意外退出，恢复上次未完成记录？</div></div>
            <div class="state-item"><span class="emphasis">离线缓存中</span><div class="tiny">当前无网络，照片与视频将在恢复连接后上传。</div></div>
          </div>
          <div class="spec-card stack-12">
            <div class="spec-title">Recovery Actions</div>
            <div class="dual-grid">
              <button class="button-primary">恢复记录</button>
              <button class="button-secondary">结束并整理</button>
            </div>
            <div class="field"><span class="field-label">未完成点位</span><div class="field-value">2 个点位缺少文字描述，可在结束后集中补充。</div></div>
          </div>
        </div>
      </div>
    `
  },
  ds_motion_spec: {
    title: "设计系统 · 动效说明",
    subtitle: "动效页定义页面切换、卡片点击、底部抽屉和记录反馈的统一节奏，保证产品在两端都保持一致的运动语言。",
    tags: ["Design System", "Motion", "Interaction Rhythm"],
    notes: [
      "动效不是装饰，而是帮助用户理解层级、状态和动作结果。",
      "本产品动效应偏克制和清晰，不做炫技式过渡。",
      "建议前端实现时统一 duration / easing token，避免各页面手感漂移。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-14">
          <div class="statusbar">
            <span>System</span>
            <div class="status-icons"><span>Motion</span><span>v1</span></div>
          </div>
          <div class="spec-card">
            <div class="spec-title">Timing Tokens</div>
            <div class="motion-track">
              <div class="motion-step"><div class="motion-index">1</div><div class="route-title" style="margin:0;">Card Press</div><div class="tiny">110ms · 轻微缩放 0.99 · ease-out</div><div class="timeline-bar"><div class="timeline-fill" style="width:24%;"></div></div></div>
              <div class="motion-step"><div class="motion-index">2</div><div class="route-title" style="margin:0;">Page Transition</div><div class="tiny">220ms · 横向推进 / 淡入 · standard curve</div><div class="timeline-bar"><div class="timeline-fill" style="width:46%;"></div></div></div>
              <div class="motion-step"><div class="motion-index">3</div><div class="route-title" style="margin:0;">Bottom Sheet</div><div class="tiny">260ms · 上滑展开 · interruptible</div><div class="timeline-bar"><div class="timeline-fill" style="width:56%;"></div></div></div>
              <div class="motion-step"><div class="motion-index">4</div><div class="route-title" style="margin:0;">Publish Success</div><div class="tiny">320ms · 微弹性完成反馈 · reduced motion 可降级</div><div class="timeline-bar"><div class="timeline-fill" style="width:68%;"></div></div></div>
            </div>
          </div>
          <div class="spec-grid-2">
            <div class="spec-card"><div class="spec-title">Do</div><div class="tiny">动画用于表达层级变化、保存成功、地图与卡片联动，不阻塞用户输入。</div></div>
            <div class="spec-card"><div class="spec-title">Don't</div><div class="tiny">避免长时间、装饰性、不可打断的动画，避免宽高重排和夸张 3D 翻转。</div></div>
          </div>
        </div>
      </div>
    `
  },
  ds_accessibility_spec: {
    title: "设计系统 · 无障碍说明",
    subtitle: "无障碍页沉淀对比度、点击热区、动态字体、图标辅助文案和颜色使用边界，保证户外场景下的可读与可操作性。",
    tags: ["Design System", "Accessibility", "Outdoor Readability"],
    notes: [
      "这类产品在强光、移动中和手套操作场景下，对可读性要求比普通内容 App 更高。",
      "无障碍不只是给极端场景准备，而是提高所有用户的成功率。",
      "建议后续把这页转化为设计评审和 QA checklist。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-14">
          <div class="statusbar">
            <span>System</span>
            <div class="status-icons"><span>Accessibility</span><span>v1</span></div>
          </div>
          <div class="spec-card stack-12">
            <div class="spec-title">Core Rules</div>
            <div class="state-item"><span class="emphasis">对比度</span><div class="tiny">正文与背景不低于 4.5:1，弱提示文本也需在户外可读。</div></div>
            <div class="state-item"><span class="emphasis">点击区域</span><div class="tiny">所有高频操作不小于 44×44，记录页关键按钮建议更大。</div></div>
            <div class="state-item"><span class="emphasis">颜色语义</span><div class="tiny">危险、成功、警告必须配合文字或图标，不仅靠颜色。</div></div>
            <div class="state-item"><span class="emphasis">动态字体</span><div class="tiny">支持系统字体放大，关键数据和按钮文案不能截断成不可理解状态。</div></div>
          </div>
          <div class="spec-grid-2">
            <div class="spec-card">
              <div class="spec-title">Readable Outdoor UI</div>
              <div class="tiny-phone">
                <div class="tiny-screen">
                  <div class="tiny-safe">
                    <div class="tiny-bar"></div>
                    <div class="tiny-block hero"></div>
                    <div class="tiny-block line" style="height:18px;"></div>
                    <div class="tiny-block map"></div>
                    <div class="button-primary" style="min-height:42px; display:grid; place-items:center;">高对比主操作</div>
                  </div>
                </div>
              </div>
            </div>
            <div class="spec-card">
              <div class="spec-title">Avoid</div>
              <div class="tiny">避免低对比灰字、过小标签、仅图标操作、过密数据块和长段无层级提示。</div>
            </div>
          </div>
        </div>
      </div>
    `
  },
  ds_component_state_matrix: {
    title: "设计系统 · 组件状态总表",
    subtitle: "这张页面把高频组件的主要状态、视觉差异和研发关注点收成一张矩阵，方便直接映射到组件库实现。",
    tags: ["Design System", "State Matrix", "Developer Handoff"],
    notes: [
      "研发最需要的不是单张漂亮稿，而是知道同一个组件在不同状态下该如何变化。",
      "这张表重点覆盖按钮、输入框、chips、卡片和反馈提示等高频组件。",
      "后续可以继续扩展成交互状态机，但当前已足够支撑第一版组件开发。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-14">
          <div class="statusbar">
            <span>System</span>
            <div class="status-icons"><span>State Matrix</span><span>handoff</span></div>
          </div>
          <div class="spec-card">
            <div class="spec-title">Component Matrix</div>
            <div class="spec-table">
              <div class="spec-table-row header"><div>组件</div><div>核心状态</div><div>视觉规则</div><div>研发备注</div></div>
              <div class="spec-table-row"><div><span class="token-pill">Button / Primary</span></div><div class="tiny">default / pressed / disabled</div><div class="tiny">按下微缩放，禁用降至 42% opacity</div><div class="tiny">高度固定 52，圆角 16</div></div>
              <div class="spec-table-row"><div><span class="token-pill">Input Field</span></div><div class="tiny">default / focus / error / disabled</div><div class="tiny">focus 加 ring，error 改描边与文案</div><div class="tiny">固定顶部 label，不用浮动标签</div></div>
              <div class="spec-table-row"><div><span class="token-pill">Chip</span></div><div class="tiny">default / selected / warning</div><div class="tiny">selected 强调底色，warning 带功能色</div><div class="tiny">高度 32，圆角 12</div></div>
              <div class="spec-table-row"><div><span class="token-pill">Route Card</span></div><div class="tiny">default / pressed / loading</div><div class="tiny">pressed 弱视差，loading skeleton</div><div class="tiny">卡片与封面分层渲染</div></div>
              <div class="spec-table-row"><div><span class="token-pill">Feedback Banner</span></div><div class="tiny">success / warning / error</div><div class="tiny">颜色 + 文案 + 图标三重表达</div><div class="tiny">不可只靠颜色区分</div></div>
            </div>
          </div>
          <div class="spec-grid-2">
            <div class="spec-card stack-12">
              <div class="spec-title">Button State Preview</div>
              <button class="button-primary">Default</button>
              <button class="button-primary" style="filter:brightness(1.06); transform:scale(0.99);">Pressed</button>
              <button class="button-primary" style="opacity:0.42;">Disabled</button>
            </div>
            <div class="spec-card stack-12">
              <div class="spec-title">Input State Preview</div>
              <div class="field"><span class="field-label">Default</span><div class="field-value">请输入标题</div></div>
              <div class="field" style="border-color:rgba(40,84,67,0.22); box-shadow:0 0 0 3px rgba(61,122,97,0.12);"><span class="field-label" style="color:var(--pine-700);">Focus</span><div class="field-value">北岭雾海穿越线</div></div>
              <div class="field" style="border-color:rgba(190,77,63,0.22);"><span class="field-label" style="color:var(--error);">Error</span><div class="field-value">标题不能为空</div></div>
            </div>
          </div>
        </div>
      </div>
    `
  },
  ds_sizing_annotations: {
    title: "设计系统 · 尺寸标注",
    subtitle: "尺寸标注页把关键组件的高度、边距、圆角、点击热区和屏幕安全区明确下来，便于前端与设计统一实现。",
    tags: ["Design System", "Sizing", "Layout Handoff"],
    notes: [
      "研发最常返工的问题往往来自尺寸与间距没有讲清楚，而不是视觉方向本身。",
      "这里优先标注最常复用的按钮、输入框、卡片、导航和页面边距。",
      "这页适合作为后续 Figma 标注或代码常量文件的直接参考。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-14">
          <div class="statusbar">
            <span>System</span>
            <div class="status-icons"><span>Sizing</span><span>handoff</span></div>
          </div>
          <div class="measure-box" style="height:220px;">
            <div class="spec-title">Page Container</div>
            <div class="sheet" style="position:absolute; left:20px; right:20px; top:54px; bottom:20px; padding:18px;">
              <div class="route-title" style="margin:0 0 8px;">页面安全区域示意</div>
              <div class="tiny">左右边距 20，模块主间距 16，大区块间距 24-32。</div>
            </div>
            <div class="measure-line-h" style="left:20px; right:20px; top:40px;"></div>
            <div class="measure-chip" style="left:164px; top:20px;">左右边距 20</div>
            <div class="measure-line-v" style="left:10px; top:54px; bottom:20px;"></div>
            <div class="measure-chip" style="left:18px; top:126px;">内容高随页面滚动</div>
          </div>
          <div class="spec-grid-2">
            <div class="measure-box" style="height:200px;">
              <div class="spec-title">Primary Button</div>
              <button class="button-primary" style="position:absolute; left:24px; right:24px; top:76px;">开始记录</button>
              <div class="measure-line-v" style="right:12px; top:76px; height:52px;"></div>
              <div class="measure-chip" style="right:20px; top:88px;">H 52</div>
              <div class="measure-line-h" style="left:24px; right:24px; top:144px;"></div>
              <div class="measure-chip" style="left:110px; top:150px;">Radius 16</div>
            </div>
            <div class="measure-box" style="height:200px;">
              <div class="spec-title">Input Field</div>
              <div class="field" style="position:absolute; left:24px; right:24px; top:76px;">
                <span class="field-label">路线名称</span>
                <div class="field-value">输入框高度由内容 + padding 决定</div>
              </div>
              <div class="measure-chip" style="right:22px; top:84px;">Padding 14 / 16</div>
              <div class="measure-chip" style="left:108px; top:152px;">Radius 18</div>
            </div>
          </div>
          <div class="spec-card">
            <div class="spec-title">Reference Metrics</div>
            <div class="spec-table">
              <div class="spec-table-row header"><div>元素</div><div>尺寸</div><div>用途</div><div>备注</div></div>
              <div class="spec-table-row"><div>Tap Target</div><div class="tiny">44 × 44 min</div><div class="tiny">所有可点击元素</div><div class="tiny">记录页高频按钮建议更大</div></div>
              <div class="spec-table-row"><div>Bottom Nav</div><div class="tiny">约 72-84 高</div><div class="tiny">4 主导航</div><div class="tiny">含安全区留白</div></div>
              <div class="spec-table-row"><div>Card Radius</div><div class="tiny">16</div><div class="tiny">路线卡 / 列表卡</div><div class="tiny">抽屉可提升到 24</div></div>
              <div class="spec-table-row"><div>Hero Cover</div><div class="tiny">170-224 高</div><div class="tiny">欢迎页 / 路线详情首屏</div><div class="tiny">随场景变体调整</div></div>
            </div>
          </div>
        </div>
      </div>
    `
  },
  ds_token_mapping: {
    title: "设计系统 · Token 命名映射",
    subtitle: "这张页把视觉设计里的颜色、间距、圆角、阴影和动效，映射为可在代码中直接使用的 token 命名。",
    tags: ["Design System", "Tokens", "Engineering Mapping"],
    notes: [
      "设计语言如果不能落到 token，就很难稳定进入代码实现。",
      "命名以语义优先，不直接把 raw hex 或具体像素写死在业务组件中。",
      "后续可以把这张表同步成 JSON / CSS Variables / Tailwind theme / native theme 文件。 "
    ],
    render: () => `
      <div class="screen">
        <div class="safe stack-14">
          <div class="statusbar">
            <span>System</span>
            <div class="status-icons"><span>Token Map</span><span>handoff</span></div>
          </div>
          <div class="spec-card">
            <div class="spec-title">Color Tokens</div>
            <div class="spec-table">
              <div class="spec-table-row header"><div>视觉名称</div><div>推荐 Token</div><div>值</div><div>使用场景</div></div>
              <div class="spec-table-row"><div>Pine 700</div><div><span class="token-pill">color.brand.primary</span></div><div class="tiny">#285443</div><div class="tiny">主按钮、主操作、激活态</div></div>
              <div class="spec-table-row"><div>Mist 100</div><div><span class="token-pill">color.surface.canvas</span></div><div class="tiny">#F4F6F3</div><div class="tiny">页面浅背景、输入底色</div></div>
              <div class="spec-table-row"><div>Error</div><div><span class="token-pill">color.feedback.error</span></div><div class="tiny">#BE4D3F</div><div class="tiny">错误态、危险提醒</div></div>
            </div>
          </div>
          <div class="spec-grid-2">
            <div class="spec-card">
              <div class="spec-title">Spacing & Radius</div>
              <div class="spec-table">
                <div class="spec-table-row header"><div>值</div><div>Token</div><div>用法</div><div>说明</div></div>
                <div class="spec-table-row"><div>8</div><div><span class="token-pill">space.8</span></div><div class="tiny">紧密信息间距</div><div class="tiny">标签、辅助文本</div></div>
                <div class="spec-table-row"><div>16</div><div><span class="token-pill">space.16</span></div><div class="tiny">模块默认间距</div><div class="tiny">页面高频</div></div>
                <div class="spec-table-row"><div>20</div><div><span class="token-pill">radius.md</span></div><div class="tiny">中型容器</div><div class="tiny">sheet / spec card</div></div>
              </div>
            </div>
            <div class="spec-card">
              <div class="spec-title">Shadow & Motion</div>
              <div class="spec-table">
                <div class="spec-table-row header"><div>视觉项</div><div>Token</div><div>值</div><div>说明</div></div>
                <div class="spec-table-row"><div>Card Shadow</div><div><span class="token-pill">shadow.card</span></div><div class="tiny">0 18 45 rgba(17,23,20,.15)</div><div class="tiny">信息卡片</div></div>
                <div class="spec-table-row"><div>Floating Shadow</div><div><span class="token-pill">shadow.float</span></div><div class="tiny">0 24 80 rgba(8,12,10,.26)</div><div class="tiny">浮层 / 设备框</div></div>
                <div class="spec-table-row"><div>Page Motion</div><div><span class="token-pill">motion.page.standard</span></div><div class="tiny">220ms</div><div class="tiny">页面切换</div></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    `
  }
};

function renderBoard(screenName) {
  const current = screens[screenName] || screens.welcome;
  const app = document.getElementById("app");

  app.innerHTML = `
    <div class="board">
      <div class="board-shell">
        <section class="headline-panel">
          <div>
            <div class="eyebrow">TrailNote Shared Mobile UI</div>
            <h1 class="hero-title">${current.title}</h1>
            <div class="hero-subtitle">${current.subtitle}</div>
            <div class="meta-row">
              ${current.tags.map((tag) => `<span class="meta-pill">${tag}</span>`).join("")}
            </div>
          </div>
          <div class="notes">
            ${current.notes.map((note) => `<div class="note">${note}</div>`).join("")}
          </div>
        </section>
        <section class="phone-stage">
          <div class="phone">
            ${current.render()}
          </div>
        </section>
      </div>
    </div>
  `;
}

const params = new URLSearchParams(window.location.search);
renderBoard(params.get("screen") || "welcome");
