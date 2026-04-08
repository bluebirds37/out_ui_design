<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Search } from '@vben/icons';

import {
  ElButton,
  ElCard,
  ElEmpty,
  ElPagination,
  ElSkeleton,
  ElTable,
  ElTableColumn,
  ElTag,
  ElInput,
} from 'element-plus';

import {
  trailnoteAdminApi,
  type AdminCommentRow,
} from '#/api/trailnote-admin';

const filters = reactive({
  keyword: '',
});

const loading = ref(false);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);
const comments = ref<AdminCommentRow[]>([]);

const filteredComments = computed(() =>
  comments.value.filter((comment) => {
    const keyword = filters.keyword.trim().toLowerCase();
    return (
      !keyword ||
      (comment.routeTitle ?? '').toLowerCase().includes(keyword) ||
      (comment.authorName ?? '').toLowerCase().includes(keyword) ||
      comment.content.toLowerCase().includes(keyword)
    );
  }),
);

async function loadComments() {
  loading.value = true;
  try {
    const resp = await trailnoteAdminApi.getComments(page.value, pageSize.value);
    comments.value = resp.records;
    total.value = resp.total;
  } finally {
    loading.value = false;
  }
}

function resetFilters() {
  filters.keyword = '';
}

onMounted(() => {
  void loadComments();
});
</script>

<template>
  <Page :auto-content-height="true">
    <div class="flex h-full w-full flex-col gap-5">
      <ElCard>
        <div class="mb-4">
          <h2 class="text-lg font-semibold">评论管理</h2>
          <p class="mt-1 text-sm text-gray-500">接入 `/admin/comments`，用于快速排查路线下的讨论内容。</p>
        </div>
        <div class="flex flex-wrap items-center gap-3">
          <div class="w-96">
            <ElInput
              v-model="filters.keyword"
              clearable
              placeholder="搜索路线、评论作者或评论内容"
              :prefix-icon="Search"
            />
          </div>
          <ElButton type="primary" @click="loadComments">刷新</ElButton>
          <ElButton @click="resetFilters">重置</ElButton>
        </div>
      </ElCard>

      <ElCard class="h-full flex-1" body-class="h-full">
        <div class="flex h-full flex-col justify-between">
          <ElSkeleton :loading="loading" animated :rows="6">
            <template #default>
              <ElTable v-if="filteredComments.length" :data="filteredComments" class="h-full flex-1">
                <ElTableColumn prop="id" label="ID" width="80" />
                <ElTableColumn prop="routeTitle" label="所属路线" min-width="200" />
                <ElTableColumn prop="authorName" label="评论作者" min-width="140" />
                <ElTableColumn label="评论内容" min-width="320">
                  <template #default="{ row }">
                    <div class="leading-6">{{ row.content }}</div>
                  </template>
                </ElTableColumn>
                <ElTableColumn prop="createdAt" label="创建时间" min-width="180" />
                <ElTableColumn label="标签" width="120">
                  <template #default="{ row }">
                    <ElTag type="info">路线 #{{ row.routeId }}</ElTag>
                  </template>
                </ElTableColumn>
              </ElTable>
              <ElEmpty v-else description="没有符合条件的评论数据" />
            </template>
          </ElSkeleton>

          <div class="mt-4 flex items-center justify-center">
            <ElPagination
              background
              layout="prev, pager, next"
              :current-page="page"
              :page-size="pageSize"
              :total="total"
              @current-change="
                (value) => {
                  page = value;
                  loadComments();
                }
              "
            />
          </div>
        </div>
      </ElCard>
    </div>
  </Page>
</template>
