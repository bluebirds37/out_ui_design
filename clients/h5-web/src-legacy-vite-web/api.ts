export interface PageResponse<T> {
  records: T[];
  total: number;
  page: number;
  pageSize: number;
}

export interface RouteSummary {
  id: number;
  title: string;
  coverUrl: string | null;
  authorName: string;
  authorId: number;
  difficulty: string;
  distanceKm: number;
  durationMinutes: number;
  ascentM: number;
  waypointCount: number;
  favoriteCount: number;
  tags: string[];
}

export interface WaypointMedia {
  id: number;
  mediaType: string;
  coverUrl: string | null;
  mediaUrl: string;
  durationSeconds: number | null;
}

export interface WaypointSummary {
  id: number;
  title: string;
  type: string;
  description: string;
  latitude: number;
  longitude: number;
  altitudeM: number | null;
  sortOrder: number;
  mediaList: WaypointMedia[];
}

export interface RouteDetail {
  id: number;
  title: string;
  authorName: string;
  authorId: number;
  description: string;
  difficulty: string;
  distanceKm: number;
  durationMinutes: number;
  ascentM: number;
  maxAltitudeM: number;
  favoriteCount: number;
  commentCount: number;
  favorited: boolean;
  tags: string[];
  waypoints: WaypointSummary[];
}

export interface FavoriteToggleResult {
  favorited: boolean;
  favoriteCount: number;
}

export interface RouteCommentItem {
  id: number;
  routeId: number;
  userId: number;
  authorName: string;
  authorAvatarUrl: string | null;
  content: string;
  mine: boolean;
  createdAt: string;
}

export interface CommentCreateResult {
  id: number;
  routeId: number;
  commentCount: number;
}

export interface MyProfile {
  id: number;
  nickname: string;
  avatarUrl: string | null;
  bio: string | null;
  city: string | null;
  levelLabel: string | null;
  publishedRouteCount: number;
  favoriteRouteCount: number;
}

export interface AuthorProfile {
  id: number;
  nickname: string;
  avatarUrl: string | null;
  bio: string | null;
  city: string | null;
  levelLabel: string | null;
  followerCount: number;
  followingCount: number;
  publishedRouteCount: number;
  followed: boolean;
  routes: RouteSummary[];
}

export interface FollowToggleResult {
  followed: boolean;
  followerCount: number;
}

export interface SearchAuthorItem {
  id: number;
  nickname: string;
  avatarUrl: string | null;
  bio: string | null;
  city: string | null;
  levelLabel: string | null;
  followerCount: number;
  followed: boolean;
}

export interface SearchWaypointItem {
  id: number;
  routeId: number;
  routeTitle: string;
  title: string;
  waypointType: string;
  description: string;
  latitude: number;
  longitude: number;
}

export interface SearchResult {
  keyword: string;
  routes: RouteSummary[];
  authors: SearchAuthorItem[];
  waypoints: SearchWaypointItem[];
}

export interface MapRouteItem {
  routeId: number;
  title: string;
  coverUrl: string | null;
  authorName: string;
  difficulty: string;
  distanceKm: number;
  durationMinutes: number;
  favoriteCount: number;
  latitude: number;
  longitude: number;
  tags: string[];
}

export interface CreatorRouteRow {
  id: number;
  title: string;
  coverUrl: string | null;
  status: string;
  distanceKm: number;
  durationMinutes: number;
  waypointCount: number;
  favoriteCount: number;
  commentCount: number;
  tags: string[];
  updatedAt: string;
}

export interface RouteDraftMediaInput {
  id: number | null;
  mediaType: "PHOTO" | "VIDEO";
  coverUrl: string;
  mediaUrl: string;
  durationSeconds: number | null;
}

export interface RouteDraftWaypointInput {
  id: number | null;
  title: string;
  waypointType: string;
  description: string;
  latitude: number;
  longitude: number;
  altitudeM: number | null;
  sortOrder: number;
  mediaList: RouteDraftMediaInput[];
}

export interface RouteDraftDetail {
  id: number;
  title: string;
  coverUrl: string | null;
  description: string;
  difficulty: string;
  distanceKm: number;
  durationMinutes: number;
  ascentM: number;
  maxAltitudeM: number;
  status: string;
  tags: string[];
  waypoints: RouteDraftWaypointInput[];
  updatedAt: string;
}

