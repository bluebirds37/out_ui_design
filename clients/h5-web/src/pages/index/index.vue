<script setup lang="ts">
import { computed, reactive, ref } from "vue";
import { onLoad, onUnload } from "@dcloudio/uni-app";
import {
  api,
  type AuthorProfile,
  type CreatorRouteRow,
  type MapRouteItem,
  type MyProfile,
  type RouteComment,
  type RouteDetail,
  type RouteDraftDetail,
  type RouteSummary,
  type SearchResult,
} from "@/shared/api";
import { difficultyLabel, formatDate, formatDuration, routeStatusLabel, waypointTypeLabel } from "@/shared/formatters";
import { readDebugParams, type DebugScreenTarget } from "@/shared/local-debug";
import { clearAccessToken, getAccessToken, onUnauthorized, setAccessToken } from "@/shared/session";

type PrimaryScreen = "discover" | "search" | "profile";
type SecondaryScreen =
  | "route-detail"
  | "author-profile"
  | "record-live"
  | "creator-studio"
  | "map-results"
  | "add-waypoint";
type ScreenName = "welcome" | "login" | PrimaryScreen | SecondaryScreen;
type RecordingStatus = "idle" | "recording" | "paused" | "ended";

const currentScreen = ref<ScreenName>("welcome");
const lastPrimaryScreen = ref<PrimaryScreen>("discover");
const secondaryBackScreen = ref<ScreenName>("profile");
const pageReady = ref(false);

const loginForm = reactive({
  account: "",
  password: "",
});

const authState = reactive({
  initializing: false,
  loggingIn: false,
  error: "",
});

const discoverState = reactive({
  loading: false,
  error: "",
  featured: [] as RouteSummary[],
  routes: [] as RouteSummary[],
});

const searchState = reactive({
  loading: false,
  error: "",
  keyword: "山脊",
  result: null as SearchResult | null,
});

const mapState = reactive({
  loading: false,
  error: "",
  items: [] as MapRouteItem[],
});

const detailState = reactive({
  loading: false,
  error: "",
  route: null as RouteDetail | null,
  comments: [] as RouteComment[],
  commentsLoading: false,
  commentsError: "",
  commentDraft: "",
  submittingComment: false,
});

const authorState = reactive({
  loading: false,
  error: "",
  profile: null as AuthorProfile | null,
});

const profileState = reactive({
  loading: false,
  error: "",
  loaded: false,
  saving: false,
  profile: null as MyProfile | null,
  favoritesLoading: false,
  favoritesError: "",
  favorites: [] as RouteSummary[],
});

const profileEditor = reactive({
  visible: false,
  nickname: "",
  avatarUrl: "",
  bio: "",
  city: "",
  levelLabel: "",
  nicknameTouched: false,
  bioTouched: false,
});

const creatorState = reactive({
  loading: false,
  error: "",
  saving: false,
  publishing: false,
  draft: null as RouteDraftDetail | null,
  routes: [] as CreatorRouteRow[],
});

const creatorForm = reactive({
  title: "新的山脊测试路线",
  description: "基于当前正式原型继续推进的创作草稿，用于验证 uni-app 统一创作链路。",
  difficulty: "INTERMEDIATE",
  distanceKm: 9.8,
  durationMinutes: 310,
  ascentM: 620,
  maxAltitudeM: 1480,
  tags: "山脊线,测试草稿,日出机位",
});

const waypointDraft = reactive({
  type: "VIEWPOINT",
  expandedTypes: false,
  title: "观景补给点",
  description: "从记录链路带入创作台的默认点位说明。",
  titleTouched: false,
  descriptionTouched: false,
});

const recordingState = reactive({
  status: "idle" as RecordingStatus,
  elapsedSeconds: 0,
  distanceKm: 0,
  ascentM: 0,
  altitudeM: 986,
  waypointCount: 0,
});

let recordingTimer: ReturnType<typeof setInterval> | null = null;

const pageTitle = computed(() => {
  const titleMap: Record<ScreenName, string> = {
    welcome: "欢迎来到 TrailNote",
    login: "登录 TrailNote",
    discover: "发现",
    search: "搜索",
    profile: "我的",
    "route-detail": "路线详情",
    "author-profile": "作者主页",
    "record-live": "轨迹记录",
    "creator-studio": "创作台",
    "map-results": "地图结果",
    "add-waypoint": "新增点位",
  };
  return titleMap[currentScreen.value];
});

const pageSubtitle = computed(() => {
  const subtitleMap: Record<ScreenName, string> = {
    welcome: "先浏览统一后的产品基调，再进入登录、发现、记录和创作链路。",
    login: "当前 uni-app 工程统一承接 H5 与微信小程序构建，先从真实登录态恢复开始。",
    discover: "统一发现流、详情互动和作者社交链路，继续向 iOS / Android 的信息节奏收敛。",
    search: "同一套页面同时支持路线、作者和点位搜索结果渲染。",
    profile: "会话、收藏、记录入口和创作入口都在这里做统一收口。",
    "route-detail": "收藏、评论、关注作者均直接走当前 Spring Boot 真实接口。",
    "author-profile": "作者资料和路线列表继续沿用统一数据契约。",
    "record-live": "记录页先用本地状态机承接离线过程，再把整理结果带入真实创作接口。",
    "creator-studio": "创作台已接入真实 creator 接口，可保存草稿并提交审核。",
    "map-results": "用真实 /api/search/map 结果继续补齐搜索到地图视角的统一迁移。",
    "add-waypoint": "补齐旧 H5 的新增点位能力，把记录链路和创作链路连接起来。",
  };
  return subtitleMap[currentScreen.value];
});

const canGoBack = computed(() => currentScreen.value !== "welcome" && currentScreen.value !== "login" && !isPrimaryScreen(currentScreen.value));

const currentProfile = computed(
  () =>
    profileState.profile || {
      nickname: "TrailNote User",
      city: "上海",
      levelLabel: "轻中度徒步",
      bio: "偏爱山脊线、湖边营地与日出路线记录",
      publishedRouteCount: 0,
      favoriteRouteCount: 0,
    },
);

const draftTagList = computed(() =>
  creatorForm.tags
    .split(",")
    .map((tag) => tag.trim())
    .filter(Boolean),
);

const recordingStatusLabel = computed(() => {
  const labelMap: Record<RecordingStatus, string> = {
    idle: "待开始",
    recording: "记录中",
    paused: "已暂停",
    ended: "已结束",
  };
  return labelMap[recordingState.status];
});

const recordingElapsedLabel = computed(() => formatDuration(Math.floor(recordingState.elapsedSeconds / 60)));
const waypointTypeOptions = {
  base: ["VIEWPOINT", "SUPPLY", "DANGER"],
  expanded: ["TRAILHEAD", "ENDPOINT", "FORK", "REST", "CAMP", "PHOTO_SPOT"],
};
const waypointTitleError = computed(() =>
  waypointDraft.titleTouched && !waypointDraft.title.trim() ? "请输入点位标题" : "",
);
const waypointDescriptionError = computed(() =>
  waypointDraft.descriptionTouched && !waypointDraft.description.trim() ? "请补充点位说明" : "",
);
const profileNicknameError = computed(() =>
  profileEditor.nicknameTouched && !profileEditor.nickname.trim() ? "昵称不能为空" : "",
);
const profileBioError = computed(() =>
  profileEditor.bioTouched && !profileEditor.bio.trim() ? "请补充个人简介" : "",
);

onUnauthorized(() => {
  logout("登录已失效，请重新登录");
});

onLoad(() => {
  void initializePage();
});

