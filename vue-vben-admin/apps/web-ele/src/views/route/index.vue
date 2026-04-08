<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Search } from '@vben/icons';

import {
  ElButton,
  ElCard,
  ElDescriptions,
  ElDescriptionsItem,
  ElDrawer,
  ElEmpty,
  ElInput,
  ElMessage,
  ElPagination,
  ElSelect,
  ElOption,
  ElSkeleton,
  ElTable,
  ElTableColumn,
  ElTag,
} from 'element-plus';

import {
  trailnoteAdminApi,
  type AdminRouteDetail,
  type AdminRouteRow,
} from '#/api/trailnote-admin';

const filters = reactive({
  keyword: '',
  status: '',
});

const loading = ref(false);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);
const routes = ref<AdminRouteRow[]>([]);
const actionLoadingId = ref<number | null>(null);
const drawerVisible = ref(false);
const detailLoading = ref(false);
const currentDetail = ref<AdminRouteDetail | null>(null);

const statusOptions: AdminRouteRow['status'][] = [
  'DRAFT',
  'PENDING_REVIEW',
  'PUBLISHED',
  'REJECTED',
  'ARCHIVED',
];

const filteredRoutes = computed(() =>
  routes.value.filter((route) => {
    const keyword = filters.keyword.trim().toLowerCase();
    const keywordMatched =
      !keyword ||
      route.title.toLowerCase().includes(keyword) ||
      (route.authorName ?? '').toLowerCase().includes(keyword);
    const statusMatched = !filters.status || route.status === filters.status;
    return keywordMatched && statusMatched;
  }),
);

function statusTagType(status: AdminRouteRow['status']) {
  const map: Record<
    AdminRouteRow['status'],
    'danger' | 'info' | 'success' | 'warning' | undefined
  > = {
    DRAFT: 'info',
    PENDING_REVIEW: 'warning',
    PUBLISHED: 'success',
    REJECTED: 'danger',
    ARCHIVED: undefined,
  };
  return map[status];
}

async function loadRoutes() {
  loading.value = true;
  try {
    const resp = await trailnoteAdminApi.getRoutes(page.value, pageSize.value);
    routes.value = resp.records;
    total.value = resp.total;
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '路线列表加载失败');
  } finally {
    loading.value = false;
  }
}

async function updateStatus(row: AdminRouteRow, status: AdminRouteRow['status']) {
  actionLoadingId.value = row.id;
  try {
    const result = await trailnoteAdminApi.updateRouteStatus(row.id, status);
    routes.value = routes.value.map((route) =>
      route.id === row.id ? { ...route, status: result.status, updatedAt: result.updatedAt } : route,
    );
    ElMessage.success(`路线状态已更新为 ${status}`);
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '路线状态更新失败');
  } finally {
    actionLoadingId.value = null;
  }
}

async function openDetail(row: AdminRouteRow) {
  drawerVisible.value = true;
  detailLoading.value = true;
  try {
    currentDetail.value = await trailnoteAdminApi.getRouteDetail(row.id);
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '路线详情加载失败');
  } finally {
    detailLoading.value = false;
  }
}

function resetFilters() {
  filters.keyword = '';
  filters.status = '';
}

onMounted(() => {
  void loadRoutes();
});
</script>

