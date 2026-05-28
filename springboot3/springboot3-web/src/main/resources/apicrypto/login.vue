<template>
  <div class="login-container">
    <a-input v-model:value="user.username" placeholder="请输入用户名" style="margin-bottom: 10px"/>
    <a-input v-model:value="user.password" type="password" placeholder="请输入密码" style="margin-bottom: 10px"/>
    <a-button type="primary" @click="login">请求接口</a-button>
  </div>
</template>

<script setup>
import { postSecureData } from './userInfoRequest.js'
import { reactive } from "vue";
import { aes } from './crypto.js'
import { message } from "ant-design-vue";

// 响应式用户数据
const user = reactive({
  username: 'admin',
  password: '123456'
})

// 登录方法
const login = async () => {
  try {
    console.log("点击成功，原始数据：", user)
    // 调用登录接口
    const res = await postSecureData(user)
    if (res.code === 200 && res.success) {
      message.success('登录成功')
      console.log('登录成功，令牌：', res.data)
    } else {
      message.error(res.msg)
    }
  } catch (error) {
    message.error(error.message)
  }
}

// 测试加解密工具类
const testKey = 'MTIzNDU2Nzg5MDEyMzQ1Ng=='
console.log('前端加密测试：', aes.encrypt(user, testKey))
console.log('前端解密测试：', aes.decrypt('6tmr623112r3OFkPes2XetsKcVO/FgDE+UD3cT8dxBWC8c8gKMNcqAhxPORuk3W52YPDDP0XKLacMp1Jk8h5MUDOsYKzVo6BaH6Y2aUdggJCu5+j/q65nMDMXTY5qPWwOFSYGIr4Hz9tm6VEwAXEAQ==', testKey))
</script>