import CryptoJS from 'crypto-js'

// 加解密配置（与后端一致：CBC模式+PKCS7填充）
const CRYPTO_CONFIG = {
  mode: CryptoJS.mode.CBC,
  padding: CryptoJS.pad.Pkcs7
}

/**
 * AES加解密工具
 * 与后端AESCryptoProcessor算法完全对齐
 */
export const aes = {
  /**
   * AES加密
   * @param {Object} data 待加密的原始数据
   * @param {String} key BASE64格式的AES密钥
   * @returns {String} 加密后的密文
   */
  encrypt(data, key) {
    // 解码BASE64密钥
    const keyBytes = CryptoJS.enc.Base64.parse(key)
    // 取密钥前16字节作为IV向量（与后端一致）
    const iv = CryptoJS.lib.WordArray.create(keyBytes.words.slice(0, 4))
    // 执行加密
    const encrypted = CryptoJS.AES.encrypt(
        JSON.stringify(data),
        keyBytes,
        { ...CRYPTO_CONFIG, iv }
    )
    // 返回BASE64格式密文
    return encrypted.toString()
  },

  /**
   * AES解密
   * @param {String} ciphertext 加密后的密文
   * @param {String} key BASE64格式的AES密钥
   * @returns {Object} 解密后的原始数据
   */
  decrypt(ciphertext, key) {
    // 解码BASE64密钥
    const keyBytes = CryptoJS.enc.Base64.parse(key)
    // 取密钥前16字节作为IV向量（与后端一致）
    const iv = CryptoJS.lib.WordArray.create(keyBytes.words.slice(0, 4))
    // 执行解密
    const decrypted = CryptoJS.AES.decrypt(
        ciphertext,
        keyBytes,
        { ...CRYPTO_CONFIG, iv }
    )
    // 转换为JSON对象返回
    return JSON.parse(decrypted.toString(CryptoJS.enc.Utf8))
  }
}