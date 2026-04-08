<script setup lang="ts">
import type { RecordingStatus } from "../types/recording";
import type { RouteName } from "../app/routes";

defineProps<{
  status: RecordingStatus;
  statusLabel: string;
  elapsed: string;
  distanceKm: number;
  ascentM: number;
  altitudeM: number;
  waypointCount: number;
}>();

const emit = defineEmits<{
  navigate: [route: RouteName];
  requestPause: [];
  requestFinish: [];
  startRecording: [];
  resumeRecording: [];
}>();
</script>

<template>
  <div class="page-header">
    <button class="nav-icon" @click="emit('navigate', 'discover')">←</button>
    <div class="page-header__title">轨迹记录</div>
    <button class="pill-button" @click="emit('navigate', 'add-waypoint')">新增点位</button>
  </div>

  <section class="record-hero">
    <div>
      <p class="record-hero__label">当前状态</p>
      <h2>{{ statusLabel }}</h2>
    </div>
    <div class="status-badge">{{ elapsed }}</div>
  </section>

  <section class="stats-grid">
    <article class="stat-card">
      <span>距离</span>
      <strong>{{ distanceKm }} km</strong>
    </article>
    <article class="stat-card">
      <span>爬升</span>
      <strong>{{ ascentM }} m</strong>
    </article>
    <article class="stat-card">
      <span>海拔</span>
      <strong>{{ altitudeM }} m</strong>
    </article>
    <article class="stat-card">
      <span>点位</span>
      <strong>{{ waypointCount }}</strong>
    </article>
  </section>

  <section class="panel-card panel-card--tight">
    <div class="section-title-row">
      <h3 class="section-title">记录控制</h3>
      <span class="meta-text">状态机演示</span>
    </div>
    <div class="stack-actions">
      <button v-if="status === 'idle'" class="button-primary" @click="emit('startRecording')">
        开始记录
      </button>
      <button v-else-if="status === 'recording'" class="button-primary" @click="emit('requestPause')">
        暂停记录
      </button>
      <button v-else-if="status === 'paused'" class="button-primary" @click="emit('resumeRecording')">
        继续记录
      </button>
      <button
        v-if="status !== 'idle' && status !== 'ended'"
        class="button-secondary"
        @click="emit('requestFinish')"
      >
        结束并整理
      </button>
      <button v-if="status === 'ended'" class="button-secondary" @click="emit('navigate', 'creator-studio')">
        进入创作整理
      </button>
    </div>
  </section>
</template>
