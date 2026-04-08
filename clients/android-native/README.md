# TrailNote Android Client

## 目标

- 使用 `Kotlin + Jetpack Compose` 实现 TrailNote Android 客户端
- 与 iOS / H5 / 微信小程序共享页面命名、交互语义与视觉规则
- 以 `prototype/`、`docs/SHARED_UI_CONTRACT.md`、`docs/SHARED_API_DATA_CONTRACT.md` 为共享基线继续扩展

## 当前状态

- 已补齐 Android SDK 命令行环境、`adb`、`sdkmanager`、`kotlinc`
- 已创建 Compose 工程骨架与设计系统目录
- 已新增第一版统一网络层骨架：`core/network`、`data/service` 与内存版服务实现
- 已补充真实 HTTP service 与 `TrailServiceRegistry`，当前默认走 remote 开发模式，可通过 `BuildConfig.TRAILNOTE_USE_MOCK_DATA` 切回 mock
- 当前适合继续补充页面、网络层、模型层与真实接口联调

## 当前目录

```text
clients/android-native/
  app/
    src/main/
      java/com/trailnote/android/core/network/
      java/com/trailnote/android/data/service/
  gradle/
    wrapper/
  build.gradle.kts
  settings.gradle.kts
  gradle.properties
  local.properties
```

## 契约对齐说明

- 技术栈：`Kotlin + Jetpack Compose`
- 主导航顺序固定：发现 / 搜索 / 记录 / 我的
- 记录链路状态命名：`idle | recording | paused | ended`
- 页面加载状态命名：`idle | loading | success | error`
- 交互动词预留：`toggleFavorite`、`toggleFollow`、`saveDraft`、`submitForReview`

## 后续建议

1. 先验证 wrapper 与基础构建，再接入 Android Studio。
2. 优先扩展发现、搜索、记录、我的四条主链路到可联调版本。
3. 在后续阶段继续拆分 `core`、`designsystem`、`feature`、`data`、`domain` 为更细模块。
4. 下一步优先将 `TrailNoteHttpClient` 接到真实业务 API，并为发现 / 搜索 / 我的落首批 service。
