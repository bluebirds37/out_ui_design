<script setup lang="ts">
defineProps<{
  selectedType: string;
  expandedTypes: boolean;
  title: string;
  description: string;
  baseTypes: string[];
  expandedTypeOptions: string[];
  titleError: string;
  descriptionError: string;
}>();

const emit = defineEmits<{
  back: [];
  toggleExpandedTypes: [];
  selectType: [value: string];
  updateTitle: [value: string];
  updateDescription: [value: string];
  markTitleTouched: [];
  markDescriptionTouched: [];
  save: [];
}>();
</script>

<template>
  <div class="page-header">
    <button class="nav-icon" @click="emit('back')">←</button>
    <div class="page-header__title">新增点位</div>
    <div class="page-header__placeholder"></div>
  </div>

  <section class="panel-card">
    <div class="section-title-row">
      <h3 class="section-title">点位类型</h3>
      <button class="button-ghost" @click="emit('toggleExpandedTypes')">
        {{ expandedTypes ? "收起" : "展开更多" }}
      </button>
    </div>
    <div class="chip-row">
      <button
        v-for="type in baseTypes"
        :key="type"
        class="chip"
        :class="{ 'chip--selected': selectedType === type }"
        @click="emit('selectType', type)"
      >
        {{ type }}
      </button>
      <button
        v-for="type in expandedTypes ? expandedTypeOptions : []"
        :key="type"
        class="chip chip--soft"
        :class="{ 'chip--selected': selectedType === type }"
        @click="emit('selectType', type)"
      >
        {{ type }}
      </button>
    </div>
    <div class="media-actions">
      <button class="button-secondary">拍照添加</button>
      <button class="button-secondary">录像添加</button>
    </div>
    <label class="field">
      <span class="field__label">标题</span>
      <input
        :value="title"
        class="field__control"
        type="text"
        placeholder="例如：山脊观景台"
        @input="emit('updateTitle', ($event.target as HTMLInputElement).value)"
        @blur="emit('markTitleTouched')"
      />
      <span v-if="titleError" class="field__error">{{ titleError }}</span>
    </label>
    <label class="field">
      <span class="field__label">说明</span>
      <textarea
        :value="description"
        class="field__control field__control--textarea"
        placeholder="补充补给、风险、拍摄建议或路况提示"
        @input="emit('updateDescription', ($event.target as HTMLTextAreaElement).value)"
        @blur="emit('markDescriptionTouched')"
      ></textarea>
      <span v-if="descriptionError" class="field__error">{{ descriptionError }}</span>
    </label>
    <button class="button-primary" @click="emit('save')">保存点位</button>
  </section>
</template>
