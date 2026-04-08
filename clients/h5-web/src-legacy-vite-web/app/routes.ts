export type RouteName =
  | "welcome"
  | "login"
  | "discover"
  | "search"
  | "map-results"
  | "route-detail"
  | "author-profile"
  | "record-live"
  | "add-waypoint"
  | "creator-studio"
  | "profile";

export const pageOptions: Array<{ route: RouteName; label: string }> = [
  { route: "welcome", label: "欢迎" },
  { route: "login", label: "登录" },
  { route: "discover", label: "发现" },
  { route: "search", label: "搜索" },
  { route: "map-results", label: "地图结果" },
  { route: "route-detail", label: "路线详情" },
  { route: "author-profile", label: "作者主页" },
  { route: "record-live", label: "轨迹记录" },
  { route: "add-waypoint", label: "新增点位" },
  { route: "creator-studio", label: "创作台" },
  { route: "profile", label: "我的" },
];
