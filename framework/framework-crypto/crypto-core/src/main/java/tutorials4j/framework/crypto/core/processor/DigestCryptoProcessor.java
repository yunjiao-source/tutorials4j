package tutorials4j.framework.crypto.core.processor;

import java.nio.charset.Charset;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface DigestCryptoProcessor extends CryptoProcessor {
  String createKey();

  String digest(String data);

  String digest(String data, Charset charset);
}
