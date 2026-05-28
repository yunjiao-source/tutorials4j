import axios from 'axios'
import { aes } from './crypto'
import qs from 'qs'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VUE_APP_API_BASE_URL, // 接口基础地址
  timeout: 10000, // 请求超时时间
  paramsSerializer:params => qs.stringify(params, { arrayFormat: 'repeat' }) // 参数序列化
})

/**
 * 请求拦截器
 * 对标记crypto:true的请求加密请求体
 */
service.interceptors.request.use(config => {
  if (config.crypto) {
    // 从环境变量获取AES密钥
    const key = import.meta.env.VITE_VUE_APP_AES_KEY
    // 加密请求体
    config.data = aes.encrypt(config.data, key)
    // 添加请求头，标记请求已加密
    config.headers['x-encrypt-request'] = 'AES'
    // 设置Content-Type为JSON
    config.headers['Content-Type'] = 'application/json'
  }
  return config
},error => {
  // 请求错误处理
  return Promise.reject(error)
})

/**
 * 响应拦截器
 * 对标记加密的响应自动解密
 */
service.interceptors.response.use(response => {
  // 响应头包含x-encrypt-response，说明响应已加密
  if (response.headers['x-encrypt-response']) {
    const key = import.meta.env.VITE_VUE_APP_AES_KEY
    // 解密响应体
    response.data = aes.decrypt(response.data, key)
  }
  return response.data
},error => {
  // 异常响应解密（如后端返回的加密错误信息）
  if (error.response?.headers['x-encrypt-error']) {
    const key = import.meta.env.VITE_VUE_APP_AES_KEY
    const decryptedError = aes.decrypt(error.response.data, key)
    // 替换错误信息为解密后的内容
    error.message = decryptedError.msg || '请求失败'
  }
  console.log('请求失败:', error)
  return Promise.reject(error)
})

export default service