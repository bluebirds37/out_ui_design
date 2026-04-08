<script setup lang="ts">
import type { MyProfile, RouteSummary } from "../api";
import type { RouteName } from "../app/routes";

defineProps<{
  loading: boolean;
  error: string;
  profile: MyProfile;
  profileInitial: string;
  favoritesLoading: boolean;
  favoritesError: string;
  favorites: RouteSummary[];
  recordingWaypointCount: number;
  recordingEnded: boolean;
}>();

const emit = defineEmits<{
  navigate: [route: RouteName];
  openProfileEditor: [];
  reload: [];
  openRouteDetail: [routeId: number];
  logout: [];
}>();
</script>

<template>
  <div class="page-header">
    <button class="nav-icon" @click="emit('navigate', 'discover')">←</button>
    <div class="page-header__title">我的</div>
    <div style="display: flex; gap: 8px">
      <button class="button-ghost" @click="emit('openProfileEditor')">编辑资料</button>
      <button class="button-ghost" @click="emit('logout')">退出</button>
    </div>
  </div>

  <section v-if="loading" class="panel-card panel-card--tight">
    <div class="empty-state">
      <strong>个人资料同步中</strong>
      <p>正在拉取我的资料与收藏路线。</p>
    </div>
  </section>

  <section v-else-if="error" class="panel-card panel-card--tight">
    <div class="empty-state">
      <strong>个人页加载失败</strong>
      <p>{{ error }}</p>
      <button class="button-secondary" @click="emit('reload')">重新加载</button>
    </div>
  </section>

  <template v-else>
    <section class="hero-banner">
      <p class="hero-banner__eyebrow">我的</p>
      <h2>{{ profile.nickname }}</h2>
      <p>{{ profile.city || "未填写城市" }} · {{ profile.levelLabel || "未设定等级" }}</p>
    </section>

    <section class="profile-card">
      <div class="profile-card__avatar">{{ profileInitial }}</div>
      <div>
        <h3 class="section-title">资料摘要</h3>
        <p class="body-copy">{{ profile.bio || "这位徒步者还没有补充简介。" }}</p>
      </div>
    </section>

    <section class="stats-grid">
      <article class="stat-card">
        <span>已发布路线</span>
        <strong>{{ profile.publishedRouteCount }}</strong>
      </article>
      <article class="stat-card">
        <span>收藏夹</span>
        <strong>{{ profile.favoriteRouteCount }}</strong>
      </article>
      <article class="stat-card">
        <span>本次记录点位</span>
        <strong>{{ recordingWaypointCount }}</strong>
      </article>
      <article class="stat-card">
        <span>草稿状态</span>
        <strong>{{ recordingEnded ? "待整理" : "进行中" }}</strong>
      </article>
    </section>

    <div style="display: grid; gap: 12px">
      <button class="button-primary button-primary--full" @click="emit('openProfileEditor')">编辑个人资料</button>
      <button class="button-secondary" @click="emit('logout')">退出当前账号</button>
    </div>

    <section class="panel-card panel-card--tight">
      <div class="section-heading-group">
        <div class="section-title-row">
          <h3 class="section-title">我的收藏</h3>
          <button class="button-ghost" @click="emit('reload')">刷新</button>
        </div>
        <p class="section-subtitle">收藏路线和其他端保持同样的展示层级与详情入口。</p>
      </div>

      <div v-if="favoritesLoading" class="empty-state empty-state--compact">
        <strong>收藏路线同步中</strong>
      </div>
      <div v-else-if="favoritesError" class="empty-state empty-state--compact">
        <strong>收藏列表加载失败</strong>
        <p>{{ favoritesError }}</p>
      </div>
      <div v-else-if="favorites.length" class="route-list">
        <button
          v-for="(route, index) in favorites"
          :key="route.id"
          class="route-card route-card--showcase route-card--compact-showcase"
          @click="emit('openRouteDetail', route.id)"
        >
          <div
            :class="[
              'route-card__cover',
              index % 2 === 0 ? 'route-card__cover--mist' : 'route-card__cover--lake',
            ]"
          ></div>
          <div class="route-card__title">{{ route.title }}</div>
          <div class="route-card__metrics">
            {{ route.distanceKm }}km · {{ route.authorName }} · 收藏 {{ route.favoriteCount }}
          </div>
          <div class="chip-row">
            <span v-for="tag in route.tags.slice(0, 3)" :key="`${route.id}-${tag}`" class="chip">
              {{ tag }}
            </span>
          </div>
          <div class="route-card__footer">
            <span class="meta-text">统一详情入口</span>
            <span class="button-primary button-primary--full">查看路线详情</span>
          </div>
        </button>
      </div>
      <div v-else class="empty-state empty-state--compact">
        <strong>你还没有收藏路线</strong>
        <p>从发现页挑一条喜欢的路线加入收藏后，会在这里展示。</p>
      </div>
    </section>
  </template>
</template>
