import { computed, reactive, ref } from "vue";
import type { RouteName } from "../app/routes";
import type { RecordingStatus } from "../types/recording";

export type ActiveModal = null | "pause" | "finish" | "edit-profile";

type NavigationHooks = {
  onDiscover: () => void;
  onSearch: () => void;
  onMapResults: () => void;
  onRouteDetail: () => void;
  onAuthorProfile: () => void;
  onProfile: () => void;
  onCreatorStudio: () => void;
  onLogin: (payload: { account: string; password: string }) => Promise<boolean>;
};

const waypointTypes = ["观景点", "补给点", "危险提醒"];
const waypointTypesExpanded = ["起点", "终点", "岔路口", "休息点", "营地点", "拍摄机位"];

export function usePrototypeLocalState(hooks: NavigationHooks) {
  const currentRoute = ref<RouteName>("login");
  const drawerOpen = ref(false);
  const toast = ref("");
  const activeModal = ref<ActiveModal>(null);

  const loginForm = reactive({
    account: "hiker@trailnote.app",
    password: "123456",
    touched: {
      account: false,
      password: false,
    },
  });

  const waypointForm = reactive({
    type: "观景点",
    expandedTypes: false,
    title: "",
    description: "",
    touched: {
      title: false,
      description: false,
    },
  });

  const recording = reactive({
    status: "idle" as RecordingStatus,
    elapsed: "00:18:42",
    distanceKm: 6.2,
    ascentM: 438,
    altitudeM: 1248,
    waypointCount: 4,
  });

  const loginErrors = computed(() => ({
    account: loginForm.touched.account && !loginForm.account.trim() ? "请输入账号" : "",
    password:
      loginForm.touched.password && loginForm.password.trim().length < 6 ? "密码至少 6 位" : "",
  }));

  const waypointErrors = computed(() => ({
    title: waypointForm.touched.title && !waypointForm.title.trim() ? "请输入点位标题" : "",
    description:
      waypointForm.touched.description && !waypointForm.description.trim() ? "请补充点位说明" : "",
  }));

  const recordingStatusLabel = computed(() => {
    const map: Record<RecordingStatus, string> = {
      idle: "未开始",
      recording: "记录中",
      paused: "已暂停",
      ended: "已结束",
    };
    return map[recording.status];
  });

  function navigate(route: RouteName) {
    currentRoute.value = route;
    drawerOpen.value = false;

    if (route === "discover") {
      hooks.onDiscover();
    }

    if (route === "search") {
      hooks.onSearch();
    }

    if (route === "map-results") {
      hooks.onMapResults();
    }

    if (route === "route-detail") {
      hooks.onRouteDetail();
    }

    if (route === "author-profile") {
      hooks.onAuthorProfile();
    }

    if (route === "profile") {
      hooks.onProfile();
    }

    if (route === "creator-studio") {
      hooks.onCreatorStudio();
    }
  }

  function showToast(message: string) {
    toast.value = message;
    window.clearTimeout((showToast as typeof showToast & { timer?: number }).timer);
    (showToast as typeof showToast & { timer?: number }).timer = window.setTimeout(() => {
      toast.value = "";
    }, 2200);
  }

  function setRoute(route: RouteName) {
    currentRoute.value = route;
  }

  async function submitLogin() {
    loginForm.touched.account = true;
    loginForm.touched.password = true;

    if (loginErrors.value.account || loginErrors.value.password) {
      showToast("请先完善登录信息");
      return;
    }

    const success = await hooks.onLogin({
      account: loginForm.account.trim(),
      password: loginForm.password,
    });

    if (!success) {
      showToast("登录失败，请重试");
      return;
    }

    loginForm.password = "";
    navigate("discover");
  }

  function startRecording() {
    recording.status = "recording";
    showToast("轨迹记录已开始");
  }

  function pauseRecording() {
    recording.status = "paused";
    activeModal.value = null;
    showToast("轨迹记录已暂停");
  }

  function resumeRecording() {
    recording.status = "recording";
    showToast("继续记录中");
  }

  function finishRecording() {
    recording.status = "ended";
    activeModal.value = null;
    showToast("记录完成，请整理路线内容");
  }

  function saveWaypoint() {
    waypointForm.touched.title = true;
    waypointForm.touched.description = true;

    if (waypointErrors.value.title || waypointErrors.value.description) {
      showToast("请先补完整点位信息");
      return;
    }

    recording.waypointCount += 1;
    showToast("点位已保存到本次轨迹");
    waypointForm.title = "";
    waypointForm.description = "";
    waypointForm.touched.title = false;
    waypointForm.touched.description = false;
    navigate("record-live");
  }

  return {
    waypointTypes,
    waypointTypesExpanded,
    currentRoute,
    drawerOpen,
    toast,
    activeModal,
    loginForm,
    waypointForm,
    recording,
    loginErrors,
    waypointErrors,
    recordingStatusLabel,
    navigate,
    setRoute,
    showToast,
    submitLogin,
    startRecording,
    pauseRecording,
    resumeRecording,
    finishRecording,
    saveWaypoint,
  };
}