onUnload(() => {
  stopRecordingTicker();
});

function isPrimaryScreen(screen: ScreenName): screen is PrimaryScreen {
  return screen === "discover" || screen === "search" || screen === "profile";
}

async function initializePage() {
  authState.initializing = true;
  authState.error = "";
  try {
    const debugParams = readDebugParams();
    if (debugParams.token) {
      setAccessToken(debugParams.token);
    }

    if (!getAccessToken()) {
      currentScreen.value = "welcome";
      return;
    }

    await bootstrap();
    await applyDebugScreen(debugParams.screen);
  } catch (error) {
    clearAccessToken();
    currentScreen.value = "welcome";
    authState.error = error instanceof Error ? error.message : "登录态恢复失败";
  } finally {
    pageReady.value = true;
    authState.initializing = false;
  }
}

async function bootstrap() {
  await Promise.all([loadDiscover(true), loadSearch(true), loadProfile(true)]);
}

async function applyDebugScreen(target: string) {
  const screen = String(target || "").trim() as DebugScreenTarget;

  if (!screen) {
    navigatePrimary("discover");
    return;
  }

  if (screen === "discover" || screen === "search" || screen === "profile") {
    navigatePrimary(screen);
    return;
  }

  if (screen === "record-live") {
    openRecordLive();
    return;
  }

  if (screen === "creator-studio") {
    await openCreatorStudio();
    return;
  }

  if (screen === "map-results") {
    await openMapResults();
    return;
  }

  if (screen === "add-waypoint") {
    openRecordLive();
    openAddWaypoint();
    return;
  }

  navigatePrimary("discover");
}

async function submitLogin() {
  if (!loginForm.account.trim() || loginForm.password.trim().length < 6) {
    showToast("请先补完整登录信息");
    return;
  }

  authState.loggingIn = true;
  authState.error = "";
  try {
    const session = await api.login(loginForm.account.trim(), loginForm.password);
    setAccessToken(session.accessToken);
    await bootstrap();
    navigatePrimary("discover");
    showToast(`欢迎回来，${session.nickname}`);
  } catch (error) {
    authState.error = error instanceof Error ? error.message : "登录失败";
  } finally {
    authState.loggingIn = false;
  }
}

function navigatePrimary(screen: PrimaryScreen) {
  currentScreen.value = screen;
  lastPrimaryScreen.value = screen;

  if (screen === "discover") {
    void loadDiscover();
  }
  if (screen === "search") {
    void loadSearch();
  }
  if (screen === "profile") {
    void loadProfile();
  }
}

function goBack() {
  if (currentScreen.value === "route-detail" || currentScreen.value === "author-profile") {
    currentScreen.value = lastPrimaryScreen.value;
    return;
  }
  currentScreen.value = secondaryBackScreen.value;
}

function logout(message = "已退出当前账号") {
  clearAccessToken();
  authState.error = "";
  currentScreen.value = "welcome";
  lastPrimaryScreen.value = "discover";
  secondaryBackScreen.value = "profile";
  profileState.loaded = false;
  profileEditor.visible = false;
  authorState.profile = null;
  detailState.route = null;
  creatorState.draft = null;
  stopRecordingTicker();
  resetRecordingState();
  if (message) {
    showToast(message);
  }
}

async function loadDiscover(force = false) {
  if (discoverState.loading || (!force && discoverState.routes.length)) {
    return;
  }

  discoverState.loading = true;
  discoverState.error = "";
  try {
    const [featured, routesPage] = await Promise.all([api.featuredRoutes(), api.routes()]);
    discoverState.featured = featured;
    discoverState.routes = routesPage.records;
  } catch (error) {
    discoverState.error = error instanceof Error ? error.message : "发现页加载失败";
  } finally {
    discoverState.loading = false;
  }
}

async function loadSearch(force = false) {
  if (searchState.loading || (!force && searchState.result)) {
    return;
  }

  searchState.loading = true;
  searchState.error = "";
  try {
    searchState.result = await api.search(searchState.keyword.trim() || "山脊");
  } catch (error) {
    searchState.error = error instanceof Error ? error.message : "搜索失败";
  } finally {
    searchState.loading = false;
  }
}

async function loadMapResults(force = false) {
  if (mapState.loading || (!force && mapState.items.length)) {
    return;
  }

  mapState.loading = true;
  mapState.error = "";
  try {
    mapState.items = await api.searchMap(searchState.keyword.trim() || "山脊");
  } catch (error) {
    mapState.error = error instanceof Error ? error.message : "地图结果加载失败";
  } finally {
    mapState.loading = false;
  }
}

async function searchNow() {
  searchState.result = null;
  await loadSearch(true);
}

async function openMapResults() {
  currentScreen.value = "map-results";
  secondaryBackScreen.value = "search";
  await loadMapResults(true);
}

async function loadProfile(force = false) {
  if (profileState.loading || profileState.favoritesLoading) {
    return;
  }

  if (!force && profileState.loaded) {
    return;
  }

  profileState.loading = true;
  profileState.favoritesLoading = true;
  profileState.error = "";
  profileState.favoritesError = "";
  try {
    const [profile, favorites] = await Promise.all([api.myProfile(), api.myFavorites()]);
    profileState.profile = profile;
    profileState.favorites = favorites.records;
    profileState.loaded = true;
    syncProfileEditor(profile);
  } catch (error) {
    const message = error instanceof Error ? error.message : "我的页面加载失败";
    profileState.error = message;
    profileState.favoritesError = message;
  } finally {
    profileState.loading = false;
    profileState.favoritesLoading = false;
  }
}

function syncProfileEditor(profile: MyProfile) {
  profileEditor.nickname = profile.nickname ?? "";
  profileEditor.avatarUrl = profile.avatarUrl ?? "";
  profileEditor.bio = profile.bio ?? "";
  profileEditor.city = profile.city ?? "";
  profileEditor.levelLabel = profile.levelLabel ?? "";
  profileEditor.nicknameTouched = false;
  profileEditor.bioTouched = false;
}

function openProfileEditor() {
  if (profileState.profile) {
    syncProfileEditor(profileState.profile);
  }
  profileEditor.visible = true;
}

function closeProfileEditor() {
  profileEditor.visible = false;
}

async function saveProfile() {
  profileEditor.nicknameTouched = true;
  profileEditor.bioTouched = true;

  if (profileNicknameError.value || profileBioError.value) {
    showToast("请先补完整资料");
    return;
  }

  profileState.saving = true;
  try {
    const profile = await api.updateMyProfile({
      nickname: profileEditor.nickname.trim(),
      avatarUrl: profileEditor.avatarUrl.trim(),
      bio: profileEditor.bio.trim(),
      city: profileEditor.city.trim(),
      levelLabel: profileEditor.levelLabel.trim(),
    });
    profileState.profile = profile;
    profileState.loaded = true;
    syncProfileEditor(profile);
    profileEditor.visible = false;
    showToast("资料已更新");
  } catch (error) {
    showToast(error instanceof Error ? error.message : "资料保存失败");
  } finally {
    profileState.saving = false;
  }
}

async function openRouteDetail(routeId: number) {
  currentScreen.value = "route-detail";
  detailState.loading = true;
  detailState.error = "";
  detailState.commentsError = "";
  detailState.route = null;
  detailState.comments = [];
  try {
    const route = await api.routeDetail(routeId);
    detailState.route = route;
    await Promise.all([loadRouteComments(routeId), loadAuthorProfile(route.authorId)]);
  } catch (error) {
    detailState.error = error instanceof Error ? error.message : "路线详情加载失败";
  } finally {
    detailState.loading = false;
  }
}

