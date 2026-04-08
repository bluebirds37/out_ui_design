# 环境记录

## 当前检测结果

- Node.js: 已安装
- npm: 已安装
- pnpm: 已安装
- Homebrew: 已安装
- Clash: 已安装并已配置代理
- 本地 HTTP/HTTPS 代理: `127.0.0.1:7897`
- Java Runtime: 已安装并接入项目本地 `.tools/`
- Maven: 已安装并接入项目本地 `.tools/`
- Xcode: 已安装，当前可通过 `DEVELOPER_DIR=/Volumes/ssd/Applications/Xcode.app/Contents/Developer` 使用
- adb / Android platform-tools: 已安装
- MySQL: 已安装并完成初始化

## 说明

- 正式进入 Spring Boot 与 Android 原生开发前，需要先补齐 Java 环境
- 正式进入 iOS 真机 / 模拟器开发前，需要完整 Xcode

## 2026-03-26 追加状态

- Homebrew 正在后台安装 `mysql`
- Homebrew 正在后台安装 `openjdk@17`
- `vue-vben-admin` 已完成 `pnpm install`
- `@vben-core/design` 与 `@vben-core/form-ui` 已单独构建成功
- `@vben-core/popup-ui`、`@vben-core/layout-ui`、`@vben-core/menu-ui`、`@vben-core/tabs-ui` 已单独构建成功
- `@vben-core/composables`、`@vben-core/icons`、`@vben-core/shared`、`@vben-core/typings` 已单独构建成功
- `@vben/web-ele` 已完成生产构建，产物位于 `vue-vben-admin/apps/web-ele/dist`
- `server/` 已升级为 Spring Boot 多模块正式骨架，但当前本机仍缺少可直接执行的 Java / Maven 运行环境
- `clients/h5-web` 已迁入首版正式交互壳并通过 `npm run build`
- MySQL 已安装、启动并完成 `root/root` 初始化
- 已创建数据库：`trailnote`、`trailnote_admin`
- 已执行 `server/sql/init.sql`，首批表结构已创建完成
- `openjdk@17` 与 `maven` 正在通过 Homebrew 安装中，当前仍未完成可执行环境接入
- 已新增 `scripts/java-env.sh`，会优先接入本地 `.tools/` JDK / Maven，其次回退到 Homebrew 安装路径
- 已下载并解压 Temurin JDK 17 到 `.tools/jdk-17`
- 已下载并解压 Apache Maven 3.9.11 到 `.tools/apache-maven`
- `scripts/java-env.sh` 已可正确导出 `JAVA_HOME` 与 `MAVEN_HOME`
- 已验证 `java -version` 与 `mvn -version`
- 已在 `server/` 下完成 `mvn test`，当前多模块测试全量通过
- 遇到下载慢、依赖拉取超时等网络问题时，可直接使用 Clash 代理 `127.0.0.1:7897`

## 2026-03-27 Android 环境补充

- 已通过 Homebrew 安装 `android-platform-tools`
- 已通过 Homebrew 安装 `android-commandlinetools`
- 已通过 Homebrew 安装 `kotlin`
- 已通过 Homebrew 安装 `gradle`
- 已验证 `adb version`、`sdkmanager --version`、`kotlinc -version`
- 已验证 `gradle -v`
- 已接受 Android SDK licenses
- 已安装 Android SDK 包：
  - `platform-tools`
  - `platforms;android-35`
  - `build-tools;35.0.0`
- Android SDK 当前位于 `~/Library/Android/sdk`
- Android 构建相关联网命令当前可通过 Clash 代理 `127.0.0.1:7897` 加速
- 已检测到微信开发者工具目录：`/Users/blue/Library/Application Support/微信开发者工具`

## 2026-03-29 iOS / 小程序运行补充

- Homebrew 已安装 `xcodegen`
- 完整 Xcode 当前位于 `/Volumes/ssd/Applications/Xcode.app`
- iOS 工程可通过 `DEVELOPER_DIR=/Volumes/ssd/Applications/Xcode.app/Contents/Developer xcodebuild ...` 正常构建
- 可用 iOS 模拟器：`iPhone 17 Pro`，UDID `5D58B478-3AE4-443B-863C-CAC725DD9021`
- 已验证 `TrailNoteApp.app` 可安装并启动到上述模拟器
- 微信开发者工具 App 当前位于 `/Volumes/ssd/Applications/wechatwebdevtools.app`
- 已验证可通过 `open -a /Volumes/ssd/Applications/wechatwebdevtools.app /Volumes/ssd/CodeSpaces/out_ui_design/clients/wechat-miniapp` 打开小程序工程
- 本地业务后端当前可通过 `http://127.0.0.1:8080` 为 iOS / Android / 小程序提供真实接口
- Android 模拟器当前已可用：`TrailNote_API35`
- Android SDK 模拟器当前位于 `~/Library/Android/sdk/emulator/emulator`
- 已验证 `adb devices` 可识别 `emulator-5554`
- 已验证模拟器内业务端需通过 `http://10.0.2.2:8080` 访问本机 Spring Boot API
- Android 客户端已补充明文网络放行配置，当前可直接请求 `10.0.2.2`
- 已验证 Android `app-debug.apk` 可安装并启动到 `TrailNote_API35` 模拟器

