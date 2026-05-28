import request from "./request.js";

/**
 * 登录接口请求
 * @param {Object} data 登录参数（用户名+密码）
 * @returns {Promise} 请求Promise
 */
export function postSecureData(data) {
  return request({
    url: '/api/auth/admin/login',
    method: 'post',
    data: data,
    crypto: true // 启用加解密
  })
}