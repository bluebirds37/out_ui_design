<script setup lang="ts">
import type { CreatorRouteRow, RouteDraftDetail } from "../api";
import type { RouteName } from "../app/routes";

defineProps<{
  loading: boolean;
  error: string;
  saving: boolean;
  publishing: boolean;
  draft: RouteDraftDetail | null;
  routes: CreatorRouteRow[];
  creatorForm: {
    title: string;
    description: string;
    difficulty: string;
    distanceKm: number;
    durationMinutes: number;
    ascentM: number;
    maxAltitudeM: number;
    tags: string;
  };
  formatDuration: (minutes: number | undefined) => string;
}>();

const emit = defineEmits<{
  navigate: [route: RouteName];
  refresh: [];
  updateField: [field: string, value: string | number];
  saveDraft: [];
  publishDraft: [];
}>();
</script>

<template>
  <div class="page-header">
    <button class="nav-icon" @click="emit('navigate', 'record-live')">←</button>
    <div class="page-header__title">创作台</div>
    <button class="pill-button" @click="emit('refresh')">刷新</button>
  </div>

  <section v-if="loading" class="panel-card panel-card--tight">
    <div class="empty-state">
      <strong>创作数据同步中</strong>
      <p>正在读取当前草稿与我的路线列表。</p>
    </div>
  </section>

  <section v-else-if="error" class="panel-card panel-card--tight">
    <div class="empty-state">
      <strong>创作台加载失败</strong>
      <p>{{ error }}</p>
    </div>
  </section>

  <template v-else>
    <section class="panel-card">
      <div class="section-title-row">
        <h3 class="section-title">当前草稿</h3>
        <span class="meta-text">{{ draft?.status || "新建中" }}</span>
      </div>
      <label class="field">
        <span class="field__label">路线标题</span>
        <input
          :value="creatorForm.title"
          class="field__control"
          type="text"
          @input="emit('updateField', 'title', ($event.target as HTMLInputElement).value)"
        />
      </label>
      <label class="field">
        <span class="field__label">路线说明</span>
        <textarea
          :value="creatorForm.description"
          class="field__control field__control--textarea"
          @input="emit('updateField', 'description', ($event.target as HTMLTextAreaElement).value)"
        ></textarea>
      </label>
      <label class="field">
        <span class="field__label">标签</span>
        <input
          :value="creatorForm.tags"
          class="field__control"
          type="text"
          @input="emit('updateField', 'tags', ($event.target as HTMLInputElement).value)"
        />
      </label>
      <div class="stats-grid">
        <label class="field">
          <span class="field__label">距离 km</span>
          <input
            :value="creatorForm.distanceKm"
            class="field__control"
            type="number"
            @input="emit('updateField', 'distanceKm', Number(($event.target as HTMLInputElement).value))"
          />
        </label>
        <label class="field">
          <span class="field__label">时长 min</span>
          <input
            :value="creatorForm.durationMinutes"
            class="field__control"
            type="number"
            @input="emit('updateField', 'durationMinutes', Number(($event.target as HTMLInputElement).value))"
          />
        </label>
        <label class="field">
          <span class="field__label">爬升 m</span>
          <input
            :value="creatorForm.ascentM"
            class="field__control"
            type="number"
            @input="emit('updateField', 'ascentM', Number(($event.target as HTMLInputElement).value))"
          />
        </label>
        <label class="field">
          <span class="field__label">最高海拔</span>
          <input
            :value="creatorForm.maxAltitudeM"
            class="field__control"
            type="number"
            @input="emit('updateField', 'maxAltitudeM', Number(($event.target as HTMLInputElement).value))"
          />
        </label>
      </div>
      <div class="stack-actions">
        <button class="button-primary" :disabled="saving" @click="emit('saveDraft')">
          {{ saving ? "保存中..." : "保存草稿" }}
        </button>
        <button class="button-secondary" :disabled="publishing" @click="emit('publishDraft')">
          {{ publishing ? "提交中..." : "提交审核" }}
        </button>
      </div>
    </section>

    <section class="panel-card panel-card--tight">
      <div class="section-title-row">
        <h3 class="section-title">我的路线</h3>
        <span class="meta-text">{{ routes.length }} 条</span>
      </div>
      <div v-if="routes.length" class="route-list">
        <article
          v-for="route in routes"
          :key="route.id"
          class="comment-card"
        >
          <div class="section-title-row comment-card__head">
            <strong>{{ route.title }}</strong>
            <span class="meta-text">{{ route.status }}</span>
          </div>
          <p class="body-copy body-copy--compact">
            {{ route.distanceKm }}km · {{ formatDuration(route.durationMinutes) }} · 点位 {{ route.waypointCount }}
          </p>
        </article>
      </div>
      <div v-else class="empty-state empty-state--compact">
        <strong>当前还没有创作记录</strong>
      </div>
    </section>
  </template>
</template>
