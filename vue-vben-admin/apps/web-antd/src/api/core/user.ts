import type { UserInfo } from '@vben/types';

import { requestClient } from '#/api/request';

/**
 * 获取用户信息
 */
export async function getUserInfoApi() {
  const raw = await requestClient.get('/oauth/common/userinfo');

  return {
    avatar: raw.picture,
    realName: raw.name,
    userId: raw.sub,
    username: raw.preferred_username,
    desc: raw.profile,
    email: raw.email
  };
}