async function loadRouteComments(routeId: number) {
  detailState.commentsLoading = true;
  detailState.commentsError = "";
  try {
    const page = await api.routeComments(routeId);
    detailState.comments = page.records;
  } catch (error) {
    detailState.commentsError = error instanceof Error ? error.message : "评论加载失败";
  } finally {
    detailState.commentsLoading = false;
  }
}

async function toggleFavorite() {
  if (!detailState.route) {
    return;
  }

  try {
    const result = await api.toggleRouteFavorite(detailState.route.id);
    detailState.route = {
      ...detailState.route,
      favorited: result.favorited,
      favoriteCount: result.favoriteCount,
    };
    syncRouteFavorite(detailState.route.id, result.favoriteCount);
    await loadProfile(true);
    showToast(result.favorited ? "已加入收藏" : "已取消收藏");
  } catch (error) {
    showToast(error instanceof Error ? error.message : "收藏操作失败");
  }
}

function syncRouteFavorite(routeId: number, favoriteCount: number) {
  const patch = (route: RouteSummary) =>
    route.id === routeId
      ? {
          ...route,
          favoriteCount,
        }
      : route;

  discoverState.featured = discoverState.featured.map(patch);
  discoverState.routes = discoverState.routes.map(patch);
  if (searchState.result) {
    searchState.result = {
      ...searchState.result,
      routes: searchState.result.routes.map(patch),
    };
  }
  profileState.favorites = profileState.favorites.map(patch);
}

async function submitComment() {
  if (!detailState.route || !detailState.commentDraft.trim()) {
    showToast("请先输入评论内容");
    return;
  }

  detailState.submittingComment = true;
  try {
    await api.addRouteComment(detailState.route.id, detailState.commentDraft.trim());
    detailState.commentDraft = "";
    await loadRouteComments(detailState.route.id);
    const latest = await api.routeDetail(detailState.route.id);
    detailState.route = latest;
    showToast("评论已发布");
  } catch (error) {
    showToast(error instanceof Error ? error.message : "评论发布失败");
  } finally {
    detailState.submittingComment = false;
  }
}

async function loadAuthorProfile(authorId: number) {
  authorState.loading = true;
  authorState.error = "";
  try {
    authorState.profile = await api.authorProfile(authorId);
  } catch (error) {
    authorState.error = error instanceof Error ? error.message : "作者主页加载失败";
  } finally {
    authorState.loading = false;
  }
}

async function openAuthorProfile(authorId: number) {
  currentScreen.value = "author-profile";
  await loadAuthorProfile(authorId);
}

async function toggleFollow() {
  if (!authorState.profile) {
    return;
  }

  try {
    const result = await api.toggleAuthorFollow(authorState.profile.id);
    authorState.profile = {
      ...authorState.profile,
      followed: result.followed,
      followerCount: result.followerCount,
    };
    if (detailState.route && detailState.route.authorId === authorState.profile.id) {
      await loadAuthorProfile(authorState.profile.id);
    }
    showToast(result.followed ? "已关注作者" : "已取消关注");
  } catch (error) {
    showToast(error instanceof Error ? error.message : "关注操作失败");
  }
}

async function openCreatorStudio() {
  secondaryBackScreen.value = "profile";
  currentScreen.value = "creator-studio";
  await loadCreatorData();
}

async function loadCreatorData(force = false) {
  if (creatorState.loading || (!force && creatorState.draft && creatorState.routes.length)) {
    return;
  }

  creatorState.loading = true;
  creatorState.error = "";
  try {
    const [draft, routesPage] = await Promise.all([api.creatorCurrentDraft(), api.creatorRoutes()]);
    creatorState.draft = draft;
    creatorState.routes = routesPage.records;
    if (draft) {
      syncCreatorForm(draft);
    }
  } catch (error) {
    creatorState.error = error instanceof Error ? error.message : "创作台加载失败";
  } finally {
    creatorState.loading = false;
  }
}

function syncCreatorForm(draft: RouteDraftDetail) {
  creatorForm.title = draft.title;
  creatorForm.description = draft.description;
  creatorForm.difficulty = draft.difficulty;
  creatorForm.distanceKm = draft.distanceKm;
  creatorForm.durationMinutes = draft.durationMinutes;
  creatorForm.ascentM = draft.ascentM;
  creatorForm.maxAltitudeM = draft.maxAltitudeM;
  creatorForm.tags = draft.tags.join(",");

  const firstWaypoint = draft.waypoints[0];
  waypointDraft.type = firstWaypoint?.waypointType || "VIEWPOINT";
  waypointDraft.title = firstWaypoint?.title || "观景补给点";
  waypointDraft.description = firstWaypoint?.description || "从记录链路带入创作台的默认点位说明。";
  waypointDraft.titleTouched = false;
  waypointDraft.descriptionTouched = false;
}

function updateCreatorField(field: keyof typeof creatorForm, value: string | number) {
  if (field === "title" || field === "description" || field === "difficulty" || field === "tags") {
    creatorForm[field] = String(value) as never;
    return;
  }
  creatorForm[field] = Number(value || 0) as never;
}

function inputValue(event: Event) {
  return String(((event as Event & { detail?: { value?: string } }).detail?.value ?? "") || "");
}

async function saveCreatorDraft() {
  if (!creatorForm.title.trim() || !creatorForm.description.trim()) {
    showToast("请先补完整草稿信息");
    return;
  }

  creatorState.saving = true;
  try {
    const draft = await api.saveDraft({
      routeId: creatorState.draft?.id ?? null,
      title: creatorForm.title.trim(),
      coverUrl: "",
      description: creatorForm.description.trim(),
      difficulty: creatorForm.difficulty,
      distanceKm: Number(creatorForm.distanceKm),
      durationMinutes: Number(creatorForm.durationMinutes),
      ascentM: Number(creatorForm.ascentM),
      maxAltitudeM: Number(creatorForm.maxAltitudeM),
      tags: draftTagList.value,
      // 记录链路尚未接设备 GPS，本轮先统一沉淀为一个可编辑点位草稿。
      waypoints: [
        {
          id: creatorState.draft?.waypoints[0]?.id ?? null,
          title: waypointDraft.title.trim() || "默认观景点",
          waypointType: waypointDraft.type,
          description: waypointDraft.description.trim() || "从记录流程补进来的默认点位说明",
          latitude: 30.23591,
          longitude: 120.10458,
          altitudeM: Math.round(recordingState.altitudeM || creatorForm.maxAltitudeM || 0),
          sortOrder: 1,
          mediaList: [],
        },
      ],
    });
    creatorState.draft = draft;
    syncCreatorForm(draft);
    await loadCreatorData(true);
    showToast("草稿已保存");
  } catch (error) {
    showToast(error instanceof Error ? error.message : "草稿保存失败");
  } finally {
    creatorState.saving = false;
  }
}

async function submitDraftForReview() {
  if (!creatorState.draft?.id) {
    showToast("请先保存草稿");
    return;
  }

  creatorState.publishing = true;
  try {
    const result = await api.submitForReview(creatorState.draft.id);
    creatorState.draft = {
      ...creatorState.draft,
      status: result.status,
      updatedAt: result.updatedAt,
    };
    await loadCreatorData(true);
    await loadProfile(true);
    showToast("已提交审核");
  } catch (error) {
    showToast(error instanceof Error ? error.message : "提交审核失败");
  } finally {
    creatorState.publishing = false;
  }
}

function openRecordLive() {
  secondaryBackScreen.value = "profile";
  currentScreen.value = "record-live";
}

