## Task Statement

为本项目所有前端、后端代码添加“标准的中文注释”。

## Desired Outcome

先明确注释覆盖范围、注释标准、是否包含测试/原型/历史兼容目录，再产出可执行的批量注释计划或执行规格。

## Stated Solution

用户明确要求使用 `deep-interview`，先澄清需求边界，而不是直接开始大面积改代码。

## Probable Intent Hypothesis

- 希望提升多端项目的可读性与维护性
- 希望通过统一中文注释降低后续理解成本
- 可能并不希望机械地给每一行代码都加注释，而是希望形成“标准化”的注释落点

## Known Facts / Evidence

- 仓库包含多个前后端和多端代码面：
  - `server`
  - `clients/h5-web`
  - `clients/android-native`
  - `clients/ios-native`
  - `vue-vben-admin`
  - `ui`
- 排除 `node_modules`、`target`、`build`、`dist`、`DerivedData` 等目录后，前后端相关源码文件大约 `980` 个
- 仓库里同时存在：
  - 主业务源码
  - 测试代码
  - 原型/UI 设计代码
  - 历史兼容目录，如 `src-legacy-vite-web`

## Constraints

- 这是一次高扩散范围的文本编辑任务，必须先界定范围，否则容易产生大面积低价值改动
- 需要避免对生成代码、第三方依赖、构建产物、历史遗留兼容目录做无差别注释污染
- 需要明确“标准中文注释”的判定口径，否则会陷入机械注释

## Unknowns / Open Questions

- 注释范围是否只限主业务源码
- 是否包含测试代码
- 是否包含原型/UI 代码
- 是否包含历史兼容目录
- “标准中文注释”是偏：
  - 文件头说明
  - 复杂函数/复杂逻辑注释
  - 组件/类/模块职责注释
  - 还是要求高密度全覆盖

## Decision-Boundary Unknowns

- OMX 是否可自行决定哪些文件“不值得注释”
- OMX 是否可以跳过浅显代码，仅给复杂逻辑加注释
- OMX 是否可以按模块分批执行，而不是一次覆盖 980 个文件

## Likely Codebase Touchpoints

- `server/**`
- `clients/h5-web/src/**`
- `clients/android-native/app/src/main/**`
- `clients/ios-native/TrailNoteApp/**`
- `vue-vben-admin/apps/**`
- `vue-vben-admin/packages/**`
- `ui/src/**`
