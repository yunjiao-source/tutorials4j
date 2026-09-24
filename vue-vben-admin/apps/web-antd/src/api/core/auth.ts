import {baseRequestClient, requestClient} from '#/api/request';

export namespace AuthApi {
  /** 登录接口参数 */
  export interface LoginParams {
    password?: string;
    username?: string;
  }

  /** 登录接口返回值 */
  export interface LoginResult {
    token_type: string
    access_token: string
    refresh_token: string
    expires_in: string
    refresh_expires_in: string
    client_id: string
    scope: string
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
  const params = {
    ...data,
    grant_type: 'password',
    client_id: '1001',
    client_secret: 'aaaa-bbbb-cccc-dddd-eeee',
    scope: 'profile,email,phone',
  };
  return requestClient.post<AuthApi.LoginResult>(
    '/oauth/openapi/token',
    undefined,
    { params },
  );
}

/**
 * 刷新accessToken
 */
export async function refreshTokenApi() {
  return baseRequestClient.post<AuthApi.RefreshTokenResult>('/oauth/openapi/refresh', {
    withCredentials: true,
  });
}

/**
 * 退出登录
 */
export async function logoutApi() {
  return baseRequestClient.post('/oauth/openapi/logout', {
    withCredentials: true,
  });
}

/**
 * 获取用户权限码
 */
export async function getAccessCodesApi() {
  return requestClient.get<string[]>('/oauth/accounts/codes');
}
