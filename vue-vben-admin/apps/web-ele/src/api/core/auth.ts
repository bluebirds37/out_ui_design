import { baseRequestClient } from '#/api/request';

import { trailnoteAdminApi } from '#/api/trailnote-admin';

export namespace AuthApi {
  /** 登录接口参数 */
  export interface LoginParams {
    password?: string;
    username?: string;
  }

  /** 登录接口返回值 */
  export interface LoginResult {
    accessToken: string;
  }

  export interface RefreshTokenResult {
    data: string;
    status: number;
  }
}

/**
 * 登录
 */
export async function loginApi(data: AuthApi.LoginParams) {
  const result = await trailnoteAdminApi.login(data);
  return {
    accessToken: result.accessToken,
  };
}

/**
 * 刷新accessToken
 */
export async function refreshTokenApi() {
  return baseRequestClient.post<AuthApi.RefreshTokenResult>('/auth/refresh', {
    withCredentials: true,
  });
}

/**
 * 退出登录
 */
export async function logoutApi() {
  return trailnoteAdminApi.logout();
}

/**
 * 获取用户权限码
 */
export async function getAccessCodesApi() {
  return trailnoteAdminApi.getAccessCodes();
}