export interface PublishRouteResult {
  routeId: number;
  status: string;
  updatedAt: string;
}

export interface LoginResponse {
  accessToken: string;
  nickname: string;
}

import { createHttpClient } from "./lib/http";

let unauthorizedHandler: (() => void) | null = null;

const http = createHttpClient({
  baseUrl: import.meta.env.VITE_API_BASE_URL ?? "",
  timeoutMs: 8000,
  retry: {
    retries: 1,
    delayMs: 350,
    retryMethods: ["GET"],
    retryStatuses: [408, 429, 500, 502, 503, 504],
  },
  getAccessToken: () => window.localStorage.getItem("trailnote_access_token"),
  onUnauthorized: () => {
    unauthorizedHandler?.();
  },
});

export function setApiUnauthorizedHandler(handler: (() => void) | null) {
  unauthorizedHandler = handler;
}

export const api = {
  login(account: string, password: string) {
    return http.request<LoginResponse>("/api/auth/login", {
      method: "POST",
      skipAuth: true,
      body: { account, password },
    });
  },
  featuredRoutes() {
    return http.request<RouteSummary[]>("/api/routes/featured");
  },
  routes(page = 1, pageSize = 10) {
    return http.request<PageResponse<RouteSummary>>("/api/routes", {
      query: { page, pageSize },
    });
  },
  routeDetail(routeId: number) {
    return http.request<RouteDetail>(`/api/routes/${routeId}`);
  },
  toggleRouteFavorite(routeId: number) {
    return http.request<FavoriteToggleResult>(`/api/routes/${routeId}/favorite`, {
      method: "POST",
    });
  },
  routeComments(routeId: number, page = 1, pageSize = 20) {
    return http.request<PageResponse<RouteCommentItem>>(`/api/routes/${routeId}/comments`, {
      query: { page, pageSize },
    });
  },
  addRouteComment(routeId: number, content: string) {
    return http.request<CommentCreateResult>(`/api/routes/${routeId}/comments`, {
      method: "POST",
      body: { content },
    });
  },
  myProfile() {
    return http.request<MyProfile>("/api/me");
  },
  updateMyProfile(payload: {
    nickname: string;
    avatarUrl: string;
    bio: string;
    city: string;
    levelLabel: string;
  }) {
    return http.request<MyProfile>("/api/me", {
      method: "PUT",
      body: payload,
    });
  },
  myFavorites(page = 1, pageSize = 10) {
    return http.request<PageResponse<RouteSummary>>("/api/me/favorites", {
      query: { page, pageSize },
    });
  },
  authorProfile(authorId: number) {
    return http.request<AuthorProfile>(`/api/authors/${authorId}`);
  },
  toggleAuthorFollow(authorId: number) {
    return http.request<FollowToggleResult>(`/api/authors/${authorId}/follow`, {
      method: "POST",
    });
  },
  search(keyword: string) {
    return http.request<SearchResult>("/api/search", {
      query: { q: keyword },
    });
  },
  searchMap(keyword: string) {
    return http.request<MapRouteItem[]>("/api/search/map", {
      query: { q: keyword },
    });
  },
  creatorRoutes(page = 1, pageSize = 10) {
    return http.request<PageResponse<CreatorRouteRow>>("/api/creator/routes", {
      query: { page, pageSize },
    });
  },
  creatorCurrentDraft() {
    return http.request<RouteDraftDetail | null>("/api/creator/drafts/current");
  },
  saveDraft(payload: {
    routeId?: number | null;
    title: string;
    coverUrl: string;
    description: string;
    difficulty: string;
    distanceKm: number;
    durationMinutes: number;
    ascentM: number;
    maxAltitudeM: number;
    tags: string[];
    waypoints: RouteDraftWaypointInput[];
  }) {
    return http.request<RouteDraftDetail>("/api/creator/drafts/save", {
      method: "POST",
      body: payload,
    });
  },
  publishDraft(routeId: number) {
    return http.request<PublishRouteResult>(`/api/creator/drafts/${routeId}/publish`, {
      method: "POST",
    });
  },
};
