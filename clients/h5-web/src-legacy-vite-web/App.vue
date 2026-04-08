<script setup lang="ts">
import { computed, onMounted, watch } from "vue";
import { pageOptions } from "./app/routes";
import { difficultyLabel, formatDate, formatDuration } from "./app/formatters";
import AppModalHost from "./components/AppModalHost.vue";
import PrototypeDrawer from "./components/PrototypeDrawer.vue";
import { usePrototypeData } from "./composables/usePrototypeData";
import { usePrototypeLocalState } from "./composables/usePrototypeLocalState";
import AddWaypointPage from "./pages/AddWaypointPage.vue";
import LoginPage from "./pages/LoginPage.vue";
import RecordLivePage from "./pages/RecordLivePage.vue";
import WelcomePage from "./pages/WelcomePage.vue";
import DiscoverPage from "./pages/DiscoverPage.vue";
import SearchPage from "./pages/SearchPage.vue";
import MapResultsPage from "./pages/MapResultsPage.vue";
import RouteDetailPage from "./pages/RouteDetailPage.vue";
import AuthorProfilePage from "./pages/AuthorProfilePage.vue";
import CreatorStudioPage from "./pages/CreatorStudioPage.vue";
import ProfilePage from "./pages/ProfilePage.vue";
let showToast: (message: string) => void = () => {};

const {
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
  initializeSession,
  login,
  clearSession,
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
  saveProfile: saveProfileData,
  saveCreatorDraft: saveCreatorDraftData,
  publishCreatorDraft,
} = usePrototypeData((message) => {
  showToast(message);
});

const {
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
  showToast: showToastImpl,
  submitLogin,
  startRecording,
  pauseRecording,
  resumeRecording,
  finishRecording,
  saveWaypoint,
} = usePrototypeLocalState({
  onDiscover: () => {
    void loadDiscover();
  },
  onSearch: () => {
    void loadSearch();
  },
  onMapResults: () => {
    void loadMapResults();
  },
  onRouteDetail: () => {
    void openRouteDetail(selectedRouteId.value ?? 1001);
  },
  onAuthorProfile: () => {
    void openAuthorProfile(selectedAuthorId.value ?? 1002);
  },
  onProfile: () => {
    void loadProfile();
  },
  onCreatorStudio: () => {
    void loadCreatorData();
  },
  onLogin: ({ account, password }) => login(account, password),
});

showToast = showToastImpl;

const profileErrors = computed(() => ({
  nickname: profileEditor.touched.nickname && !profileEditor.nickname.trim() ? "昵称不能为空" : "",
  bio: profileEditor.touched.bio && !profileEditor.bio.trim() ? "请补充个人简介" : "",
}));

async function openRouteDetail(routeId: number) {
  selectedRouteId.value = routeId;
  currentRoute.value = "route-detail";
  await loadRouteDetail(routeId);
}

async function openAuthorProfile(authorId: number) {
  currentRoute.value = "author-profile";
  await loadAuthorProfile(authorId);
}

function openProfileEditor() {
  syncProfileEditor(profileView.value);
  activeModal.value = "edit-profile";
}

function updateProfileEditorField(
  field: "nickname" | "avatarUrl" | "bio" | "city" | "levelLabel",
  value: string,
) {
  profileEditor[field] = value;
}

async function saveProfile() {
  profileEditor.touched.nickname = true;
  profileEditor.touched.bio = true;
  if (profileErrors.value.nickname || profileErrors.value.bio) {
    showToast("请先补完整资料");
    return;
  }
  await saveProfileData(() => {
    activeModal.value = null;
  });
}

async function saveCreatorDraft() {
  await saveCreatorDraftData({
    title: waypointForm.title,
    description: waypointForm.description,
  });
}

onMounted(() => {
  void (async () => {
    const hasSession = await initializeSession();
    if (hasSession) {
      setRoute("discover");
      return;
    }
    setRoute("login");
  })();
});

function logout() {
  clearSession();
  setRoute("login");
  showToast("已退出当前账号");
}

watch(
  () => authState.expired,
  (expired) => {
    if (!expired) {
      return;
    }
    setRoute("login");
  },
);
</script>