function openAddWaypoint() {
  secondaryBackScreen.value = "record-live";
  currentScreen.value = "add-waypoint";
}

function startRecording() {
  if (recordingState.status === "recording") {
    return;
  }

  resetRecordingState();
  recordingState.status = "recording";
  recordingState.altitudeM = 986;
  startRecordingTicker();
  showToast("开始记录");
}

function pauseRecording() {
  if (recordingState.status !== "recording") {
    return;
  }
  recordingState.status = "paused";
  stopRecordingTicker();
  showToast("已暂停记录");
}

function resumeRecording() {
  if (recordingState.status !== "paused") {
    return;
  }
  recordingState.status = "recording";
  startRecordingTicker();
  showToast("继续记录");
}

function finishRecording() {
  if (recordingState.status === "idle" || recordingState.status === "ended") {
    return;
  }

  stopRecordingTicker();
  recordingState.status = "ended";

  creatorForm.distanceKm = Number(Math.max(recordingState.distanceKm, 1.2).toFixed(1));
  creatorForm.durationMinutes = Math.max(Math.round(recordingState.elapsedSeconds / 60), 18);
  creatorForm.ascentM = Math.max(recordingState.ascentM, 120);
  creatorForm.maxAltitudeM = Math.max(Math.round(recordingState.altitudeM), 1200);
  creatorForm.tags = "记录整理,uni-app,待审核";
  waypointDraft.type = "VIEWPOINT";
  waypointDraft.title = `记录点位 ${recordingState.waypointCount || 1}`;
  waypointDraft.description = `本次记录共经过 ${recordingState.waypointCount || 1} 个关键点位，已从记录页整理进入创作台。`;
  waypointDraft.titleTouched = false;
  waypointDraft.descriptionTouched = false;

  showToast("记录已结束，可进入创作整理");
}

function toggleExpandedWaypointTypes() {
  waypointDraft.expandedTypes = !waypointDraft.expandedTypes;
}

function selectWaypointType(type: string) {
  waypointDraft.type = type;
}

function saveWaypointToRecording() {
  waypointDraft.titleTouched = true;
  waypointDraft.descriptionTouched = true;

  if (waypointTitleError.value || waypointDescriptionError.value) {
    showToast("请先补完整点位信息");
    return;
  }

  recordingState.waypointCount += 1;
  waypointDraft.expandedTypes = false;
  waypointDraft.titleTouched = false;
  waypointDraft.descriptionTouched = false;
  showToast("点位已保存到本次轨迹");
  currentScreen.value = "record-live";
}

function jumpToCreatorFromRecording() {
  if (recordingState.status !== "ended") {
    finishRecording();
  }
  void openCreatorStudio();
}

function startRecordingTicker() {
  stopRecordingTicker();
  recordingTimer = setInterval(() => {
    recordingState.elapsedSeconds += 12;
    recordingState.distanceKm = Number((recordingState.distanceKm + 0.08).toFixed(2));
    recordingState.ascentM += 7;
    recordingState.altitudeM += recordingState.elapsedSeconds % 48 === 0 ? -3 : 2;
    if (recordingState.elapsedSeconds % 72 === 0) {
      recordingState.waypointCount += 1;
    }
  }, 1000);
}

function stopRecordingTicker() {
  if (recordingTimer) {
    clearInterval(recordingTimer);
    recordingTimer = null;
  }
}

function resetRecordingState() {
  recordingState.status = "idle";
  recordingState.elapsedSeconds = 0;
  recordingState.distanceKm = 0;
  recordingState.ascentM = 0;
  recordingState.altitudeM = 986;
  recordingState.waypointCount = 0;
}

function showToast(title: string) {
  uni.showToast({
    title,
    icon: "none",
    duration: 2200,
  });
}
</script>

