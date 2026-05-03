package tutorials4j.framework.examples.template;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import tutorials4j.framework.cache.core.template.AbstractMultiLevelCacheTemplate;

import java.util.Objects;

/**
 * 验证码缓存服务
 *
 * @author Yun Jiao
 */
@Service
public class CaptchaCacheTemplate extends AbstractMultiLevelCacheTemplate<String, String> {
    public CaptchaCacheTemplate() {
        super("captcha");
    }

    @Override
    public Class<String> getValueClass() {
        return String.class;
    }

    @Override
    public String valueGenerator(String key) {
        return RandomStringUtils.secure().nextAlphanumeric(4);
    }

    public boolean check(String key, String inputValue) {
        String cacheValue = get(key);
        return Objects.equals(cacheValue, inputValue);
    }
}
