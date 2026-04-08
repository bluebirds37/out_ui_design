import { request } from "./http";

export interface RouteSummary {
  id: number;
  title: string;
  authorId: number;
  authorName: string;
  difficulty: string;
  distanceKm: number;
  durationMinutes: number;
  ascentM: number;
  waypointCount: number;
  favoriteCount: number;
  tags: string[];
}

export interface RouteWaypoint {
  id: number;
  title: string;
  waypointType: string;
  description: string;
}

export interface RouteDetail {
  id: number;
  title: string;
  authorId: number;
  authorName: string;
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
  waypoints: RouteWaypoint[];
}

export interface RouteComment {
  id: number;
  routeId: number;
  authorName: string;
  content: string;
  mine: boolean;
  createdAt: string;
}

export interface SearchAuthorItem {
  id: number;
  nickname: string;
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

export interface AuthorProfile {
  id: number;
  nickname: string;
  bio: string | null;
  city: string | null;
  levelLabel: string | null;
  followerCount: number;
  followingCount: number;
  publishedRouteCount: number;
  followed: boolean;
  routes: RouteSummary[];
}

export interface MyProfile {
  id: number;
  nickname: string;
  avatarUrl?: string | null;
  bio: string | null;
  city: string | null;
  levelLabel: string | null;
  publishedRouteCount: number;
  favoriteRouteCount: number;
}

interface PageResponse<T> {
  records: T[];
  total: number;
  page: number;
  pageSize: number;
}

export interface LoginResponse {
  accessToken: string;
  nickname: string;
}

export interface CreatorRouteRow {
  id: number;
  title: string;
  status: string;
  distanceKm: number;
  durationMinutes: number;
  waypointCount: number;
  favoriteCount: number;
  commentCount: number;
  tags: string[];
  updatedAt: string;
}

export interface DraftWaypointInput {
  id: number | null;
  title: string;
  waypointType: string;
  description: string;
  latitude: number;
  longitude: number;
  altitudeM: number | null;
  sortOrder: number;
  mediaList: DraftMediaInput[];
}

export interface DraftMediaInput {
  id: number | null;
  mediaType: "PHOTO" | "VIDEO";
  coverUrl: string;
  mediaUrl: string;
  durationSeconds: number | null;
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
  waypoints: DraftWaypointInput[];
  updatedAt: string;
}

export interface PublishRouteResult {
  routeId: number;
  status: string;
  updatedAt: string;
}

function normalizeWaypoint<T extends { waypointType?: string; type?: string }>(waypoint: T) {
  return {
    ...waypoint,
    waypointType: waypoint.waypointType || waypoint.type || "VIEWPOINT",
  };
}

function normalizeRouteDetail(route: RouteDetail) {
  return {
    ...route,
    waypoints: (route.waypoints || []).map((waypoint) => normalizeWaypoint(waypoint)),
  };
}

function normalizeDraftDetail(draft: RouteDraftDetail | null) {
  if (!draft) {
    return null;
  }

  return {
    ...draft,
    waypoints: (draft.waypoints || []).map((waypoint) => normalizeWaypoint(waypoint)),
  };
}

export const api = {
  login(account: string, password: string) {
    return request<LoginResponse>("/api/auth/login", {
      method: "POST",
      body: { account, password },
      skipAuth: true,
    });
  },
  featuredRoutes() {
    return request<RouteSummary[]>("/api/routes/featured");
  },
  routes(page = 1, pageSize = 6) {
    return request<PageResponse<RouteSummary>>("/api/routes", {
      query: { page, pageSize },
    });
  },
  routeDetail(routeId: number) {
    return request<RouteDetail>(`/api/routes/${routeId}`).then(normalizeRouteDetail);
  },
  routeComments(routeId: number, page = 1, pageSize = 20) {
    return request<PageResponse<RouteComment>>(`/api/routes/${routeId}/comments`, {
      query: { page, pageSize },
    });
  },
  toggleRouteFavorite(routeId: number) {
    return request<{ favorited: boolean; favoriteCount: number }>(`/api/routes/${routeId}/favorite`, {
      method: "POST",
    });
  },
  addRouteComment(routeId: number, content: string) {
    return request<{ commentCount: number }>(`/api/routes/${routeId}/comments`, {
      method: "POST",
      body: { content },
    });
  },
  search(keyword: string) {
    return request<SearchResult>("/api/search", {
      query: { q: keyword },
    });
  },
  searchMap(keyword: string) {
    return request<MapRouteItem[]>("/api/search/map", {
      query: { q: keyword },
    });
  },
  authorProfile(authorId: number) {
    return request<AuthorProfile>(`/api/authors/${authorId}`);
  },
  toggleAuthorFollow(authorId: number) {
    return request<{ followed: boolean; followerCount: number }>(`/api/authors/${authorId}/follow`, {
      method: "POST",
    });
  },
  myProfile() {
    return request<MyProfile>("/api/me");
  },
  updateMyProfile(payload: {
    nickname: string;
    avatarUrl: string;
    bio: string;
    city: string;
    levelLabel: string;
  }) {
    return request<MyProfile>("/api/me", {
      method: "PUT",
      body: payload,
    });
  },
  myFavorites(page = 1, pageSize = 6) {
    return request<PageResponse<RouteSummary>>("/api/me/favorites", {
      query: { page, pageSize },
    });
  },
  creatorRoutes(page = 1, pageSize = 6) {
    return request<PageResponse<CreatorRouteRow>>("/api/creator/routes", {
      query: { page, pageSize },
    });
  },
  creatorCurrentDraft() {
    return request<RouteDraftDetail | null>("/api/creator/drafts/current").then(normalizeDraftDetail);
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
    waypoints: DraftWaypointInput[];
  }) {
    return request<RouteDraftDetail>("/api/creator/drafts/save", {
      method: "POST",
      body: payload,
    }).then((draft) => normalizeDraftDetail(draft) as RouteDraftDetail);
  },
  submitForReview(routeId: number) {
    return request<PublishRouteResult>(`/api/creator/drafts/${routeId}/publish`, {
      method: "POST",
    });
  },
};
