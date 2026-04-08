# TrailNote iOS Client

TrailNote iOS 客户端使用 SwiftUI，并持续对齐共享契约与 `prototype/` 真源。

## 目标

- 使用 `SwiftUI` 实现 TrailNote iOS 客户端。
- 与 Android / H5 / 小程序共享页面命名、交互语义与视觉规则。
- 以 `prototype/`、`docs/SHARED_UI_CONTRACT.md`、`docs/SHARED_API_DATA_CONTRACT.md` 为共享基线继续扩展。

## 当前状态

- 已创建 SwiftUI 源码骨架与设计系统目录。
- 已提供根导航容器与四主页面占位视图：发现 / 搜索 / 记录 / 我的。
- 已落地基础设计系统：颜色、间距、圆角、阴影、字号。
- 已落地共享组件：主按钮、路线卡片、品牌页头。
- 已新增第一版统一网络层骨架：`Core/Network`、`Core/Services` 与内存版 service。
- 已补充 `TrailEnvironment`、`TrailServiceRegistry` 与真实 HTTP service，当前默认走 remote 开发模式，也可切回 mock。
- 当前未生成 `.xcodeproj`，后续可使用 `project.yml`（XcodeGen）生成正式工程。

## 当前目录

```text
clients/ios-native/
  TrailNoteApp/
    App/                     # App 入口与根容器
    Core/                    # 状态枚举、导航语义、网络层与服务契约
      Navigation/           # 根导航相关枚举
      Network/              # 请求封装、错误对象、分页包裹
      Services/             # 业务 service 协议与 mock 数据源
    DesignSystem/            # Token 映射与共享组件（按钮、卡片、品牌页头）
    Features/
      Discover/
      Search/
      Record/
      Mine/
    Resources/               # Info.plist 与后续资产目录
  project.yml
```

## 契约对齐说明

- 技术栈：`SwiftUI`（对齐 `docs/UNIFIED_DEVELOPMENT_EXECUTION_PLAN.md`）。
- 主导航顺序固定：发现 / 搜索 / 记录 / 我的（对齐 `docs/SHARED_UI_CONTRACT.md`）。
- 记录链路状态命名：`idle | recording | paused | ended`。
- 页面加载状态命名：`idle | loading | success | error`。
- 交互动词预留：`toggleFavorite`、`toggleFollow`、`saveDraft`、`submitForReview`。
- 视觉 token 已映射品牌主色、浅色画布、卡片边框与按钮高度约束。

## 后续建议

1. 使用 `project.yml` 生成 Xcode 工程并补齐 `Assets.xcassets`。
2. 优先将 `TrailNetworkClient` 接到真实业务 API，并把发现 / 搜索 / 我的切到 service 层取数。
3. 继续扩展发现、搜索、记录、我的四条主链路到可联调版本。
