package tutorials4j.framework.crypto.web.endpoint;

import tutorials4j.framework.crypto.core.bean.AsymmetricCryptoStrategy;
import tutorials4j.framework.crypto.core.bean.SymmetricCryptoStrategy;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record CryptoInfo(
    AsymmetricCryptoStrategy asymmetricCryptoStrategy,
    SymmetricCryptoStrategy symmetricCryptoStrategy,
    String publicKeyHex) {}
