import { useAccessStore } from '@vben/stores';

export interface ApiResponse<T> {
  success: boolean;
  code: string;
  message: string;
  data: T;
}

export interface AdminLoginParams {
  password?: string;
  username?: string;
}

export interface AdminLoginResult {
  accessToken: string;
  role: string;
  username: string;
  expiresAt: string;
}

export interface AdminCurrentUser {
  userId: string;
  username: string;
  realName: string;
  avatar: string;
  desc: string;
  homePath: string;
  roles: string[];
}

export interface PageResponse<T> {
  records: T[];
  total: number;
  page: number;
  pageSize: number;
}

export interface AdminDashboardOverview {
  totalUsers: number;
  totalRoutes: number;
  publishedRoutes: number;
  pendingOrDraftRoutes: number;
  totalComments: number;
  totalFavorites: number;
  generatedAt: string;
}

export interface AdminUserRow {
  id: number;
  username: string;
  roleCode: string;
  status: "ACTIVE" | "DISABLED";
  createdAt: string;
  updatedAt: string;
}

export interface AdminUserSummary {
  totalUsers: number;
  activeUsers: number;
  disabledUsers: number;
}

export interface AdminRouteRow {
  id: number;
  title: string;
  authorName: string | null;
  status: "DRAFT" | "PENDING_REVIEW" | "PUBLISHED" | "REJECTED" | "ARCHIVED";
  waypointCount: number;
  favoriteCount: number;
  updatedAt: string;
}

export interface AdminRouteStatusResult {
  id: number;
  status: AdminRouteRow["status"];
  publishedAt: string | null;
  updatedAt: string;
}

export interface AdminRouteWaypointRow {
  id: number;
  title: string;
  waypointType: string;
  description: string | null;
  sortOrder: number;
}

export interface AdminRouteDetail {
  id: number;
  title: string;
  authorName: string | null;
  description: string | null;
  status: AdminRouteRow["status"];
  difficulty: string;
  distanceKm: number;
  durationMinutes: number;
  ascentM: number;
  maxAltitudeM: number;
  favoriteCount: number;
  commentCount: number;
  tags: string | null;
  publishedAt: string | null;
  updatedAt: string;
  waypoints: AdminRouteWaypointRow[];
}

export interface AdminCommentRow {
  id: number;
  routeId: number;
  routeTitle: string | null;
  userId: number;
  authorName: string | null;
  content: string;
  createdAt: string;
}

export interface AdminRouteMeta {
  title: string;
  icon?: null | string;
  order?: null | number;
  affixTab?: null | boolean;
}

export interface AdminMenuRoute {
  name: string;
  path: string;
  component: string;
  redirect?: null | string;
  meta: AdminRouteMeta;
  children?: AdminMenuRoute[];
}

const ADMIN_API_BASE =
  import.meta.env.DEV
    ? "/trailnote-admin"
    : ((import.meta.env.VITE_TRAILNOTE_ADMIN_API_URL as string | undefined)?.replace(/\/$/, "") ||
      "http://127.0.0.1:8081");

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const accessStore = useAccessStore();
  const accessToken = accessStore.accessToken;
  const response = await fetch(`${ADMIN_API_BASE}${path}`, {
    headers: {
      ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
      'Content-Type': 'application/json',
      ...(init?.headers ?? {}),
    },
    ...init,
  });

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }

  const payload = (await response.json()) as ApiResponse<T>;
  if (!payload.success) {
    if (payload.code === 'UNAUTHORIZED') {
      accessStore.setAccessToken(null);
      accessStore.setIsAccessChecked(false);
    }
    throw new Error(payload.message || payload.code || 'request failed');
  }
  return payload.data;
}

export const trailnoteAdminApi = {
  async login(data: AdminLoginParams) {
    const result = await request<AdminLoginResult>('/admin/auth/login', {
      method: 'POST',
      body: JSON.stringify(data),
    });
    return result;
  },
  getCurrentUser() {
    return request<AdminCurrentUser>('/admin/auth/profile');
  },
  getAccessCodes() {
    return request<string[]>('/admin/auth/access-codes');
  },
  getMenus() {
    return request<AdminMenuRoute[]>('/admin/auth/menus');
  },
  async logout() {
    await request<void>('/admin/auth/logout', {
      method: 'POST',
    });
    return true;
  },
  getOverview() {
    return request<AdminDashboardOverview>("/admin/dashboard/overview");
  },
  getUsers(page = 1, pageSize = 10) {
    return request<PageResponse<AdminUserRow>>(`/admin/users?page=${page}&pageSize=${pageSize}`);
  },
  getUserDetail(adminUserId: number) {
    return request<AdminUserRow>(`/admin/users/${adminUserId}`);
  },
  getUserSummary() {
    return request<AdminUserSummary>("/admin/users/summary");
  },
  updateUserStatus(adminUserId: number, status: AdminUserRow["status"]) {
    return request<AdminUserRow>(`/admin/users/${adminUserId}/status`, {
      method: "POST",
      body: JSON.stringify({ status }),
    });
  },
  getRoutes(page = 1, pageSize = 10) {
    return request<PageResponse<AdminRouteRow>>(`/admin/routes?page=${page}&pageSize=${pageSize}`);
  },
  getRouteDetail(routeId: number) {
    return request<AdminRouteDetail>(`/admin/routes/${routeId}`);
  },
  updateRouteStatus(routeId: number, status: AdminRouteRow["status"]) {
    return request<AdminRouteStatusResult>(`/admin/routes/${routeId}/status`, {
      method: "POST",
      body: JSON.stringify({ status }),
    });
  },
  getComments(page = 1, pageSize = 10) {
    return request<PageResponse<AdminCommentRow>>(`/admin/comments?page=${page}&pageSize=${pageSize}`);
  },
};
