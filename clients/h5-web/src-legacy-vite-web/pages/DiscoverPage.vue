<script setup lang="ts">
import type { RouteSummary } from "../api";
import type { RouteName } from "../app/routes";

defineProps<{
  loading: boolean;
  error: string;
  primaryFeaturedRoute: RouteSummary | null;
  routes: RouteSummary[];
  formatDuration: (minutes: number | undefined) => string;
  difficultyLabel: (difficulty: string | undefined) => string;
}>();

const emit = defineEmits<{
  refresh: [];
  openRouteDetail: [routeId: number];
  navigate: [route: RouteName];
  openDrawer: [];
}>();
</script>

<template>
  <div class="page-header">
    <button class="nav-icon" @click="emit('openDrawer')">☰</button>
    <div class="page-header__title">发现</div>
    <button class="pill-button" @click="emit('refresh')">刷新</button>
  </div>

  <section v-if="loading" class="panel-card panel-card--tight">
    <div class="empty-state">
      <strong>正在同步路线数据</strong>
      <p>从 Spring Boot 业务 API 拉取热门路线与发现流。</p>
    </div>
  </section>

  <section v-else-if="error" class="panel-card panel-card--tight">
    <div class="empty-state">
      <strong>发现页暂时不可用</strong>
      <p>{{ error }}</p>
      <button class="button-secondary" @click="emit('refresh')">重新加载</button>
    </div>
  </section>

  <template v-else>
    <section class="hero-banner">
      <p class="hero-banner__eyebrow">精选路线</p>
      <h2>把山脊线、湖边营地和真实点位装进同一条路线里。</h2>
      <p>发现页优先承接统一的路线卡片、详情入口和远程错误反馈，和 iOS、Android、小程序保持同一信息顺序。</p>
    </section>

    <section v-if="primaryFeaturedRoute" class="section-block">
      <button
        class="route-card route-card--showcase"
        @click="emit('openRouteDetail', primaryFeaturedRoute.id)"
      >
        <div class="route-card__cover route-card__cover--ridge"></div>
        <div class="route-card__title">{{ primaryFeaturedRoute.title }}</div>
        <div class="route-card__metrics">
          {{ primaryFeaturedRoute.distanceKm }}km ·
          {{ formatDuration(primaryFeaturedRoute.durationMinutes) }} ·
          爬升 {{ primaryFeaturedRoute.ascentM }}m
        </div>
        <div class="meta-text">推荐路线 · 关键点位 {{ primaryFeaturedRoute.waypointCount }}</div>
        <div class="chip-row">
          <span
            v-for="tag in primaryFeaturedRoute.tags.slice(0, 3)"
            :key="`featured-${primaryFeaturedRoute.id}-${tag}`"
            class="chip"
          >
            {{ tag }}
          </span>
          <span class="chip chip--soft">{{ difficultyLabel(primaryFeaturedRoute.difficulty) }}</span>
        </div>
      </button>
      <button class="button-primary button-primary--full" @click="emit('openRouteDetail', primaryFeaturedRoute.id)">
        查看路线详情
      </button>
    </section>

    <section class="section-block">
      <div class="section-heading-group">
        <h3 class="section-title">继续浏览</h3>
        <p class="section-subtitle">保持和原生端一致的路线信息顺序、按钮层级和卡片视觉重心。</p>
      </div>

      <div class="route-list">
        <button
          v-for="(route, index) in routes"
          :key="route.id"
          class="route-card route-card--showcase"
          @click="emit('openRouteDetail', route.id)"
        >
          <div
            :class="[
              'route-card__cover',
              index % 2 === 0 ? 'route-card__cover--ridge' : 'route-card__cover--lake',
            ]"
          ></div>
          <div class="route-card__title">{{ route.title }}</div>
          <div class="route-card__metrics">
            {{ route.distanceKm }}km · 收藏 {{ route.favoriteCount }} · 关键点位 {{ route.waypointCount }}
          </div>
          <div class="meta-text">
            作者 {{ route.authorName }}
          </div>
          <div class="chip-row">
            <span
              v-for="tag in route.tags.slice(0, 3)"
              :key="`${route.id}-${tag}`"
              class="chip"
            >
              {{ tag }}
            </span>
            <span class="chip chip--soft">{{ difficultyLabel(route.difficulty) }}</span>
          </div>
          <div class="route-card__footer">
            <span class="meta-text">统一互动入口</span>
            <span class="button-primary button-primary--full">进入互动详情</span>
          </div>
        </button>
      </div>
    </section>
  </template>

  <nav class="bottom-nav">
    <button class="bottom-nav__item is-active">
      <span class="bottom-nav__icon">⌂</span>
      <span>发现</span>
    </button>
    <button class="bottom-nav__item" @click="emit('navigate', 'search')">
      <span class="bottom-nav__icon">⌕</span>
      <span>搜索</span>
    </button>
    <button class="bottom-nav__item bottom-nav__item--record" @click="emit('navigate', 'record-live')">
      <span class="bottom-nav__icon">◉</span>
      <span>记录</span>
    </button>
    <button class="bottom-nav__item" @click="emit('navigate', 'profile')">
      <span class="bottom-nav__icon">◔</span>
      <span>我的</span>
    </button>
  </nav>
</template>
