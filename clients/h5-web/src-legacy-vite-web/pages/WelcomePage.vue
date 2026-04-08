<script setup lang="ts">
import type { RouteName } from "../app/routes";

const emit = defineEmits<{
  navigate: [route: RouteName];
}>();
</script>

<template>
  <section class="hero-card">
    <p class="hero-card__eyebrow">TrailNote</p>
    <h2 class="hero-card__title">分享每一段值得被记住的徒步路线</h2>
    <p class="hero-card__body">
      记录轨迹、补充关键点位、附上照片和视频，让后来者看见路线真正的样子。
    </p>
    <div class="stack-actions">
      <button class="button-primary" @click="emit('navigate', 'login')">登录 / 注册</button>
      <button class="button-secondary" @click="emit('navigate', 'discover')">先看看热门路线</button>
    </div>
  </section>
</template>
