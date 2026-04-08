import type { UserInfo } from '@vben/types';

import { trailnoteAdminApi } from '#/api/trailnote-admin';

/**
 * 获取用户信息
 */
export async function getUserInfoApi() {
  return (await trailnoteAdminApi.getCurrentUser()) as UserInfo;
}
