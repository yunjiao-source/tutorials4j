import type { UserInfo } from '@vben/types';

import { requestClient } from '#/api/request';

/**
 * 获取用户信息
 */
export async function getUserInfoApi():Promise<UserInfo> {
  const raw = await requestClient.get<any>('/oauth/accounts/userinfo');

  return {
    token: '',
    homePath: '',
    avatar: raw.picture,
    realName: raw.name,
    userId: raw.sub,
    username: raw.preferred_username || '',
    desc: raw.profile || '',
    email: raw.email
  };
}
