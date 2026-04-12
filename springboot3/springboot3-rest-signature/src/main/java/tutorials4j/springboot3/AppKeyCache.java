package tutorials4j.springboot3;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用配置中心管理密钥
 * 支持多版本密钥并存
 * 平滑切换，兼容新旧密钥
 *
 * @author Yun Jiao
 */
public final class AppKeyCache {
    private final static Map<String, String> cache = new ConcurrentHashMap<>();

    static {
        cache.put("your_app_key", "your_app_secret");
    }

    public static String getSecret(String appKey) {
        return cache.get(appKey);
    }
}