<template>
  <scroll-view scroll-y class="page-shell">
    <view class="status-bar"></view>

    <view class="hero-card">
      <view class="hero-row">
        <view class="hero-copy-wrap">
          <text class="hero-eyebrow">TrailNote</text>
          <text class="hero-title">{{ pageTitle }}</text>
          <text class="hero-copy">{{ pageSubtitle }}</text>
        </view>
        <button v-if="canGoBack" class="ghost-button" @tap="goBack">返回</button>
      </view>

      <view v-if="currentScreen !== 'welcome' && currentScreen !== 'login'" class="tab-row">
        <button class="tab-button" :class="{ 'tab-button--active': currentScreen === 'discover' }" @tap="navigatePrimary('discover')">发现</button>
        <button class="tab-button" :class="{ 'tab-button--active': currentScreen === 'search' }" @tap="navigatePrimary('search')">搜索</button>
        <button class="tab-button" :class="{ 'tab-button--active': currentScreen === 'profile' }" @tap="navigatePrimary('profile')">我的</button>
      </view>
    </view>

    <view v-if="authState.initializing && !pageReady" class="panel-card">
      <text class="section-title">正在恢复登录态</text>
      <text class="section-copy">同步发现、搜索和我的页面数据中。</text>
    </view>

    <view v-else-if="currentScreen === 'welcome'" class="panel-card panel-card--login">
      <text class="section-title">分享每一段值得被记住的徒步路线</text>
      <text class="section-copy">记录轨迹、补充关键点位、附上照片和视频，让后来者看见路线真正的样子。</text>
      <view class="stack-actions">
        <button class="primary-button" @tap="currentScreen = 'login'">登录 / 注册</button>
        <button class="secondary-button" @tap="navigatePrimary('discover')">先看看热门路线</button>
      </view>
    </view>

    <view v-else-if="currentScreen === 'login'" class="panel-card panel-card--login">
      <text class="section-title">登录 TrailNote</text>
      <text class="section-copy">当前 uni-app 工程统一使用真实业务登录，再进入发现、搜索、详情、记录和创作流程。</text>

      <view class="field">
        <text class="field-label">账号</text>
        <input v-model="loginForm.account" class="field-input" placeholder="请输入测试账号" />
      </view>

      <view class="field">
        <text class="field-label">密码</text>
        <input v-model="loginForm.password" class="field-input" password placeholder="请输入测试密码" />
      </view>

      <text v-if="authState.error" class="error-text">{{ authState.error }}</text>
      <button class="primary-button" :loading="authState.loggingIn" @tap="submitLogin">登录并同步真实数据</button>
    </view>

    <view v-else-if="currentScreen === 'discover'" class="content-stack">
      <view class="action-grid">
        <view class="action-card">
          <text class="action-card__title">轨迹记录</text>
          <text class="section-copy">把本地记录状态机和创作链路先在统一端跑通。</text>
          <button class="secondary-button" @tap="openRecordLive">进入记录页</button>
        </view>
        <view class="action-card">
          <text class="action-card__title">创作整理</text>
          <text class="section-copy">直接打开真实 creator 接口，继续整理草稿并提审。</text>
          <button class="secondary-button" @tap="openCreatorStudio">进入创作台</button>
        </view>
      </view>

      <view class="section-header">
        <text class="section-title">精选路线</text>
        <button class="ghost-button" @tap="loadDiscover(true)">刷新</button>
      </view>

      <view v-if="discoverState.loading" class="panel-card">
        <text class="section-title">发现页同步中</text>
        <text class="section-copy">正在拉取精选路线与列表。</text>
      </view>
      <view v-else-if="discoverState.error" class="panel-card">
        <text class="section-title">发现页加载失败</text>
        <text class="section-copy">{{ discoverState.error }}</text>
      </view>
      <template v-else>
        <view v-for="route in discoverState.featured" :key="`featured-${route.id}`" class="route-card route-card--hero">
          <text class="route-title">{{ route.title }}</text>
          <text class="route-meta">{{ route.distanceKm }}km · {{ formatDuration(route.durationMinutes) }} · {{ difficultyLabel(route.difficulty) }}</text>
          <text class="route-meta">作者 {{ route.authorName }} · 收藏 {{ route.favoriteCount }}</text>
          <view class="chip-row">
            <text v-for="tag in route.tags" :key="`${route.id}-${tag}`" class="chip">{{ tag }}</text>
          </view>
          <button class="primary-button" @tap="openRouteDetail(route.id)">查看路线详情</button>
        </view>

        <view class="section-header">
          <text class="section-title">继续浏览</text>
        </view>
        <view v-for="route in discoverState.routes" :key="route.id" class="route-card">
          <text class="route-title">{{ route.title }}</text>
          <text class="route-meta">{{ route.distanceKm }}km · 点位 {{ route.waypointCount }} · 收藏 {{ route.favoriteCount }}</text>
          <text class="route-meta">作者 {{ route.authorName }}</text>
          <view class="chip-row">
            <text v-for="tag in route.tags.slice(0, 3)" :key="`${route.id}-${tag}`" class="chip">{{ tag }}</text>
          </view>
          <button class="secondary-button" @tap="openRouteDetail(route.id)">进入互动详情</button>
        </view>
      </template>
    </view>

    <view v-else-if="currentScreen === 'search'" class="content-stack">
      <view class="panel-card">
        <text class="section-title">统一搜索</text>
        <text class="section-copy">同一套 uni-app 页面直接支持 H5 与微信小程序搜索路线、作者和点位。</text>
        <view class="field">
          <text class="field-label">关键词</text>
          <input v-model="searchState.keyword" class="field-input" placeholder="搜索山脊、作者或点位" />
        </view>
        <view class="stack-actions">
          <button class="primary-button" :loading="searchState.loading" @tap="searchNow">执行搜索</button>
          <button class="secondary-button" @tap="openMapResults">查看地图结果</button>
        </view>
      </view>

      <view v-if="searchState.error" class="panel-card">
        <text class="section-title">搜索失败</text>
        <text class="section-copy">{{ searchState.error }}</text>
      </view>

      <view v-if="searchState.result" class="content-stack">
        <view class="section-header">
          <text class="section-title">路线结果</text>
        </view>
        <view v-for="route in searchState.result.routes" :key="`search-route-${route.id}`" class="route-card">
          <text class="route-title">{{ route.title }}</text>
          <text class="route-meta">{{ route.distanceKm }}km · {{ difficultyLabel(route.difficulty) }}</text>
          <button class="secondary-button" @tap="openRouteDetail(route.id)">打开详情</button>
        </view>

        <view class="section-header">
          <text class="section-title">作者结果</text>
        </view>
        <view v-if="searchState.result.authors.length === 0" class="panel-card panel-card--compact">
          <text class="section-copy">暂无作者结果</text>
        </view>
        <view v-for="author in searchState.result.authors" :key="`search-author-${author.id}`" class="panel-card panel-card--compact">
          <text class="route-title">{{ author.nickname }}</text>
          <text class="route-meta">{{ author.city || "未填写城市" }} · 粉丝 {{ author.followerCount }}</text>
          <button class="secondary-button" @tap="openAuthorProfile(author.id)">打开作者主页</button>
        </view>

        <view class="section-header">
          <text class="section-title">点位结果</text>
        </view>
        <view v-if="searchState.result.waypoints.length === 0" class="panel-card panel-card--compact">
          <text class="section-copy">暂无点位结果</text>
        </view>
        <view v-for="waypoint in searchState.result.waypoints" :key="`search-waypoint-${waypoint.id}`" class="panel-card panel-card--compact">
          <text class="route-title">{{ waypoint.title }}</text>
          <text class="route-meta">{{ waypoint.routeTitle }} · {{ waypoint.waypointType }}</text>
          <text class="section-copy">{{ waypoint.description }}</text>
        </view>
      </view>
    </view>

    <view v-else-if="currentScreen === 'map-results'" class="content-stack">
      <view class="panel-card">
        <text class="section-title">围绕“{{ searchState.keyword || "山脊" }}”的路线分布</text>
        <text class="section-copy">当前用真实 `/api/search/map` 拉取地图聚合结果，先以坐标卡片方式表达路线分布。</text>
        <button class="secondary-button" @tap="loadMapResults(true)">刷新地图结果</button>
      </view>

      <view v-if="mapState.loading" class="panel-card">
        <text class="section-title">地图结果同步中</text>
        <text class="section-copy">正在检索符合条件的路线坐标。</text>
      </view>
      <view v-else-if="mapState.error" class="panel-card">
        <text class="section-title">地图结果加载失败</text>
        <text class="section-copy">{{ mapState.error }}</text>
      </view>
      <view v-else-if="mapState.items.length === 0" class="panel-card">
        <text class="section-title">没有匹配的地图路线</text>
      </view>
      <view v-else class="content-stack">
        <view v-for="item in mapState.items" :key="`map-route-${item.routeId}`" class="route-card">
          <text class="route-title">{{ item.title }}</text>
          <text class="route-meta">{{ item.authorName }} · {{ difficultyLabel(item.difficulty) }}</text>
          <text class="route-meta">{{ item.latitude.toFixed(3) }}, {{ item.longitude.toFixed(3) }} · 收藏 {{ item.favoriteCount }}</text>
          <view class="chip-row">
            <text v-for="tag in item.tags.slice(0, 2)" :key="`${item.routeId}-${tag}`" class="chip">{{ tag }}</text>
          </view>
          <button class="secondary-button" @tap="openRouteDetail(item.routeId)">打开路线详情</button>
        </view>
      </view>
    </view>

    <view v-else-if="currentScreen === 'route-detail'" class="content-stack">
      <view v-if="detailState.loading" class="panel-card">
        <text class="section-title">路线详情同步中</text>
      </view>
      <view v-else-if="detailState.error" class="panel-card">
        <text class="section-title">路线详情加载失败</text>
        <text class="section-copy">{{ detailState.error }}</text>
      </view>
      <template v-else-if="detailState.route">
        <view class="route-card route-card--hero">
          <text class="route-title">{{ detailState.route.title }}</text>
          <text class="route-meta">{{ detailState.route.distanceKm }}km · {{ formatDuration(detailState.route.durationMinutes) }} · 爬升 {{ detailState.route.ascentM }}m</text>
          <text class="section-copy">{{ detailState.route.description }}</text>
          <view class="chip-row">
            <text v-for="tag in detailState.route.tags" :key="`detail-${tag}`" class="chip">{{ tag }}</text>
          </view>
          <view class="button-row">
            <button class="primary-button button-row__item" @tap="toggleFavorite">{{ detailState.route.favorited ? "取消收藏" : "加入收藏" }}</button>
            <button v-if="authorState.profile" class="secondary-button button-row__item" @tap="toggleFollow">{{ authorState.profile.followed ? "取消关注" : "关注作者" }}</button>
          </view>
          <button v-if="authorState.profile" class="ghost-button" @tap="openAuthorProfile(authorState.profile.id)">进入作者主页</button>
        </view>

        <view class="panel-card panel-card--compact">
          <text class="section-title">关键点位</text>
          <view v-for="waypoint in detailState.route.waypoints" :key="waypoint.id" class="list-item">
            <text class="list-item__title">{{ waypoint.title }}</text>
            <text class="route-meta">{{ waypoint.waypointType }} · {{ waypoint.description }}</text>
          </view>
        </view>

        <view class="panel-card">
          <text class="section-title">评论</text>
          <textarea v-model="detailState.commentDraft" class="field-textarea" maxlength="200" placeholder="补充你的路线体验" />
          <button class="primary-button" :loading="detailState.submittingComment" @tap="submitComment">发布评论</button>

          <view v-if="detailState.commentsLoading" class="panel-card panel-card--compact">
            <text class="section-copy">评论同步中</text>
          </view>
          <view v-else-if="detailState.commentsError" class="panel-card panel-card--compact">
            <text class="section-copy">{{ detailState.commentsError }}</text>
          </view>
          <view v-else class="content-stack">
            <view v-for="comment in detailState.comments" :key="comment.id" class="panel-card panel-card--compact">
              <text class="list-item__title">{{ comment.authorName }}</text>
              <text class="section-copy">{{ comment.content }}</text>
              <text class="route-meta">{{ formatDate(comment.createdAt) }}</text>
            </view>
          </view>
        </view>
      </template>
    </view>

    <view v-else-if="currentScreen === 'author-profile'" class="content-stack">
      <view v-if="authorState.loading" class="panel-card">
        <text class="section-title">作者主页同步中</text>
      </view>
      <view v-else-if="authorState.error" class="panel-card">
        <text class="section-title">作者主页加载失败</text>
        <text class="section-copy">{{ authorState.error }}</text>
      </view>
      <template v-else-if="authorState.profile">
        <view class="panel-card">
          <text class="section-title">{{ authorState.profile.nickname }}</text>
          <text class="route-meta">{{ authorState.profile.city || "未填写城市" }} · {{ authorState.profile.levelLabel || "未设置等级" }}</text>
          <text class="section-copy">{{ authorState.profile.bio || "这位作者还没有补充简介。" }}</text>
          <view class="stats-grid">
            <view class="stat-card">
              <text class="stat-card__label">粉丝</text>
              <text class="stat-card__value">{{ authorState.profile.followerCount }}</text>
            </view>
            <view class="stat-card">
              <text class="stat-card__label">关注中</text>
              <text class="stat-card__value">{{ authorState.profile.followingCount }}</text>
            </view>
            <view class="stat-card">
              <text class="stat-card__label">已发布</text>
              <text class="stat-card__value">{{ authorState.profile.publishedRouteCount }}</text>
            </view>
            <view class="stat-card">
              <text class="stat-card__label">关系</text>
              <text class="stat-card__value">{{ authorState.profile.followed ? "已关注" : "未关注" }}</text>
            </view>
          </view>
          <button class="primary-button" @tap="toggleFollow">{{ authorState.profile.followed ? "取消关注" : "关注作者" }}</button>
        </view>

        <view v-for="route in authorState.profile.routes" :key="`author-route-${route.id}`" class="route-card">
          <text class="route-title">{{ route.title }}</text>
          <text class="route-meta">{{ route.distanceKm }}km · {{ formatDuration(route.durationMinutes) }} · 收藏 {{ route.favoriteCount }}</text>
          <button class="secondary-button" @tap="openRouteDetail(route.id)">查看路线详情</button>
        </view>
      </template>
    </view>

    <view v-else-if="currentScreen === 'profile'" class="content-stack">
      <view v-if="profileState.loading" class="panel-card">
        <text class="section-title">我的资料同步中</text>
      </view>
      <view v-else-if="profileState.error" class="panel-card">
        <text class="section-title">我的页面加载失败</text>
        <text class="section-copy">{{ profileState.error }}</text>
        <button class="secondary-button" @tap="loadProfile(true)">重新加载</button>
      </view>
      <template v-else>
        <view class="panel-card">
          <text class="section-title">{{ currentProfile.nickname }}</text>
          <text class="route-meta">{{ currentProfile.city || "未填写城市" }} · {{ currentProfile.levelLabel || "未设置等级" }}</text>
          <text class="section-copy">{{ currentProfile.bio || "这位徒步者还没有补充简介。" }}</text>
          <view class="stats-grid">
            <view class="stat-card">
              <text class="stat-card__label">已发布路线</text>
              <text class="stat-card__value">{{ currentProfile.publishedRouteCount }}</text>
            </view>
            <view class="stat-card">
              <text class="stat-card__label">收藏夹</text>
              <text class="stat-card__value">{{ currentProfile.favoriteRouteCount }}</text>
            </view>
            <view class="stat-card">
              <text class="stat-card__label">本次记录点位</text>
              <text class="stat-card__value">{{ recordingState.waypointCount }}</text>
            </view>
            <view class="stat-card">
              <text class="stat-card__label">草稿状态</text>
              <text class="stat-card__value">{{ recordingState.status === 'ended' ? "待整理" : "进行中" }}</text>
            </view>
          </view>
          <button class="primary-button" @tap="openProfileEditor">编辑个人资料</button>
          <button class="secondary-button" @tap="loadProfile(true)">刷新资料</button>
          <button class="ghost-button ghost-button--danger" @tap="logout()">退出当前账号</button>
        </view>

        <view v-if="profileEditor.visible" class="panel-card">
          <view class="section-header section-header--flush">
            <text class="section-title">编辑个人资料</text>
            <button class="ghost-button" @tap="closeProfileEditor">取消</button>
          </view>

          <view class="field">
            <text class="field-label">昵称</text>
            <input v-model="profileEditor.nickname" class="field-input" @blur="profileEditor.nicknameTouched = true" />
            <text v-if="profileNicknameError" class="error-text">{{ profileNicknameError }}</text>
          </view>

          <view class="field">
            <text class="field-label">城市</text>
            <input v-model="profileEditor.city" class="field-input" placeholder="例如：上海" />
          </view>

          <view class="field">
            <text class="field-label">等级标签</text>
            <input v-model="profileEditor.levelLabel" class="field-input" placeholder="例如：轻中度徒步" />
          </view>

          <view class="field">
            <text class="field-label">头像 URL</text>
            <input v-model="profileEditor.avatarUrl" class="field-input" placeholder="可选" />
          </view>

          <view class="field">
            <text class="field-label">个人简介</text>
            <textarea v-model="profileEditor.bio" class="field-textarea field-textarea--tight" maxlength="255" @blur="profileEditor.bioTouched = true" />
            <text v-if="profileBioError" class="error-text">{{ profileBioError }}</text>
          </view>

          <button class="primary-button" :loading="profileState.saving" @tap="saveProfile">保存资料</button>
        </view>

        <view class="section-header">
          <text class="section-title">我的收藏</text>
        </view>
        <view v-if="profileState.favoritesLoading" class="panel-card panel-card--compact">
          <text class="section-copy">收藏同步中</text>
        </view>
        <view v-else-if="profileState.favoritesError" class="panel-card panel-card--compact">
          <text class="section-copy">{{ profileState.favoritesError }}</text>
        </view>
        <view v-else-if="profileState.favorites.length === 0" class="panel-card panel-card--compact">
          <text class="section-copy">当前账号还没有收藏路线。</text>
        </view>
        <view v-else class="content-stack">
          <view v-for="route in profileState.favorites" :key="`favorite-${route.id}`" class="route-card">
            <text class="route-title">{{ route.title }}</text>
            <text class="route-meta">{{ route.distanceKm }}km · 收藏 {{ route.favoriteCount }} · {{ route.authorName }}</text>
            <button class="secondary-button" @tap="openRouteDetail(route.id)">查看路线详情</button>
          </view>
        </view>
      </template>
    </view>

    <view v-else-if="currentScreen === 'record-live'" class="content-stack">
      <view class="record-hero">
        <view>
          <text class="hero-eyebrow">Recording</text>
          <text class="section-title">{{ recordingStatusLabel }}</text>
          <text class="section-copy">当前记录链路先保留在本地状态机中，整理后再进入真实创作接口。</text>
        </view>
        <view class="status-badge">
          <text class="status-badge__label">已记录</text>
          <text class="status-badge__value">{{ recordingElapsedLabel }}</text>
        </view>
      </view>

      <view class="stats-grid">
        <view class="stat-card">
          <text class="stat-card__label">距离</text>
          <text class="stat-card__value">{{ recordingState.distanceKm.toFixed(2) }} km</text>
        </view>
        <view class="stat-card">
          <text class="stat-card__label">爬升</text>
          <text class="stat-card__value">{{ recordingState.ascentM }} m</text>
        </view>
        <view class="stat-card">
          <text class="stat-card__label">海拔</text>
          <text class="stat-card__value">{{ Math.round(recordingState.altitudeM) }} m</text>
        </view>
        <view class="stat-card">
          <text class="stat-card__label">点位</text>
          <text class="stat-card__value">{{ recordingState.waypointCount }}</text>
        </view>
      </view>

      <view class="panel-card">
        <text class="section-title">记录控制</text>
        <text class="section-copy">这块先做统一状态机行为，后续可继续接原生 GPS、后台上传和离线补偿。</text>
        <view class="stack-actions">
          <button v-if="recordingState.status === 'idle'" class="primary-button" @tap="startRecording">开始记录</button>
          <button v-else-if="recordingState.status === 'recording'" class="primary-button" @tap="pauseRecording">暂停记录</button>
          <button v-else-if="recordingState.status === 'paused'" class="primary-button" @tap="resumeRecording">继续记录</button>
          <button class="secondary-button" @tap="openAddWaypoint">新增点位</button>
          <button v-if="recordingState.status !== 'idle' && recordingState.status !== 'ended'" class="secondary-button" @tap="finishRecording">结束并整理</button>
          <button v-if="recordingState.status === 'ended'" class="secondary-button" @tap="jumpToCreatorFromRecording">进入创作整理</button>
        </view>
      </view>

      <view class="panel-card panel-card--compact">
        <text class="section-title">整理预览</text>
        <text class="route-meta">点位类型：{{ waypointTypeLabel(waypointDraft.type) }}</text>
        <text class="route-meta">草稿标题：{{ creatorForm.title }}</text>
        <text class="route-meta">预计点位：{{ waypointDraft.title }}</text>
        <text class="section-copy">{{ waypointDraft.description }}</text>
      </view>
    </view>

    <view v-else-if="currentScreen === 'add-waypoint'" class="content-stack">
      <view class="panel-card">
        <view class="section-header section-header--flush">
          <text class="section-title">点位类型</text>
          <button class="ghost-button" @tap="toggleExpandedWaypointTypes">
            {{ waypointDraft.expandedTypes ? "收起" : "展开更多" }}
          </button>
        </view>

        <view class="chip-row chip-row--spacious">
          <text
            v-for="type in waypointTypeOptions.base"
            :key="`type-${type}`"
            class="chip chip--selectable"
            :class="{ 'chip--active': waypointDraft.type === type }"
            @tap="selectWaypointType(type)"
          >
            {{ waypointTypeLabel(type) }}
          </text>
          <text
            v-for="type in waypointDraft.expandedTypes ? waypointTypeOptions.expanded : []"
            :key="`type-expanded-${type}`"
            class="chip chip--selectable chip--soft"
            :class="{ 'chip--active': waypointDraft.type === type }"
            @tap="selectWaypointType(type)"
          >
            {{ waypointTypeLabel(type) }}
          </text>
        </view>

        <view class="stack-actions">
          <button class="secondary-button">拍照添加</button>
          <button class="secondary-button">录像添加</button>
        </view>

        <view class="field">
          <text class="field-label">标题</text>
          <input
            v-model="waypointDraft.title"
            class="field-input"
            placeholder="例如：山脊观景台"
            @blur="waypointDraft.titleTouched = true"
          />
          <text v-if="waypointTitleError" class="error-text">{{ waypointTitleError }}</text>
        </view>

        <view class="field">
          <text class="field-label">说明</text>
          <textarea
            v-model="waypointDraft.description"
            class="field-textarea field-textarea--tight"
            placeholder="补充补给、风险、拍摄建议或路况提示"
            @blur="waypointDraft.descriptionTouched = true"
          />
          <text v-if="waypointDescriptionError" class="error-text">{{ waypointDescriptionError }}</text>
        </view>

        <button class="primary-button" @tap="saveWaypointToRecording">保存点位</button>
      </view>
    </view>

    <view v-else-if="currentScreen === 'creator-studio'" class="content-stack">
      <view v-if="creatorState.loading" class="panel-card">
        <text class="section-title">创作数据同步中</text>
        <text class="section-copy">正在读取当前草稿与我的路线列表。</text>
      </view>
      <view v-else-if="creatorState.error" class="panel-card">
        <text class="section-title">创作台加载失败</text>
        <text class="section-copy">{{ creatorState.error }}</text>
        <button class="secondary-button" @tap="loadCreatorData(true)">重新加载</button>
      </view>
      <template v-else>
        <view class="panel-card">
          <view class="section-header section-header--flush">
            <text class="section-title">当前草稿</text>
            <text class="meta-chip">{{ routeStatusLabel(creatorState.draft?.status || 'DRAFT') }}</text>
          </view>

          <view class="field">
            <text class="field-label">路线标题</text>
            <input :value="creatorForm.title" class="field-input" @input="updateCreatorField('title', inputValue($event))" />
          </view>

          <view class="field">
            <text class="field-label">路线说明</text>
            <textarea :value="creatorForm.description" class="field-textarea field-textarea--tight" @input="updateCreatorField('description', inputValue($event))" />
          </view>

          <view class="field">
            <text class="field-label">标签</text>
            <input :value="creatorForm.tags" class="field-input" placeholder="用逗号分隔标签" @input="updateCreatorField('tags', inputValue($event))" />
          </view>

          <view class="field">
            <text class="field-label">点位标题</text>
            <input v-model="waypointDraft.title" class="field-input" placeholder="例如：山脊观景台" />
          </view>

          <view class="field">
            <text class="field-label">点位说明</text>
            <textarea v-model="waypointDraft.description" class="field-textarea field-textarea--tight" maxlength="120" />
          </view>

          <view class="stats-grid">
            <view class="field field--stat">
              <text class="field-label">距离 km</text>
              <input :value="String(creatorForm.distanceKm)" class="field-input" type="digit" @input="updateCreatorField('distanceKm', inputValue($event))" />
            </view>
            <view class="field field--stat">
              <text class="field-label">时长 min</text>
              <input :value="String(creatorForm.durationMinutes)" class="field-input" type="number" @input="updateCreatorField('durationMinutes', inputValue($event))" />
            </view>
            <view class="field field--stat">
              <text class="field-label">爬升 m</text>
              <input :value="String(creatorForm.ascentM)" class="field-input" type="number" @input="updateCreatorField('ascentM', inputValue($event))" />
            </view>
            <view class="field field--stat">
              <text class="field-label">最高海拔</text>
              <input :value="String(creatorForm.maxAltitudeM)" class="field-input" type="number" @input="updateCreatorField('maxAltitudeM', inputValue($event))" />
            </view>
          </view>

          <view class="chip-row chip-row--spacious">
            <text v-for="tag in draftTagList" :key="`draft-tag-${tag}`" class="chip">{{ tag }}</text>
          </view>

          <view class="stack-actions">
            <button class="primary-button" :loading="creatorState.saving" @tap="saveCreatorDraft">保存草稿</button>
            <button class="secondary-button" :loading="creatorState.publishing" @tap="submitDraftForReview">提交审核</button>
          </view>
        </view>

        <view class="panel-card panel-card--compact">
          <view class="section-header section-header--flush">
            <text class="section-title">我的路线</text>
            <text class="route-meta">{{ creatorState.routes.length }} 条</text>
          </view>
          <view v-if="creatorState.routes.length === 0" class="empty-card">
            <text class="section-copy">当前还没有创作记录。</text>
          </view>
          <view v-else class="content-stack content-stack--tight">
            <view v-for="route in creatorState.routes" :key="`creator-route-${route.id}`" class="route-card route-card--nested">
              <text class="route-title">{{ route.title }}</text>
              <text class="route-meta">{{ route.distanceKm }}km · {{ formatDuration(route.durationMinutes) }} · 点位 {{ route.waypointCount }}</text>
              <text class="route-meta">{{ routeStatusLabel(route.status) }} · 更新于 {{ formatDate(route.updatedAt) }}</text>
            </view>
          </view>
        </view>
      </template>
    </view>
  </scroll-view>
