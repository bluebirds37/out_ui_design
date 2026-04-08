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
  type AdminUserRow,
  type AdminUserSummary,
} from '#/api/trailnote-admin';

const searchReq = reactive({
  words: '',
  status: '',
});

const loading = ref(false);
const actionLoadingId = ref<number | null>(null);
const drawerVisible = ref(false);
const detailLoading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const users = ref<AdminUserRow[]>([]);
const summary = ref<AdminUserSummary | null>(null);
const currentRow = ref<AdminUserRow | null>(null);

const filteredUsers = computed(() => {
  return users.value.filter((user) => {
    const keyword = searchReq.words.trim().toLowerCase();
    const matchKeyword =
      !keyword ||
      user.username.toLowerCase().includes(keyword) ||
      user.roleCode.toLowerCase().includes(keyword);
    const matchStatus = !searchReq.status || user.status === searchReq.status;
    return matchKeyword && matchStatus;
  });
});

function statusTagType(status: AdminUserRow['status']) {
  return status === 'ACTIVE' ? 'success' : 'info';
}

async function loadUsers() {
  loading.value = true;
  try {
    const [pageResp, summaryResp] = await Promise.all([
      trailnoteAdminApi.getUsers(currentPage.value, pageSize.value),
      trailnoteAdminApi.getUserSummary(),
    ]);
    users.value = pageResp.records;
    total.value = pageResp.total;
    summary.value = summaryResp;
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '用户列表加载失败');
  } finally {
    loading.value = false;
  }
}

async function openDetail(row: AdminUserRow) {
  drawerVisible.value = true;
  detailLoading.value = true;
  try {
    currentRow.value = await trailnoteAdminApi.getUserDetail(row.id);
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '用户详情加载失败');
  } finally {
    detailLoading.value = false;
  }
}

async function toggleStatus(row: AdminUserRow) {
  const nextStatus = row.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE';
  actionLoadingId.value = row.id;
  try {
    const updated = await trailnoteAdminApi.updateUserStatus(row.id, nextStatus);
    users.value = users.value.map((user) => (user.id === row.id ? updated : user));
    if (currentRow.value?.id === row.id) {
      currentRow.value = updated;
    }
    await loadUsers();
    ElMessage.success(nextStatus === 'ACTIVE' ? '用户已启用' : '用户已禁用');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '状态更新失败');
  } finally {
    actionLoadingId.value = null;
  }
}

function resetFilters() {
  searchReq.words = '';
  searchReq.status = '';
}

onMounted(() => {
  void loadUsers();
});
</script>

<template>
  <Page :auto-content-height="true">
    <div class="flex h-full w-full flex-col gap-5">
      <ElCard>
        <div class="mb-4 flex flex-wrap gap-3">
          <div class="rounded-xl bg-slate-50 px-4 py-3">
            <div class="text-xs text-gray-500">管理员总数</div>
            <div class="mt-1 text-2xl font-semibold">{{ summary?.totalUsers ?? 0 }}</div>
          </div>
          <div class="rounded-xl bg-emerald-50 px-4 py-3">
            <div class="text-xs text-gray-500">启用中</div>
            <div class="mt-1 text-2xl font-semibold text-emerald-600">
              {{ summary?.activeUsers ?? 0 }}
            </div>
          </div>
          <div class="rounded-xl bg-slate-100 px-4 py-3">
            <div class="text-xs text-gray-500">已禁用</div>
            <div class="mt-1 text-2xl font-semibold text-slate-600">
              {{ summary?.disabledUsers ?? 0 }}
            </div>
          </div>
        </div>

        <div class="flex flex-wrap items-center gap-3">
          <div class="w-80">
            <ElInput
              v-model="searchReq.words"
              clearable
              placeholder="搜索用户名或角色"
              :prefix-icon="Search"
            />
          </div>
          <div class="w-40">
            <ElSelect v-model="searchReq.status" clearable placeholder="用户状态">
              <ElOption label="启用中" value="ACTIVE" />
              <ElOption label="已禁用" value="DISABLED" />
            </ElSelect>
          </div>
          <ElButton type="primary" @click="loadUsers">刷新</ElButton>
          <ElButton @click="resetFilters">重置</ElButton>
        </div>
      </ElCard>

      <ElCard class="h-full flex-1" body-class="h-full">
        <div class="flex h-full flex-col justify-between">
          <ElSkeleton :loading="loading" animated :rows="6">
            <template #default>
              <ElTable v-if="filteredUsers.length" :data="filteredUsers" class="h-full flex-1">
                <ElTableColumn prop="id" label="ID" width="80" />
                <ElTableColumn prop="username" label="用户名" min-width="180" />
                <ElTableColumn prop="roleCode" label="角色编码" min-width="160" />
                <ElTableColumn label="状态" width="120">
                  <template #default="{ row }">
                    <ElTag :type="statusTagType(row.status)">{{ row.status }}</ElTag>
                  </template>
                </ElTableColumn>
                <ElTableColumn prop="updatedAt" label="更新时间" min-width="180" />
                <ElTableColumn label="操作" width="220" fixed="right">
                  <template #default="{ row }">
                    <div class="flex gap-2">
                      <ElButton link type="primary" @click="openDetail(row)">详情</ElButton>
                      <ElButton
                        link
                        :loading="actionLoadingId === row.id"
                        @click="toggleStatus(row)"
                      >
                        {{ row.status === 'ACTIVE' ? '禁用' : '启用' }}
                      </ElButton>
                    </div>
                  </template>
                </ElTableColumn>
              </ElTable>
              <ElEmpty v-else description="没有符合当前筛选条件的管理员" />
            </template>
          </ElSkeleton>

          <div class="mt-4 flex items-center justify-center">
            <ElPagination
              background
              layout="prev, pager, next"
              :current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              @current-change="
                (page) => {
                  currentPage = page;
                  loadUsers();
                }
              "
            />
          </div>
        </div>
      </ElCard>
    </div>

    <ElDrawer v-model="drawerVisible" title="管理员详情" size="420px">
      <ElSkeleton :loading="detailLoading" animated :rows="6">
        <template #default>
          <ElDescriptions v-if="currentRow" :column="1" border>
            <ElDescriptionsItem label="ID">{{ currentRow.id }}</ElDescriptionsItem>
            <ElDescriptionsItem label="用户名">{{ currentRow.username }}</ElDescriptionsItem>
            <ElDescriptionsItem label="角色">{{ currentRow.roleCode }}</ElDescriptionsItem>
            <ElDescriptionsItem label="状态">
              <ElTag :type="statusTagType(currentRow.status)">{{ currentRow.status }}</ElTag>
            </ElDescriptionsItem>
            <ElDescriptionsItem label="创建时间">{{ currentRow.createdAt }}</ElDescriptionsItem>
            <ElDescriptionsItem label="更新时间">{{ currentRow.updatedAt }}</ElDescriptionsItem>
          </ElDescriptions>
        </template>
      </ElSkeleton>
    </ElDrawer>
  </Page>
</template>
