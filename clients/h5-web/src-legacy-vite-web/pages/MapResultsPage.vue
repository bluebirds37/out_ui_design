<script setup lang="ts">
import type { MapRouteItem } from "../api";
import type { RouteName } from "../app/routes";

defineProps<{
  loading: boolean;
  error: string;
  keyword: string;
  items: MapRouteItem[];
  difficultyLabel: (difficulty: string | undefined) => string;
}>();

const emit = defineEmits<{
  refresh: [];
  openRouteDetail: [routeId: number];
  navigate: [route: RouteName];
}>();
</script>

<template>
  <div class="page-header">
    <button class="nav-icon" @click="emit('navigate', 'search')">←</button>
    <div class="page-header__title">地图结果</div>
    <button class="pill-button" @click="emit('refresh')">刷新</button>
  </div>

  <section class="hero-banner">
    <p class="hero-banner__eyebrow">地图视角</p>
    <h2>围绕“{{ keyword || "山脊" }}”的路线分布</h2>
    <p>当前用真实 `/api/search/map` 拉取地图聚合结果，先以卡片方式表达坐标与路线信息。</p>
  </section>

  <section v-if="loading" class="panel-card panel-card--tight">
    <div class="empty-state">
      <strong>地图结果同步中</strong>
      <p>正在检索符合条件的路线坐标。</p>
    </div>
  </section>

  <section v-else-if="error" class="panel-card panel-card--tight">
    <div class="empty-state">
      <strong>地图结果加载失败</strong>
      <p>{{ error }}</p>
    </div>
  </section>

  <section v-else class="panel-card panel-card--tight">
    <div class="section-title-row">
      <h3 class="section-title">结果点位</h3>
      <span class="meta-text">{{ items.length }} 条路线</span>
    </div>
    <div v-if="items.length" class="route-list">
      <button
        v-for="item in items"
        :key="item.routeId"
        class="route-card route-card--compact"
        @click="emit('openRouteDetail', item.routeId)"
      >
        <div class="route-card__title">{{ item.title }}</div>
        <div class="meta-text">
          {{ item.authorName }} · {{ item.latitude.toFixed(3) }}, {{ item.longitude.toFixed(3) }}
        </div>
        <div class="chip-row">
          <span class="chip chip--soft">{{ difficultyLabel(item.difficulty) }}</span>
          <span v-for="tag in item.tags.slice(0, 2)" :key="`${item.routeId}-${tag}`" class="chip">
            {{ tag }}
          </span>
        </div>
      </button>
    </div>
    <div v-else class="empty-state empty-state--compact">
      <strong>没有匹配的地图路线</strong>
    </div>
  </section>

  <nav class="bottom-nav">
    <button class="bottom-nav__item" @click="emit('navigate', 'discover')">
      <span class="bottom-nav__icon">⌂</span>
      <span>发现</span>
    </button>
    <button class="bottom-nav__item" @click="emit('navigate', 'search')">
      <span class="bottom-nav__icon">⌕</span>
      <span>搜索</span>
    </button>
    <button class="bottom-nav__item bottom-nav__item--record is-active">
      <span class="bottom-nav__icon">◎</span>
      <span>地图</span>
    </button>
    <button class="bottom-nav__item" @click="emit('navigate', 'profile')">
      <span class="bottom-nav__icon">◔</span>
      <span>我的</span>
    </button>
  </nav>
</template>