</template>

<style lang="scss" scoped>
.page-shell {
  min-height: 100vh;
  background:
    radial-gradient(circle at top right, rgba(61, 122, 97, 0.1), transparent 30%),
    linear-gradient(180deg, #f5f8f4 0%, #eef4ef 100%);
}

.status-bar {
  height: 16px;
}

.content-stack {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  padding: 0 24rpx 40rpx;
}

.content-stack--tight {
  gap: 16rpx;
  padding: 0;
}

.hero-card,
.panel-card,
.route-card,
.record-hero,
.action-card {
  margin: 0 24rpx 24rpx;
  background: #ffffff;
  border-radius: 32rpx;
  box-shadow: 0 20rpx 60rpx rgba(17, 23, 20, 0.08);
}

.hero-card {
  padding: 28rpx;
  background:
    linear-gradient(135deg, rgba(61, 122, 97, 0.18), rgba(255, 255, 255, 0.94)),
    #ffffff;
}

.hero-row {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;
}

.hero-copy-wrap {
  flex: 1;
}

.hero-eyebrow {
  display: block;
  color: $trail-pine-700;
  font-size: 24rpx;
  font-weight: 600;
  margin-bottom: 10rpx;
}

.hero-title {
  display: block;
  color: $trail-ink-950;
  font-size: 44rpx;
  font-weight: 700;
  line-height: 1.25;
}

.hero-copy,
.section-copy,
.route-meta {
  display: block;
  color: $trail-stone-600;
  font-size: 26rpx;
  line-height: 1.6;
}

.hero-copy {
  margin-top: 14rpx;
}

.tab-row,
.chip-row,
.button-row,
.stats-grid,
.action-grid,
.stack-actions {
  display: flex;
  gap: 16rpx;
  flex-wrap: wrap;
}

.tab-row {
  margin-top: 24rpx;
}

.action-grid {
  padding: 0 24rpx;
}

.action-card {
  flex: 1;
  min-width: 0;
  margin: 0;
  padding: 24rpx;
  background: linear-gradient(180deg, rgba(247, 250, 247, 0.95), #ffffff);
}

.action-card__title {
  display: block;
  color: $trail-ink-950;
  font-size: 30rpx;
  font-weight: 700;
  margin-bottom: 10rpx;
}

.tab-button,
.ghost-button,
.secondary-button,
.primary-button {
  border: none;
  border-radius: 999rpx;
  font-size: 28rpx;
  line-height: 1;
}

.tab-button,
.ghost-button,
.secondary-button {
  background: #eef2ee;
  color: $trail-ink-800;
  padding: 18rpx 24rpx;
}

.tab-button--active,
.primary-button {
  background: $trail-pine-700;
  color: #ffffff;
}

.primary-button {
  padding: 22rpx 28rpx;
}

.button-row__item {
  flex: 1;
}

.ghost-button--danger {
  color: $trail-danger;
}

.panel-card,
.route-card,
.record-hero {
  padding: 28rpx;
}

.route-card--nested {
  margin: 0;
}

.panel-card--login {
  margin-top: 24rpx;
}

.panel-card--compact {
  margin: 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24rpx;
  gap: 16rpx;
}

.section-header--flush {
  padding: 0;
  margin-bottom: 12rpx;
}

.section-title,
.route-title,
.list-item__title {
  display: block;
  color: $trail-ink-950;
  font-size: 34rpx;
  font-weight: 700;
  line-height: 1.35;
}

.route-title {
  margin-bottom: 10rpx;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
  margin-top: 20rpx;
}

.field--stat {
  width: calc(50% - 8rpx);
  margin-top: 0;
}

.field-label {
  color: $trail-stone-600;
  font-size: 24rpx;
}

.field-input,
.field-textarea {
  width: 100%;
  background: #f8faf8;
  border: 2rpx solid $trail-stone-300;
  border-radius: 24rpx;
  box-sizing: border-box;
  padding: 24rpx;
  font-size: 28rpx;
  color: $trail-ink-950;
}

.field-textarea {
  min-height: 180rpx;
  margin: 20rpx 0;
}

.field-textarea--tight {
  min-height: 150rpx;
}

.chip {
  background: rgba(40, 84, 67, 0.08);
  color: $trail-pine-700;
  border-radius: 999rpx;
  padding: 10rpx 18rpx;
  font-size: 22rpx;
}

.chip--selectable {
  border: 2rpx solid rgba(40, 84, 67, 0.08);
}

.chip--soft {
  background: rgba(40, 84, 67, 0.04);
}

.chip--active {
  background: $trail-pine-700;
  color: #ffffff;
  border-color: $trail-pine-700;
}

.chip-row--spacious {
  margin: 20rpx 0;
}

.meta-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(61, 122, 97, 0.12);
  color: $trail-pine-700;
  font-size: 22rpx;
}

.list-item {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  padding: 18rpx 0;
  border-bottom: 2rpx solid rgba(215, 221, 217, 0.7);
}

.list-item:last-child {
  border-bottom: none;
}

.stats-grid {
  margin: 20rpx 0;
}

.stat-card {
  width: calc(50% - 8rpx);
  background: #f8faf8;
  border-radius: 24rpx;
  padding: 20rpx;
  box-sizing: border-box;
}

.stat-card__label {
  display: block;
  color: $trail-stone-600;
  font-size: 22rpx;
}

.stat-card__value {
  display: block;
  color: $trail-pine-700;
  font-size: 34rpx;
  font-weight: 700;
  margin-top: 8rpx;
}

.record-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20rpx;
  background: linear-gradient(135deg, rgba(61, 122, 97, 0.14), #ffffff);
}

.status-badge {
  min-width: 180rpx;
  padding: 18rpx 20rpx;
  border-radius: 28rpx;
  background: rgba(22, 55, 43, 0.9);
}

.status-badge__label {
  display: block;
  color: rgba(255, 255, 255, 0.72);
  font-size: 20rpx;
}

.status-badge__value {
  display: block;
  color: #ffffff;
  font-size: 32rpx;
  font-weight: 700;
  margin-top: 8rpx;
}

.stack-actions {
  margin-top: 24rpx;
}

.empty-card {
  padding: 16rpx 0 8rpx;
}

.error-text {
  display: block;
  margin: 18rpx 0;
  color: $trail-danger;
  font-size: 24rpx;
}
</style>
