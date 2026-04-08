import { computed, reactive, ref } from "vue";
import {
  api,
  setApiUnauthorizedHandler,
  type AuthorProfile,
  type CreatorRouteRow,
  type MapRouteItem,
  type MyProfile,
  type RouteCommentItem,
  type RouteDetail,
  type RouteDraftDetail,
  type RouteSummary,
  type SearchResult,
} from "../api";

type ToastHandler = (message: string) => void;

type ProfileEditor = {
  nickname: string;
  avatarUrl: string;
  bio: string;
  city: string;
  levelLabel: string;
  touched: {
    nickname: boolean;
    bio: boolean;
  };
};

type WaypointDraftSource = {
  title: string;
  description: string;
};

const fallbackProfile: MyProfile = {
  id: 1001,
  nickname: "景野",
  avatarUrl: "",
  bio: "偏爱山脊线、湖边营地与日出路线记录",
  city: "上海",
  levelLabel: "轻中度徒步",
  publishedRouteCount: 0,
  favoriteRouteCount: 0,
};

export function usePrototypeData(showToast: ToastHandler) {
  const selectedRouteId = ref<number | null>(null);
  const selectedAuthorId = ref<number | null>(null);
  const authState = reactive({
    initializing: false,
    loggingIn: false,
    loginError: "",
    nickname: "",
    expired: false,
  });

  const discoverState = reactive({
    loading: false,
    error: "",
    featured: [] as RouteSummary[],
    routes: [] as RouteSummary[],
  });

  const detailState = reactive({
    loading: false,
    error: "",
    route: null as RouteDetail | null,
    commentsLoading: false,
    commentsError: "",
    comments: [] as RouteCommentItem[],
    submittingComment: false,
  });

  const profileState = reactive({
    loading: false,
    error: "",
    profile: null as MyProfile | null,
    favoritesLoading: false,
    favoritesError: "",
    favorites: [] as RouteSummary[],
    saving: false,
  });

  const socialState = reactive({
    authorLoading: false,
    authorError: "",
    author: null as AuthorProfile | null,
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
    description: "基于当前正式原型继续推进的创作草稿，用于验证多端统一创作流程。",
    difficulty: "INTERMEDIATE",
    distanceKm: 9.8,
    durationMinutes: 310,
    ascentM: 620,
    maxAltitudeM: 1480,
    tags: "山脊线,测试草稿,日出机位",
  });

  const commentForm = reactive({
    content: "",
  });

  const profileEditor = reactive<ProfileEditor>({
    nickname: "",
    avatarUrl: "",
    bio: "",
    city: "",
    levelLabel: "",
    touched: {
      nickname: false,
      bio: false,
    },
  });

  const primaryFeaturedRoute = computed(
    () => discoverState.featured[0] ?? discoverState.routes[0] ?? null,
  );
  const routeWaypoints = computed(() => detailState.route?.waypoints ?? []);
  const profileView = computed(() => profileState.profile ?? fallbackProfile);
  const profileInitial = computed(() => profileView.value.nickname.slice(0, 1) || "徒");
  const authorView = computed(() => socialState.author);
  const apiStatusLabel = computed(() => {
    if (authState.loggingIn || authState.initializing) {
      return "登录同步中";
    }
    if (discoverState.loading || detailState.loading || profileState.loading) {
      return "同步中";
    }
    if (discoverState.error || detailState.error || profileState.error) {
      return "接口待重试";
    }
    if (discoverState.routes.length || profileState.profile) {
      return "真实数据在线";
    }
    return "等待连接";
  });

  function hasAccessToken() {
    return Boolean(window.localStorage.getItem("trailnote_access_token"));
  }

  function clearSession() {
    window.localStorage.removeItem("trailnote_access_token");
    authState.nickname = "";
    authState.loginError = "";
  }

  function markSessionExpired() {
    clearSession();
    authState.expired = true;
    showToast("登录已失效，请重新登录");
  }

  function syncProfileEditor(profile: MyProfile) {
    profileEditor.nickname = profile.nickname ?? "";
    profileEditor.avatarUrl = profile.avatarUrl ?? "";
    profileEditor.bio = profile.bio ?? "";
    profileEditor.city = profile.city ?? "";
    profileEditor.levelLabel = profile.levelLabel ?? "";
    profileEditor.touched.nickname = false;
    profileEditor.touched.bio = false;
  }

  async function loadDiscover(force = false) {
    if (discoverState.loading) {
      return;
    }
    if (!force && discoverState.routes.length > 0) {
      return;
    }

    discoverState.loading = true;
    discoverState.error = "";
    try {
      const [featured, routesPage] = await Promise.all([api.featuredRoutes(), api.routes(1, 6)]);
      discoverState.featured = featured;
      discoverState.routes = routesPage.records;
      if (!selectedRouteId.value && routesPage.records.length > 0) {
        selectedRouteId.value = routesPage.records[0].id;
      }
    } catch (error) {
      discoverState.error = error instanceof Error ? error.message : "发现页加载失败";
    } finally {
      discoverState.loading = false;
    }
  }

  async function loadRouteComments(routeId: number) {
    detailState.commentsLoading = true;
    detailState.commentsError = "";
    try {
      const page = await api.routeComments(routeId, 1, 20);
      detailState.comments = page.records;
    } catch (error) {
      detailState.commentsError = error instanceof Error ? error.message : "评论加载失败";
    } finally {
      detailState.commentsLoading = false;
    }
  }

  async function loadAuthorProfile(authorId: number) {
    socialState.authorLoading = true;
    socialState.authorError = "";
    selectedAuthorId.value = authorId;
    try {
      socialState.author = await api.authorProfile(authorId);
    } catch (error) {
      socialState.authorError = error instanceof Error ? error.message : "作者资料加载失败";
    } finally {
      socialState.authorLoading = false;
    }
  }

  async function loadRouteDetail(routeId: number) {
    detailState.loading = true;
    detailState.error = "";
    socialState.author = null;
    socialState.authorError = "";
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

  async function loadSearch(forceKeyword?: string) {
    const keyword = (forceKeyword ?? searchState.keyword).trim();
    searchState.keyword = keyword;
    searchState.loading = true;
    searchState.error = "";
    try {
      searchState.result = await api.search(keyword);
    } catch (error) {
      searchState.error = error instanceof Error ? error.message : "搜索结果加载失败";
    } finally {
      searchState.loading = false;
    }
  }

  async function loadMapResults() {
    mapState.loading = true;
    mapState.error = "";
    try {
      mapState.items = await api.searchMap(searchState.keyword || "山脊");
    } catch (error) {
      mapState.error = error instanceof Error ? error.message : "地图结果加载失败";
    } finally {
      mapState.loading = false;
    }
  }

  async function loadCreatorData() {
    creatorState.loading = true;
    creatorState.error = "";
    try {
      const [draft, routesPage] = await Promise.all([
        api.creatorCurrentDraft(),
        api.creatorRoutes(1, 6),
      ]);
      creatorState.draft = draft;
      creatorState.routes = routesPage.records;
      if (draft) {
        creatorForm.title = draft.title;
        creatorForm.description = draft.description;
        creatorForm.difficulty = draft.difficulty;
        creatorForm.distanceKm = draft.distanceKm;
        creatorForm.durationMinutes = draft.durationMinutes;
        creatorForm.ascentM = draft.ascentM;
        creatorForm.maxAltitudeM = draft.maxAltitudeM;
        creatorForm.tags = draft.tags.join(",");
      }
    } catch (error) {
      creatorState.error = error instanceof Error ? error.message : "创作台加载失败";
    } finally {
      creatorState.loading = false;
    }
  }

  async function loadProfile(force = false) {
    if (profileState.loading || profileState.favoritesLoading) {
      return;
    }
    if (!force && profileState.profile && profileState.favorites.length > 0) {
      return;
    }

    profileState.loading = true;
    profileState.favoritesLoading = true;
    profileState.error = "";
    profileState.favoritesError = "";
    try {
      const [profile, favoritesPage] = await Promise.all([api.myProfile(), api.myFavorites(1, 6)]);
      profileState.profile = profile;
      profileState.favorites = favoritesPage.records;
      syncProfileEditor(profile);
    } catch (error) {
      const message = error instanceof Error ? error.message : "个人页加载失败";
      profileState.error = message;
      profileState.favoritesError = message;
    } finally {
      profileState.loading = false;
      profileState.favoritesLoading = false;
    }
  }

  async function bootstrapAfterLogin(force = false) {
    await Promise.all([
      loadDiscover(force),
      loadProfile(force),
      loadSearch(),
      loadMapResults(),
    ]);
  }

  async function initializeSession() {
    if (!hasAccessToken()) {
      clearSession();
      return false;
    }

    authState.initializing = true;
    authState.loginError = "";
    try {
      await bootstrapAfterLogin();
      authState.nickname = profileState.profile?.nickname ?? "";
      authState.expired = false;
      return true;
    } catch (error) {
      clearSession();
      authState.loginError = error instanceof Error ? error.message : "登录态恢复失败";
      return false;
    } finally {
      authState.initializing = false;
    }
  }

  async function login(account: string, password: string) {
    authState.loggingIn = true;
    authState.loginError = "";

    try {
      const response = await api.login(account, password);
      window.localStorage.setItem("trailnote_access_token", response.accessToken);
      authState.nickname = response.nickname;
      authState.expired = false;
      await bootstrapAfterLogin(true);
      showToast(`欢迎回来，${response.nickname}`);
      return true;
    } catch (error) {
      authState.loginError = error instanceof Error ? error.message : "登录失败";
      return false;
    } finally {
      authState.loggingIn = false;
    }
  }

  setApiUnauthorizedHandler(markSessionExpired);

  function updateRouteSummaryFavorite(routeId: number, favoriteCount: number) {
    const updater = (route: RouteSummary) =>
      route.id === routeId ? { ...route, favoriteCount } : route;
    discoverState.featured = discoverState.featured.map(updater);
    discoverState.routes = discoverState.routes.map(updater);
    profileState.favorites = profileState.favorites.map(updater);
  }

  async function toggleRouteFavorite() {
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
      updateRouteSummaryFavorite(detailState.route.id, result.favoriteCount);
      await loadProfile(true);
      showToast(result.favorited ? "已加入收藏夹" : "已取消收藏");
    } catch (error) {
      showToast(error instanceof Error ? error.message : "收藏操作失败");
    }
  }

  async function toggleAuthorFavorite() {
    const author = authorView.value;
    if (!author) {
      return;
    }

    try {
      const result = await api.toggleAuthorFollow(author.id);
      socialState.author = {
        ...author,
        followed: result.followed,
        followerCount: result.followerCount,
      };
      showToast(result.followed ? "已关注作者" : "已取消关注");
    } catch (error) {
      showToast(error instanceof Error ? error.message : "关注操作失败");
    }
  }

  async function submitComment() {
    const route = detailState.route;
    if (!route) {
      return;
    }
    if (!commentForm.content.trim()) {
      showToast("请输入评论内容");
      return;
    }

    detailState.submittingComment = true;
    try {
      const result = await api.addRouteComment(route.id, commentForm.content.trim());
      commentForm.content = "";
      detailState.route = {
        ...route,
        commentCount: result.commentCount,
      };
      await loadRouteComments(route.id);
      showToast("评论已发送");
    } catch (error) {
      showToast(error instanceof Error ? error.message : "评论发送失败");
    } finally {
      detailState.submittingComment = false;
    }
  }

  async function saveProfile(closeModal: () => void) {
    if (!profileEditor.nickname.trim() || !profileEditor.bio.trim()) {
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
      closeModal();
      showToast("资料已更新");
    } catch (error) {
      showToast(error instanceof Error ? error.message : "资料保存失败");
    } finally {
      profileState.saving = false;
    }
  }

  async function saveCreatorDraft(waypointDraft: WaypointDraftSource) {
    creatorState.saving = true;
    try {
      const tags = creatorForm.tags
        .split(",")
        .map((tag) => tag.trim())
        .filter(Boolean);
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
        tags,
        waypoints: [
          {
            id: null,
            title: waypointDraft.title.trim() || "默认观景点",
            waypointType: "VIEWPOINT",
            description: waypointDraft.description.trim() || "从记录流程补进来的默认点位说明",
            latitude: 30.23591,
            longitude: 120.10458,
            altitudeM: 1248,
            sortOrder: 1,
            mediaList: [],
          },
        ],
      });
      creatorState.draft = draft;
      showToast("草稿已保存到真实 creator 接口");
      await loadCreatorData();
    } catch (error) {
      showToast(error instanceof Error ? error.message : "草稿保存失败");
    } finally {
      creatorState.saving = false;
    }
  }

  async function publishCreatorDraft() {
    if (!creatorState.draft?.id) {
      showToast("请先保存草稿");
      return;
    }
    creatorState.publishing = true;
    try {
      const result = await api.publishDraft(creatorState.draft.id);
      showToast(`已提交发布，当前状态 ${result.status}`);
      await loadCreatorData();
    } catch (error) {
      showToast(error instanceof Error ? error.message : "发布失败");
    } finally {
      creatorState.publishing = false;
    }
  }

  return {
    authState,
    selectedRouteId,
    selectedAuthorId,
    discoverState,
    detailState,
    profileState,
    socialState,
    searchState,
    mapState,
    creatorState,
    creatorForm,
    commentForm,
    profileEditor,
    primaryFeaturedRoute,
    routeWaypoints,
    profileView,
    profileInitial,
    authorView,
    apiStatusLabel,
    clearSession,
    hasAccessToken,
    initializeSession,
    login,
    markSessionExpired,
    syncProfileEditor,
    loadDiscover,
    loadRouteDetail,
    loadAuthorProfile,
    loadSearch,
    loadMapResults,
    loadCreatorData,
    loadProfile,
    toggleRouteFavorite,
    toggleAuthorFavorite,
    submitComment,
    saveProfile,
    saveCreatorDraft,
    publishCreatorDraft,
  };
}
