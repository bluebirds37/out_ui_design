import type { RouteRecordStringComponent } from '@vben/types';

import { trailnoteAdminApi } from '#/api/trailnote-admin';

/**
 * 获取用户所有菜单
 */
export async function getAllMenusApi() {
  return (await trailnoteAdminApi.getMenus()) as RouteRecordStringComponent[];
}