<template>
  <main class="prototype-shell">
    <aside class="desktop-panel">
      <p class="desktop-panel__eyebrow">TrailNote</p>
      <h1 class="desktop-panel__title">H5 正式原型基线</h1>
      <p class="desktop-panel__copy">
        这一版开始承接正式多端开发基线，页面结构、交互反馈与设计 token 会持续向 iOS、Android、
        小程序和后台管理端同步。
      </p>

      <div class="desktop-panel__section">
        <p class="desktop-panel__section-title">同步状态</p>
        <div class="desktop-panel__status">
          <span class="chip chip--soft">{{ apiStatusLabel }}</span>
          <button class="button-ghost" @click="loadDiscover(true)">刷新发现</button>
        </div>
      </div>

      <div class="desktop-panel__section">
        <p class="desktop-panel__section-title">页面导航</p>
        <div class="desktop-panel__nav">
          <button
            v-for="page in pageOptions"
            :key="page.route"
            :class="{ 'is-active': currentRoute === page.route }"
            @click="navigate(page.route)"
          >
            {{ page.label }}
          </button>
        </div>
      </div>
    </aside>

    <section class="mobile-stage">
      <div class="device-frame">
        <div class="device-screen">
          <div class="screen">
            <div class="safe-area">
              <header class="status-bar">
                <span>9:41</span>
                <span>5G 88%</span>
              </header>

              <WelcomePage v-if="currentRoute === 'welcome'" @navigate="navigate" />

              <LoginPage
                v-else-if="currentRoute === 'login'"
                :account="loginForm.account"
                :password="loginForm.password"
                :account-error="loginErrors.account"
                :password-error="loginErrors.password"
                :submitting="authState.loggingIn"
                :login-error="authState.loginError"
                @back="navigate('welcome')"
                @update-account="loginForm.account = $event"
                @update-password="loginForm.password = $event"
                @mark-account-touched="loginForm.touched.account = true"
                @mark-password-touched="loginForm.touched.password = true"
                @submit="submitLogin"
              />

              <DiscoverPage
                v-else-if="currentRoute === 'discover'"
                :loading="discoverState.loading"
                :error="discoverState.error"
                :primary-featured-route="primaryFeaturedRoute"
                :routes="discoverState.routes"
                :format-duration="formatDuration"
                :difficulty-label="difficultyLabel"
                @refresh="loadDiscover(true)"
                @open-route-detail="openRouteDetail"
                @navigate="navigate"
                @open-drawer="drawerOpen = true"
              />

              <SearchPage
                v-else-if="currentRoute === 'search'"
                :loading="searchState.loading"
                :error="searchState.error"
                :keyword="searchState.keyword"
                :result="searchState.result"
                :difficulty-label="difficultyLabel"
                @update-keyword="searchState.keyword = $event"
                @search="loadSearch(searchState.keyword)"
                @open-route-detail="openRouteDetail"
                @open-author-profile="openAuthorProfile"
                @navigate="navigate"
              />

              <MapResultsPage
                v-else-if="currentRoute === 'map-results'"
                :loading="mapState.loading"
                :error="mapState.error"
                :keyword="searchState.keyword"
                :items="mapState.items"
                :difficulty-label="difficultyLabel"
                @refresh="loadMapResults()"
                @open-route-detail="openRouteDetail"
                @navigate="navigate"
              />

              <RouteDetailPage
                v-else-if="currentRoute === 'route-detail'"
                :loading="detailState.loading"
                :error="detailState.error"
                :selected-route-id="selectedRouteId"
                :route="detailState.route"
                :author="authorView"
                :route-waypoints="routeWaypoints"
                :comments-loading="detailState.commentsLoading"
                :comments-error="detailState.commentsError"
                :comments="detailState.comments"
                :comment-content="commentForm.content"
                :submitting-comment="detailState.submittingComment"
                :format-duration="formatDuration"
                :difficulty-label="difficultyLabel"
                :format-date="formatDate"
                @back="navigate('discover')"
                @toggle-favorite="toggleRouteFavorite"
                @retry="loadRouteDetail"
                @toggle-author-follow="toggleAuthorFavorite"
                @open-author-profile="openAuthorProfile"
                @update-comment-content="commentForm.content = $event"
                @submit-comment="submitComment"
              />

              <AuthorProfilePage
                v-else-if="currentRoute === 'author-profile'"
                :loading="socialState.authorLoading"
                :error="socialState.authorError"
                :selected-author-id="selectedAuthorId"
                :author="authorView"
                :format-duration="formatDuration"
                @navigate="navigate"
                @toggle-follow="toggleAuthorFavorite"
                @retry="loadAuthorProfile"
                @open-route-detail="openRouteDetail"
              />

              <RecordLivePage
                v-else-if="currentRoute === 'record-live'"
                :status="recording.status"
                :status-label="recordingStatusLabel"
                :elapsed="recording.elapsed"
                :distance-km="recording.distanceKm"
                :ascent-m="recording.ascentM"
                :altitude-m="recording.altitudeM"
                :waypoint-count="recording.waypointCount"
                @navigate="navigate"
                @request-pause="activeModal = 'pause'"
                @request-finish="activeModal = 'finish'"
                @start-recording="startRecording"
                @resume-recording="resumeRecording"
              />

              <AddWaypointPage
                v-else-if="currentRoute === 'add-waypoint'"
                :selected-type="waypointForm.type"
                :expanded-types="waypointForm.expandedTypes"
                :title="waypointForm.title"
                :description="waypointForm.description"
                :base-types="waypointTypes"
                :expanded-type-options="waypointTypesExpanded"
                :title-error="waypointErrors.title"
                :description-error="waypointErrors.description"
                @back="navigate('record-live')"
                @toggle-expanded-types="waypointForm.expandedTypes = !waypointForm.expandedTypes"
                @select-type="waypointForm.type = $event"
                @update-title="waypointForm.title = $event"
                @update-description="waypointForm.description = $event"
                @mark-title-touched="waypointForm.touched.title = true"
                @mark-description-touched="waypointForm.touched.description = true"
                @save="saveWaypoint"
              />

              <CreatorStudioPage
                v-else-if="currentRoute === 'creator-studio'"
                :loading="creatorState.loading"
                :error="creatorState.error"
                :saving="creatorState.saving"
                :publishing="creatorState.publishing"
                :draft="creatorState.draft"
                :routes="creatorState.routes"
                :creator-form="creatorForm"
                :format-duration="formatDuration"
                @navigate="navigate"
                @refresh="loadCreatorData()"
                @update-field="
                  (field, value) => {
                    (creatorForm as Record<string, string | number>)[field] = value;
                  }
                "
                @save-draft="saveCreatorDraft"
                @publish-draft="publishCreatorDraft"
              />

              <ProfilePage
                v-else-if="currentRoute === 'profile'"
                :loading="profileState.loading"
                :error="profileState.error"
                :profile="profileView"
                :profile-initial="profileInitial"
                :favorites-loading="profileState.favoritesLoading"
                :favorites-error="profileState.favoritesError"
                :favorites="profileState.favorites"
                :recording-waypoint-count="recording.waypointCount"
                :recording-ended="recording.status === 'ended'"
                @navigate="navigate"
                @open-profile-editor="openProfileEditor"
                @reload="loadProfile(true)"
                @open-route-detail="openRouteDetail"
                @logout="logout"
              />
            </div>

            <button class="fab" @click="drawerOpen = true">页</button>

            <PrototypeDrawer
              :open="drawerOpen"
              :page-options="pageOptions"
              @close="drawerOpen = false"
              @navigate="navigate"
            />

            <AppModalHost
              :active-modal="activeModal"
              :profile-saving="profileState.saving"
              :profile-editor="profileEditor"
              :profile-errors="profileErrors"
              @close="activeModal = null"
              @pause-recording="pauseRecording"
              @finish-recording="finishRecording"
              @save-profile="saveProfile"
              @update-profile-field="updateProfileEditorField"
              @mark-nickname-touched="profileEditor.touched.nickname = true"
              @mark-bio-touched="profileEditor.touched.bio = true"
            />

            <transition name="toast">
              <div v-if="toast" class="toast">{{ toast }}</div>
            </transition>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>
