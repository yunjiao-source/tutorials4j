package tutorials4j.framework.crypto.web;

import tutorials4j.framework.crypto.core.AsymmetricCryptoStrategy;
import tutorials4j.framework.crypto.core.SymmetricCryptoStrategy;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record CryptoInfo(
    AsymmetricCryptoStrategy asymmetricCryptoStrategy,
    SymmetricCryptoStrategy symmetricCryptoStrategy,
    String publicKeyHex) {}