<template>
  <Page :auto-content-height="true">
    <div class="flex h-full w-full flex-col gap-5">
      <ElCard>
        <div class="mb-4">
          <h2 class="text-lg font-semibold">路线管理</h2>
          <p class="mt-1 text-sm text-gray-500">接入 `/admin/routes` 与状态流转接口，便于审核和上架管理。</p>
        </div>
        <div class="flex flex-wrap items-center gap-3">
          <div class="w-80">
            <ElInput
              v-model="filters.keyword"
              clearable
              placeholder="搜索路线标题或作者"
              :prefix-icon="Search"
            />
          </div>
          <div class="w-48">
            <ElSelect v-model="filters.status" clearable placeholder="路线状态">
              <ElOption v-for="item in statusOptions" :key="item" :label="item" :value="item" />
            </ElSelect>
          </div>
          <ElButton type="primary" @click="loadRoutes">刷新</ElButton>
          <ElButton @click="resetFilters">重置</ElButton>
        </div>
      </ElCard>

      <ElCard class="h-full flex-1" body-class="h-full">
        <div class="flex h-full flex-col justify-between">
          <ElSkeleton :loading="loading" animated :rows="6">
            <template #default>
              <ElTable v-if="filteredRoutes.length" :data="filteredRoutes" class="h-full flex-1">
                <ElTableColumn prop="id" label="ID" width="80" />
                <ElTableColumn prop="title" label="路线标题" min-width="220" />
                <ElTableColumn prop="authorName" label="作者" min-width="140" />
                <ElTableColumn label="状态" width="150">
                  <template #default="{ row }">
                    <ElTag :type="statusTagType(row.status)">{{ row.status }}</ElTag>
                  </template>
                </ElTableColumn>
                <ElTableColumn prop="waypointCount" label="点位数" width="100" />
                <ElTableColumn prop="favoriteCount" label="收藏数" width="100" />
                <ElTableColumn prop="updatedAt" label="更新时间" min-width="180" />
                <ElTableColumn label="操作" width="280" fixed="right">
                  <template #default="{ row }">
                    <div class="flex flex-wrap gap-2">
                      <ElButton link type="primary" @click="openDetail(row)">详情</ElButton>
                      <ElButton
                        v-for="status in statusOptions"
                        :key="status"
                        link
                        :disabled="row.status === status"
                        :loading="actionLoadingId === row.id && row.status !== status"
                        @click="updateStatus(row, status)"
                      >
                        {{ status }}
                      </ElButton>
                    </div>
                  </template>
                </ElTableColumn>
              </ElTable>
              <ElEmpty v-else description="没有符合条件的路线数据" />
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
                  loadRoutes();
                }
              "
            />
          </div>
        </div>
      </ElCard>
    </div>

    <ElDrawer v-model="drawerVisible" title="路线审核详情" size="480px">
      <ElSkeleton :loading="detailLoading" animated :rows="8">
        <template #default>
          <ElDescriptions v-if="currentDetail" :column="1" border>
            <ElDescriptionsItem label="路线标题">{{ currentDetail.title }}</ElDescriptionsItem>
            <ElDescriptionsItem label="作者">{{ currentDetail.authorName || '未知作者' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="状态">
              <ElTag :type="statusTagType(currentDetail.status)">{{ currentDetail.status }}</ElTag>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="难度">{{ currentDetail.difficulty }}</ElDescriptionsItem>
            <ElDescriptionsItem label="路线数据">
              {{ currentDetail.distanceKm }}km / {{ currentDetail.durationMinutes }}min / 爬升
              {{ currentDetail.ascentM }}m
            </ElDescriptionsItem>
            <ElDescriptionsItem label="互动数据">
              收藏 {{ currentDetail.favoriteCount }} / 评论 {{ currentDetail.commentCount }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="标签">{{ currentDetail.tags || '无' }}</ElDescriptionsItem>
            <ElDescriptionsItem label="路线描述">
              {{ currentDetail.description || '暂无描述' }}
            </ElDescriptionsItem>
            <ElDescriptionsItem label="关键点位">
              <div class="grid gap-2">
                <div
                  v-for="waypoint in currentDetail.waypoints"
                  :key="waypoint.id"
                  class="rounded-lg bg-slate-50 px-3 py-2"
                >
                  <div class="font-medium">
                    {{ waypoint.sortOrder }}. {{ waypoint.title }} / {{ waypoint.waypointType }}
                  </div>
                  <div class="mt-1 text-sm text-gray-500">{{ waypoint.description || '暂无说明' }}</div>
                </div>
              </div>
            </ElDescriptionsItem>
          </ElDescriptions>
        </template>
      </ElSkeleton>
    </ElDrawer>
  </Page>
</template>
