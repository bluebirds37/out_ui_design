# TrailNote 共享网络层契约（多端）

## 1. 目标

本契约用于统一 iOS、Android、H5、小程序与后台前端的请求封装、认证注入、超时、重试、错误处理与分页口径，避免每端自行约定后在联调阶段出现行为分叉。

## 2. 统一分层建议

### 2.1 推荐结构

- `contracts`
  - 共享实体、分页结构、枚举、错误码语义
- `network/core`
  - `HttpClient`
  - `RequestOptions`
  - `ApiClientError`
  - query builder / timeout / retry / auth hook
- `network/services`
  - `routeService`
  - `profileService`
  - `creatorService`
  - `adminRouteService`
- `state`
  - 页面状态 / ViewModel / store / composable

### 2.2 职责边界

- `HttpClient` 只负责传输层能力，不包含业务字段拼装
- `service` 负责路径、query、body 与领域语义映射
- `state` 负责页面 loading/error/empty/success 的编排
- UI 层不直接 `fetch` / `URLSession` / `OkHttp`

## 3. 请求规范

### 3.1 Base URL

- 业务端统一读取业务 API base URL
- 管理端统一读取 admin API base URL
- Base URL 末尾不保留 `/`
- path 统一以 `/` 开头

### 3.2 请求选项

统一建议字段：

- `method`
- `path`
- `query`
- `body`
- `headers`
- `timeoutMs`
- `skipAuth`
- `retry`

### 3.3 Query 规则

- `null | undefined | ""` 不拼入 query
- 数组 query 使用重复 key 形式
  - 例如：`?status=PUBLISHED&status=REJECTED`
- 布尔值转字符串 `true/false`
- 分页统一使用 `page`、`pageSize`

### 3.4 Body 规则

- JSON 请求统一 `Content-Type: application/json`
- 文件上传后续单独扩展 multipart 能力
- 空 body 的 `POST`/`PUT`/`DELETE` 不强制发送空对象

## 4. 认证注入

### 4.1 Token 注入规则

- 默认从本地安全存储注入 `Authorization: Bearer <token>`
- 支持 `skipAuth`，供健康检查、登录等匿名接口使用
- token 刷新策略后续独立扩展，不阻塞当前统一封装基线

### 4.2 本地存储建议

- iOS：Keychain
- Android：EncryptedSharedPreferences / DataStore + Keystore
- H5：admin：localStorage 仅作开发阶段方案，正式版建议迁移安全存储策略
- 小程序：`wx.setStorage` + token 失效后及时清理

## 5. 超时与重试

### 5.1 默认超时

- 普通 JSON 请求：`8000ms`
- 地图/大列表查询：可按页面放宽到 `10000-12000ms`
- 媒体上传：单独配置，不复用普通请求超时

### 5.2 默认重试

- 仅默认对幂等请求启用：`GET`
- 默认重试次数：`1`
- 默认退避：`350ms * attempt`
- 默认可重试状态：
  - `408`
  - `429`
  - `500`
  - `502`
  - `503`
  - `504`

### 5.3 不默认重试

- `POST / favorite / follow / comment`
- `PUT / profile`
- `POST / drafts/save`

这些动作若需要离线重放或幂等保护，应通过业务层任务队列实现，而不是简单传输层重试。

## 6. 响应与错误归一

### 6.1 响应壳

- 统一响应壳仍使用 `ApiResponse<T>`
  - `success`
  - `code`
  - `message`
  - `data`

### 6.2 统一错误对象

- `code`
- `message`
- `status`
- `requestId`（若服务端返回）

客户端统一包装为 `ApiClientError` 或等价结构，不直接把原始平台异常抛给页面层。

### 6.3 页面展示建议

- 超时：提示“请求超时，请稍后重试”
- 网络中断：提示“当前网络不可用，请检查连接”
- 429：提示“请求过于频繁，请稍后再试”
- 5xx：提示“服务暂时不可用，请稍后重试”
- 业务错误：优先展示服务端 `message`

## 7. 分页规范

- 请求：`page`、`pageSize`
- 响应：
  - `records`
  - `total`
  - `page`
  - `pageSize`
- 页面层不直接依赖平台分页组件的私有结构

## 8. 当前仓库落地状态

### 8.1 H5 参考实现

- 已新增 `clients/h5-web/src/lib/http.ts`
- 已将 `clients/h5-web/src/api.ts` 切换为基于共享 `HttpClient` 的 service 封装
- 已支持：
  - base URL 归一
  - query builder
  - JSON body 自动处理
  - 超时控制
  - GET 请求轻量重试
  - 统一错误对象
  - token 注入 hook

### 8.2 后续多端对齐建议

- iOS：封装 `NetworkClient` + `RequestBuilder`，保持与本文同名字段
- Android：封装 `TrailNoteHttpClient` + `Service` 层，保持 query / error / retry 口径一致
- 小程序：封装 `request.ts`，统一 `timeout`、`header`、`ApiResponse` 解包与错误归一
- admin-web：沿用相同错误模型与 query 规则

## 9. 下一步

- 抽离共享错误码字典与用户提示文案
- 为登录与 token 续期补充统一认证流转
- 为媒体上传补充 multipart / task queue / progress 能力
- 为多端补齐统一网络层目录骨架
