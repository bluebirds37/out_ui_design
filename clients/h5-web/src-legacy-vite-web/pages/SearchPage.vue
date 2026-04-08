<script setup lang="ts">
import type { SearchResult } from "../api";
import type { RouteName } from "../app/routes";

const props = defineProps<{
  loading: boolean;
  error: string;
  keyword: string;
  result: SearchResult | null;
  difficultyLabel: (difficulty: string | undefined) => string;
}>();

const emit = defineEmits<{
  updateKeyword: [value: string];
  search: [];
  openRouteDetail: [routeId: number];
  openAuthorProfile: [authorId: number];
  navigate: [route: RouteName];
}>();
</script>

<template>
  <div class="page-header">
    <button class="nav-icon" @click="emit('navigate', 'discover')">←</button>
    <div class="page-header__title">搜索</div>
    <button class="pill-button" @click="emit('search')">查询</button>
  </div>

  <section class="panel-card">
    <label class="field">
      <span class="field__label">关键词</span>
      <input
        :value="props.keyword"
        class="field__control"
        type="text"
        placeholder="搜索路线、作者或关键点位"
        @input="emit('updateKeyword', ($event.target as HTMLInputElement).value)"
      />
    </label>
    <button class="button-primary" @click="emit('search')">搜索徒步内容</button>
  </section>

  <section class="hero-banner">
    <p class="hero-banner__eyebrow">统一搜索</p>
    <h2>把路线、作者和关键点位放进同一条检索链路。</h2>
    <p>搜索页继续沿用四端共享的 Hero 区、结果分组和主操作层级，避免和发现页、详情页出现两套视觉节奏。</p>
  </section>

  <section v-if="loading" class="panel-card panel-card--tight">
    <div class="empty-state">
      <strong>正在检索内容</strong>
      <p>同步路线、作者和点位结果。</p>
    </div>
  </section>

  <section v-else-if="error" class="panel-card panel-card--tight">
    <div class="empty-state">
      <strong>搜索失败</strong>
      <p>{{ error }}</p>
    </div>
  </section>

  <template v-else-if="result">
    <section class="panel-card panel-card--tight">
      <div class="section-heading-group">
        <div class="section-title-row">
          <h3 class="section-title">路线结果</h3>
          <span class="meta-text">{{ result.routes.length }} 条</span>
        </div>
        <p class="section-subtitle">优先展示和发现页一致的路线大卡与详情入口。</p>
      </div>
      <div v-if="result.routes.length" class="route-list">
        <button
          v-for="(route, index) in result.routes"
          :key="route.id"
          class="route-card route-card--showcase route-card--compact-showcase"
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
            {{ route.distanceKm }}km · {{ route.authorName }} · 收藏 {{ route.favoriteCount }}
          </div>
          <div class="meta-text">关键点位 {{ route.waypointCount }} · {{ props.difficultyLabel(route.difficulty) }}</div>
          <div class="chip-row">
            <span v-for="tag in route.tags.slice(0, 3)" :key="`${route.id}-${tag}`" class="chip">
              {{ tag }}
            </span>
            <span class="chip chip--soft">{{ props.difficultyLabel(route.difficulty) }}</span>
          </div>
          <div class="route-card__footer">
            <span class="meta-text">统一详情入口</span>
            <span class="button-primary button-primary--full">打开路线详情</span>
          </div>
        </button>
      </div>
      <div v-else class="empty-state empty-state--compact">
        <strong>没有匹配的路线</strong>
      </div>
    </section>

    <section class="panel-card panel-card--tight">
      <div class="section-heading-group">
        <div class="section-title-row">
          <h3 class="section-title">作者结果</h3>
          <span class="meta-text">{{ result.authors.length }} 位</span>
        </div>
        <p class="section-subtitle">作者结果保持 Hero 后接统计信息的内容顺序。</p>
      </div>
      <div v-if="result.authors.length" class="route-list">
        <button
          v-for="author in result.authors"
          :key="author.id"
          class="route-card route-card--showcase route-card--compact-showcase"
          @click="emit('openAuthorProfile', author.id)"
        >
          <div class="route-card__cover route-card__cover--lake"></div>
          <div class="route-card__title">{{ author.nickname }}</div>
          <div class="route-card__metrics">
            {{ author.city || "城市未填" }} · 粉丝 {{ author.followerCount }}
          </div>
          <div class="meta-text">{{ author.levelLabel || "等级未设定" }} · {{ author.followed ? "已关注" : "未关注" }}</div>
          <div class="route-card__footer">
            <span class="meta-text">统一作者入口</span>
            <span class="button-primary button-primary--full">进入作者主页</span>
          </div>
        </button>
      </div>
      <div v-else class="empty-state empty-state--compact">
        <strong>没有匹配的作者</strong>
      </div>
    </section>

    <section class="panel-card panel-card--tight">
      <div class="section-heading-group">
        <div class="section-title-row">
          <h3 class="section-title">点位结果</h3>
          <span class="meta-text">{{ result.waypoints.length }} 个</span>
        </div>
        <p class="section-subtitle">点位结果仍保留更紧凑的信息卡，承接描述与所属路线。</p>
      </div>
      <div v-if="result.waypoints.length" class="comment-list">
        <article
          v-for="waypoint in result.waypoints"
          :key="waypoint.id"
          class="comment-card"
        >
          <div class="section-title-row comment-card__head">
            <strong>{{ waypoint.title }}</strong>
            <span class="meta-text">{{ waypoint.waypointType }}</span>
          </div>
          <p class="body-copy body-copy--compact">{{ waypoint.routeTitle }}</p>
          <p class="body-copy body-copy--compact">{{ waypoint.description }}</p>
        </article>
      </div>
      <div v-else class="empty-state empty-state--compact">
        <strong>没有匹配的点位</strong>
      </div>
    </section>
  </template>

  <nav class="bottom-nav">
    <button class="bottom-nav__item" @click="emit('navigate', 'discover')">
      <span class="bottom-nav__icon">⌂</span>
      <span>发现</span>
    </button>
    <button class="bottom-nav__item is-active">
      <span class="bottom-nav__icon">⌕</span>
      <span>搜索</span>
    </button>
    <button class="bottom-nav__item bottom-nav__item--record" @click="emit('navigate', 'map-results')">
      <span class="bottom-nav__icon">◎</span>
      <span>地图</span>
    </button>
    <button class="bottom-nav__item" @click="emit('navigate', 'profile')">
      <span class="bottom-nav__icon">◔</span>
      <span>我的</span>
    </button>
  </nav>
</template>
