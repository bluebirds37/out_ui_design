# H5 Web Client

技术栈：

- Vue 3
- TypeScript
- Vite

说明：

- 当前已建立正式 H5 工程基础文件
- 已接入真实业务 API：
  - `/api/routes/featured`
  - `/api/routes`
  - `/api/routes/{routeId}`
  - `/api/routes/{routeId}/favorite`
  - `/api/routes/{routeId}/comments`
  - `/api/authors/{authorId}`
  - `/api/authors/{authorId}/follow`
  - `/api/me`
  - `/api/me/favorites`
- 当前可直接通过 `npm run dev` 启动，Vite 已代理 `/api` 到本地 `http://127.0.0.1:8080`
- 后续将继续把 `prototype/` 中的搜索、地图、创作链路迁移进正式工程
