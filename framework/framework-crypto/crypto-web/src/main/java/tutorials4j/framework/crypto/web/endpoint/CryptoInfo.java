package tutorials4j.framework.crypto.web.endpoint;

import tutorials4j.framework.crypto.core.bean.AsymmetricCryptoStrategy;
import tutorials4j.framework.crypto.core.bean.SymmetricCryptoStrategy;

/**
 * 加密信息记录。
 *
 * <p>封装前端进行加密传输所需的公钥及加解密策略信息，包括非对称加密策略、 对称加密策略以及公钥的十六进制表示。
 *
 * @param asymmetricCryptoStrategy 非对称加密策略
 * @param symmetricCryptoStrategy 对称加密策略
 * @param publicKeyHex 公钥十六进制字符串
 * @author Yun Jiao
 */
public record CryptoInfo(
    AsymmetricCryptoStrategy asymmetricCryptoStrategy,
    SymmetricCryptoStrategy symmetricCryptoStrategy,
    String publicKeyHex) {}
