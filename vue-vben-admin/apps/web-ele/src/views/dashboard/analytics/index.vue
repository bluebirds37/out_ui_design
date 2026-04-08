<script lang="ts" setup>
import type { AnalysisOverviewItem } from '@vben/common-ui';
import type { TabOption } from '@vben/types';

import { computed, onMounted, ref } from 'vue';

import {
  AnalysisChartCard,
  AnalysisChartsTabs,
  AnalysisOverview,
  Page,
} from '@vben/common-ui';
import {
  SvgBellIcon,
  SvgCakeIcon,
  SvgCardIcon,
  SvgDownloadIcon,
} from '@vben/icons';

import { ElButton, ElCard, ElSkeleton, ElTag } from 'element-plus';

import { trailnoteAdminApi, type AdminDashboardOverview } from '#/api/trailnote-admin';

import AnalyticsTrends from './analytics-trends.vue';
import AnalyticsVisitsData from './analytics-visits-data.vue';
import AnalyticsVisitsSales from './analytics-visits-sales.vue';
import AnalyticsVisitsSource from './analytics-visits-source.vue';
import AnalyticsVisits from './analytics-visits.vue';

const loading = ref(false);
const error = ref('');
const overview = ref<AdminDashboardOverview | null>(null);

const chartTabs: TabOption[] = [
  {
    label: '流量趋势',
    value: 'trends',
  },
  {
    label: '月访问量',
    value: 'visits',
  },
];

const overviewItems = computed<AnalysisOverviewItem[]>(() => {
  const data = overview.value;
  return [
    {
      icon: SvgCardIcon,
      title: '用户量',
      totalTitle: '总用户量',
      totalValue: data?.totalUsers ?? 0,
      value: data?.totalUsers ?? 0,
    },
    {
      icon: SvgCakeIcon,
      title: '路线量',
      totalTitle: '总路线量',
      totalValue: data?.totalRoutes ?? 0,
      value: data?.publishedRoutes ?? 0,
    },
    {
      icon: SvgDownloadIcon,
      title: '评论量',
      totalTitle: '总评论量',
      totalValue: data?.totalComments ?? 0,
      value: data?.totalComments ?? 0,
    },
    {
      icon: SvgBellIcon,
      title: '收藏量',
      totalTitle: '总收藏量',
      totalValue: data?.totalFavorites ?? 0,
      value: data?.pendingOrDraftRoutes ?? 0,
    },
  ];
});

async function loadOverview() {
  loading.value = true;
  error.value = '';
  try {
    overview.value = await trailnoteAdminApi.getOverview();
  } catch (loadError) {
    error.value = loadError instanceof Error ? loadError.message : '概览数据加载失败';
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  void loadOverview();
});
</script>

<template>
  <Page auto-content-height>
    <div class="p-5">
      <div class="mb-5 flex items-center justify-between">
        <div>
          <h2 class="text-xl font-semibold">TrailNote 管理概览</h2>
          <p class="mt-1 text-sm text-gray-500">
            这里接的是本地 `spring-boot admin-api` 实时统计，不再是默认演示数据。
          </p>
        </div>
        <ElButton type="primary" @click="loadOverview">刷新概览</ElButton>
      </div>

      <ElCard class="mb-5">
        <ElSkeleton :loading="loading" animated :rows="4">
          <template #default>
            <div v-if="error" class="flex items-center justify-between gap-4">
              <div>
                <strong>概览数据加载失败</strong>
                <p class="mt-1 text-sm text-gray-500">{{ error }}</p>
              </div>
              <ElButton @click="loadOverview">重试</ElButton>
            </div>
            <div v-else class="flex flex-wrap items-center gap-3 text-sm text-gray-500">
              <span>生成时间：{{ overview?.generatedAt || '--' }}</span>
              <ElTag type="success">已接真实接口</ElTag>
              <ElTag type="warning">待接后台前端更多模块</ElTag>
            </div>
          </template>
        </ElSkeleton>
      </ElCard>

      <AnalysisOverview :items="overviewItems" />
      <AnalysisChartsTabs :tabs="chartTabs" class="mt-5">
        <template #trends>
          <AnalyticsTrends />
        </template>
        <template #visits>
          <AnalyticsVisits />
        </template>
      </AnalysisChartsTabs>

      <div class="mt-5 grid gap-5 md:grid-cols-3">
        <AnalysisChartCard title="发布态路线">
          <div class="text-3xl font-semibold">{{ overview?.publishedRoutes ?? 0 }}</div>
          <p class="mt-2 text-sm text-gray-500">当前已发布可见的路线总数</p>
        </AnalysisChartCard>
        <AnalysisChartCard title="待处理路线">
          <div class="text-3xl font-semibold">{{ overview?.pendingOrDraftRoutes ?? 0 }}</div>
          <p class="mt-2 text-sm text-gray-500">草稿与待审核路线数量</p>
        </AnalysisChartCard>
        <AnalysisChartCard title="用户总量">
          <div class="text-3xl font-semibold">{{ overview?.totalUsers ?? 0 }}</div>
          <p class="mt-2 text-sm text-gray-500">当前业务库用户总量</p>
        </AnalysisChartCard>
      </div>

      <div class="mt-5 w-full md:flex">
        <AnalysisChartCard class="mt-5 md:mr-4 md:mt-0 md:w-1/3" title="访问数量">
          <AnalyticsVisitsData />
        </AnalysisChartCard>
        <AnalysisChartCard class="mt-5 md:mr-4 md:mt-0 md:w-1/3" title="访问来源">
          <AnalyticsVisitsSource />
        </AnalysisChartCard>
        <AnalysisChartCard class="mt-5 md:mt-0 md:w-1/3" title="访问来源">
          <AnalyticsVisitsSales />
        </AnalysisChartCard>
      </div>
    </div>
  </Page>
</template>
