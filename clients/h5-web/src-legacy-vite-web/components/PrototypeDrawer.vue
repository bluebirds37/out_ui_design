<script setup lang="ts">
import type { RouteName } from "../app/routes";

defineProps<{
  open: boolean;
  pageOptions: Array<{ route: RouteName; label: string }>;
}>();

const emit = defineEmits<{
  close: [];
  navigate: [route: RouteName];
}>();
</script>

<template>
  <div v-if="open" class="drawer">
    <button class="drawer__scrim" @click="emit('close')"></button>
    <div class="drawer__sheet">
      <div class="drawer__handle"></div>
      <div class="section-title-row">
        <h3 class="section-title">页面切换</h3>
        <button class="button-ghost" @click="emit('close')">关闭</button>
      </div>
      <div class="drawer__grid">
        <button
          v-for="page in pageOptions"
          :key="page.route"
          class="drawer__link"
          @click="emit('navigate', page.route)"
        >
          {{ page.label }}
        </button>
      </div>
    </div>
  </div>
</template>
