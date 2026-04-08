<script setup lang="ts">
import type { AuthorProfile } from "../api";
import type { RouteName } from "../app/routes";

defineProps<{
  loading: boolean;
  error: string;
  selectedAuthorId: number | null;
  author: AuthorProfile | null;
  formatDuration: (minutes: number | undefined) => string;
}>();

const emit = defineEmits<{
  navigate: [route: RouteName];
  toggleFollow: [];
  retry: [authorId: number];
  openRouteDetail: [routeId: number];
}>();
</script>

<template>
  <div class="page-header">
    <button class="nav-icon" @click="emit('navigate', 'search')">←</button>
    <div class="page-header__title">作者主页</div>
    <button class="button-ghost" :disabled="!author" @click="emit('toggleFollow')">
      {{ author?.followed ? "已关注" : "关注" }}
    </button>
  </div>

  <section v-if="loading" class="panel-card panel-card--tight">
    <div class="empty-state">
      <strong>作者资料同步中</strong>
      <p>正在加载作者资料与其公开路线。</p>
    </div>
  </section>

  <section v-else-if="error" class="panel-card panel-card--tight">
    <div class="empty-state">
      <strong>作者主页加载失败</strong>
      <p>{{ error }}</p>
      <button
        v-if="selectedAuthorId"
        class="button-secondary"
        @click="emit('retry', selectedAuthorId)"
      >
        重试
      </button>
    </div>
  </section>

  <template v-else-if="author">
    <section class="hero-banner">
      <p class="hero-banner__eyebrow">作者主页</p>
      <h2>{{ author.nickname }}</h2>
      <p>{{ author.city || "未填写城市" }} · {{ author.levelLabel || "等级未设定" }}</p>
    </section>

    <section class="panel-card panel-card--tight">
      <p class="body-copy">{{ author.bio || "作者暂未补充简介。" }}</p>
    </section>

    <section class="stats-grid">
      <article class="stat-card">
        <span>粉丝</span>
        <strong>{{ author.followerCount }}</strong>
      </article>
      <article class="stat-card">
        <span>关注中</span>
        <strong>{{ author.followingCount }}</strong>
      </article>
      <article class="stat-card">
        <span>公开路线</span>
        <strong>{{ author.publishedRouteCount }}</strong>
      </article>
      <article class="stat-card">
        <span>当前关系</span>
        <strong>{{ author.followed ? "已关注" : "未关注" }}</strong>
      </article>
    </section>

    <button class="button-primary button-primary--full" @click="emit('toggleFollow')">
      {{ author.followed ? `已关注 · 粉丝 ${author.followerCount}` : `关注作者 · 粉丝 ${author.followerCount}` }}
    </button>

    <section class="panel-card panel-card--tight">
      <div class="section-heading-group">
        <h3 class="section-title">作者路线</h3>
        <p class="section-subtitle">和原生端保持同样的路线展示骨架与主操作层级。</p>
      </div>

      <div v-if="author.routes.length" class="route-list">
        <button
          v-for="(route, index) in author.routes"
          :key="route.id"
          class="route-card route-card--showcase route-card--compact-showcase"
          @click="emit('openRouteDetail', route.id)"
        >
          <div
            :class="[
              'route-card__cover',
              index % 2 === 0 ? 'route-card__cover--lake' : 'route-card__cover--ridge',
            ]"
          ></div>
          <div class="route-card__title">{{ route.title }}</div>
          <div class="route-card__metrics">
            {{ route.distanceKm }}km · {{ formatDuration(route.durationMinutes) }} · 收藏
            {{ route.favoriteCount }}
          </div>
          <div class="chip-row">
            <span v-for="tag in route.tags.slice(0, 3)" :key="`${route.id}-${tag}`" class="chip">
              {{ tag }}
            </span>
          </div>
          <div class="route-card__footer">
            <span class="meta-text">作者路线 {{ index + 1 }}</span>
            <span class="button-primary button-primary--full">查看路线详情</span>
          </div>
        </button>
      </div>
      <div v-else class="empty-state empty-state--compact">
        <strong>作者暂时没有公开路线</strong>
      </div>
    </section>
  </template>
</template>