## 2026-03-30 H5 浏览器验证补充

- Homebrew 已安装 `google-chrome`
- 已验证 `/Applications/Google Chrome.app/Contents/MacOS/Google Chrome` 可用
- Playwright CLI 当前可通过 Chrome 通道在本机执行 H5 浏览器级快照、点击与截图验证
- H5 本地开发服务当前可通过 `npm run dev -- --host 127.0.0.1 --port 4173` 启动
- H5 浏览器自动化产物当前写入 `/Volumes/ssd/CodeSpaces/out_ui_design/.playwright-cli/`
- 微信开发者工具 CLI 当前继续可用：`/Volumes/ssd/Applications/wechatwebdevtools.app/Contents/MacOS/cli open --project /Volumes/ssd/CodeSpaces/out_ui_design/clients/wechat-miniapp --port 9420 --lang zh`
- 当前微信开发者工具 IDE HTTP 服务仍为 `http://127.0.0.1:9420`

## 2026-03-30 原生 / 小程序续跑补充

- Android 模拟器 `emulator-5554` 当前仍可用，并已验证最新 `app-debug.apk` 可安装启动
- Android “我的”页最新设备级运行截图位于 `tmp/android-mine-pass6.png`
- Android “我的”页最新 UI dump 位于 `tmp/android-mine-pass6.xml`
- iOS 模拟器 `iPhone 17 Pro` 当前仍为 Booted 状态，最新 `TrailNoteApp.app` 可重新安装启动
- iOS 最新启动后的模拟器截图位于 `tmp/ios-after-install-pass5.png`
- 微信开发者工具 CLI `auto` 当前继续可用，最近一次成功返回时间为 2026-03-30
- 小程序 `project.config.json` 当前仍使用 `touristappid`，即开发者工具游客模式
- 小程序请求层当前在 DevTools / 游客模式下优先尝试 `http://192.168.0.174:8080`，其次回退 `http://127.0.0.1:8080`
- 小程序请求层当前在 DevTools / 游客模式下默认请求超时为 `3500ms`
- 小程序启动与请求日志当前会输出 runtime profile、候选 base URL 与失败尝试顺序，便于在开发者工具控制台定位 timeout
- H5 当前已接通真实登录接口 `/api/auth/login`，本地 token 存储 key 为 `trailnote_access_token`
- iOS 当前已接通真实登录接口 `/api/auth/login`，本地 token 存储 key 为 `trailnote_access_token`
- Android 当前已接通真实登录接口 `/api/auth/login`，本地 token 存储 key 为 `trailnote_access_token`
- Android 模拟器 `TrailNote_API35` 已重新启动并可完成 `adb install -r` 安装验证
- iOS 模拟器当前登录页截图位于 `tmp/ios-login-sync-20260330.png`
- Android 模拟器当前登录页截图位于 `tmp/android-login-sync-20260330.png`
- iOS 模拟器当前会话版截图位于 `tmp/ios-session-pass-20260330.png`
- Android 模拟器当前会话版截图位于 `tmp/android-session-pass-20260330.png`
- 三端当前均已具备未授权退登录兜底基础：H5 通过 HTTP client 回调，iOS 通过 `NotificationCenter`，Android 通过可观察 session store
- `clients/wechat-miniapp` 原生小程序目录已删除，后续微信小程序构建统一由 `clients/h5-web` 的 uni-app 工程产出
- `clients/h5-web` 当前已切换为 uni-app Vue3/Vite 工程
- `clients/h5-web/src-legacy-vite-web/` 当前保留原 H5 Vite 源码备份，便于继续迁移未覆盖细节
- uni-app 当前可用命令：
  - `npm run dev:h5`
  - `npm run build:h5`
  - `npm run dev:mp-weixin`
  - `npm run build:mp-weixin`
- uni-app H5 构建产物当前位于 `clients/h5-web/dist/build/h5/`
- uni-app 微信小程序构建产物当前位于 `clients/h5-web/dist/build/mp-weixin/`

## 2026-03-31 H5 联调补充

- 本地业务后端已再次通过 `mvn -pl api spring-boot:run` 启动，`http://127.0.0.1:8080/api/public/health` 当前可访问
- 本地管理端已再次通过 `mvn -pl admin-api spring-boot:run` 启动，`http://127.0.0.1:8081/actuator/health` 当前可访问
- uni-app H5 开发服务当前可通过 `npm run dev:h5` 启动在 `http://127.0.0.1:4173/`
- `clients/h5-web/vite.config.ts` 当前已为 H5 本地联调配置 `/api` -> `http://127.0.0.1:8080` 代理
- `clients/h5-web/src/shared/http.ts` 当前对 H5 端优先尝试同源空 base URL，避免浏览器开发态 CORS
- 2026-03-31 浏览器真点验证产物位于 `.playwright-cli/`，已覆盖登录、记录页、创作台联动
- `clients/h5-web` 当前支持本地 H5 URL 调试参数：
  - `debugToken=<token>`
  - `debugScreen=discover|search|profile|record-live|creator-studio|map-results|add-waypoint`
- uni-app H5 重点接口回归脚本当前位于 `scripts/uniapp-h5-api-check.sh`
