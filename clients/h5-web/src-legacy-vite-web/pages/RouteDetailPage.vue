<script setup lang="ts">
import type { AuthorProfile, RouteCommentItem, RouteDetail, WaypointSummary } from "../api";

defineProps<{
  loading: boolean;
  error: string;
  selectedRouteId: number | null;
  route: RouteDetail | null;
  author: AuthorProfile | null;
  routeWaypoints: WaypointSummary[];
  commentsLoading: boolean;
  commentsError: string;
  comments: RouteCommentItem[];
  commentContent: string;
  submittingComment: boolean;
  formatDuration: (minutes: number | undefined) => string;
  difficultyLabel: (difficulty: string | undefined) => string;
  formatDate: (value: string) => string;
}>();

const emit = defineEmits<{
  back: [];
  toggleFavorite: [];
  retry: [routeId: number];
  toggleAuthorFollow: [];
  openAuthorProfile: [authorId: number];
  updateCommentContent: [value: string];
  submitComment: [];
}>();
</script>

<template>
  <div class="page-header">
    <button class="nav-icon" @click="emit('back')">←</button>
    <div class="page-header__title">路线详情</div>
    <button class="nav-icon" :disabled="!route" @click="emit('toggleFavorite')">
      {{ route?.favorited ? "★" : "☆" }}
    </button>
  </div>

  <section v-if="loading" class="panel-card panel-card--tight">
    <div class="empty-state">
      <strong>正在加载路线详情</strong>
      <p>同步点位、作者信息与评论内容。</p>
    </div>
  </section>

  <section v-else-if="error" class="panel-card panel-card--tight">
    <div class="empty-state">
      <strong>路线详情加载失败</strong>
      <p>{{ error }}</p>
      <button
        v-if="selectedRouteId"
        class="button-secondary"
        @click="emit('retry', selectedRouteId)"
      >
        重试
      </button>
    </div>
  </section>

  <template v-else-if="route">
    <section class="route-card route-card--showcase route-card--static">
      <div class="detail-cover"></div>
      <div class="route-card__title">{{ route.title }}</div>
      <div class="route-card__metrics">
        {{ route.distanceKm }}km ·
        {{ formatDuration(route.durationMinutes) }} · 爬升 {{ route.ascentM }}m
      </div>
      <div class="meta-text">路线详情 · 点位 {{ routeWaypoints.length }} · 评论 {{ route.commentCount }}</div>
      <div class="chip-row">
        <span v-for="tag in route.tags" :key="tag" class="chip">{{ tag }}</span>
        <span class="chip chip--soft">{{ difficultyLabel(route.difficulty) }}</span>
      </div>
    </section>

    <section class="button-row button-row--full">
      <button class="button-primary button-primary--full" @click="emit('toggleFavorite')">
        {{ route.favorited ? `已收藏 · ${route.favoriteCount}` : `收藏 · ${route.favoriteCount}` }}
      </button>
      <button
        v-if="author"
        class="button-secondary button-secondary--full"
        @click="emit('toggleAuthorFollow')"
      >
        {{ author?.followed ? "已关注作者" : "关注作者" }}
      </button>
    </section>

    <section class="panel-card panel-card--tight">
      <div class="section-heading-group">
        <h3 class="section-title">路线说明</h3>
        <p class="section-subtitle">和其他端统一把说明、作者入口与统计信息放在头图区之后。</p>
      </div>
      <p class="body-copy">{{ route.description }}</p>
    </section>

    <section
      v-if="author"
      class="panel-card panel-card--tight panel-card--interactive"
      @click="emit('openAuthorProfile', author.id)"
    >
      <div class="section-heading-group">
        <h3 class="section-title">{{ author.nickname }} · {{ author.city || "城市未填写" }}</h3>
        <p class="section-subtitle">作者信息</p>
      </div>
      <div class="stats-grid stats-grid--dual">
        <article class="stat-card">
          <span>粉丝</span>
          <strong>{{ author.followerCount }}</strong>
        </article>
        <article class="stat-card">
          <span>已发布</span>
          <strong>{{ author.publishedRouteCount }}</strong>
        </article>
      </div>
    </section>

    <section class="panel-card panel-card--tight">
      <div class="section-heading-group">
        <h3 class="section-title">关键点位</h3>
        <p class="section-subtitle">统一按点位卡片列表展示类型、海拔和媒体数量。</p>
      </div>

      <div v-if="routeWaypoints.length" class="waypoint-list">
        <div v-for="waypoint in routeWaypoints" :key="waypoint.id" class="list-card">
          <div class="list-card__thumb list-card__thumb--lake"></div>
          <div>
            <div class="list-card__title">{{ waypoint.title }}</div>
            <div class="meta-text">
              {{ waypoint.type }} · 海拔 {{ waypoint.altitudeM ?? "未知" }}m · 媒体
              {{ waypoint.mediaList.length }}
            </div>
            <p class="body-copy body-copy--compact">{{ waypoint.description }}</p>
          </div>
        </div>
      </div>

      <div v-else class="empty-state empty-state--compact">
        <strong>当前路线暂无点位</strong>
      </div>
    </section>

    <section class="panel-card panel-card--tight">
      <div class="section-heading-group">
        <h3 class="section-title">路线评论</h3>
        <p class="section-subtitle">输入区、提交按钮和最新评论保持同一层级。</p>
      </div>

      <label class="field">
        <span class="field__label">写下你的路线反馈</span>
        <textarea
          :value="commentContent"
          class="field__control field__control--textarea"
          placeholder="比如：补给是否充足、风口提醒是否准确、适合什么天气出发"
          @input="emit('updateCommentContent', ($event.target as HTMLTextAreaElement).value)"
        ></textarea>
      </label>
      <button class="button-primary button-primary--full" :disabled="submittingComment" @click="emit('submitComment')">
        {{ submittingComment ? "发送中..." : "发布评论" }}
      </button>

      <div v-if="commentsLoading" class="empty-state empty-state--compact">
        <strong>评论同步中</strong>
      </div>
      <div v-else-if="commentsError" class="empty-state empty-state--compact">
        <strong>评论加载失败</strong>
        <p>{{ commentsError }}</p>
      </div>
      <div v-else-if="comments.length" class="comment-list">
        <article
          v-for="comment in comments"
          :key="comment.id"
          class="comment-card"
        >
          <div class="section-title-row comment-card__head">
            <strong>{{ comment.authorName }}</strong>
            <span class="meta-text">{{ formatDate(comment.createdAt) }}</span>
          </div>
          <p class="body-copy body-copy--compact">{{ comment.content }}</p>
        </article>
      </div>
    </section>
  </template>
</template>
