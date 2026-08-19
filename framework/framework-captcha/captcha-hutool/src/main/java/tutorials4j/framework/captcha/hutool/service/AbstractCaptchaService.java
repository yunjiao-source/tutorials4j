package tutorials4j.framework.captcha.hutool.service;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.IdUtil;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import tutorials4j.framework.captcha.exception.CaptchaErrorCode;
import tutorials4j.framework.captcha.hutool.bean.CaptchaData;
import tutorials4j.framework.captcha.support.BehaviorCaptchaCacheTemplate;
import tutorials4j.framework.captcha.support.CaptchaService;
import tutorials4j.framework.common.core.util.GaussianBlur;

/**
 * 抽象验证码服务，提供验证码生成、模糊处理、缓存存储及验证等公共逻辑。
 *
 * <p>子类需实现{@link #getValidIgnoreCase()}和{@link #getFuzziness()}以提供配置， 并实现{@link
 * CaptchaService#draw()}方法生成具体的验证码数据。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCaptchaService implements CaptchaService {
  /** 验证码缓存操作模板，用于存储与校验验证码 */
  protected final BehaviorCaptchaCacheTemplate captchaCacheTemplate;

  /**
   * 获取校验时是否忽略大小写的配置。
   *
   * @return 校验时是否忽略大小写
   */
  protected abstract Boolean getValidIgnoreCase();

  /**
   * 获取图片模糊度的配置。
   *
   * @return 模糊度
   */
  protected abstract Integer getFuzziness();

  /**
   * 模糊度处理
   *
   * @param image 必须值
   * @return 模糊后的图片
   */
  protected BufferedImage handleFuzziness(BufferedImage image) {
    Integer fuzziness = getFuzziness();
    if (fuzziness != null && fuzziness > 0) {
      return GaussianBlur.execute(image, fuzziness);
    }
    return image;
  }

  /**
   * 创建验证码信息
   *
   * @param code 码，必须值
   * @param image 必须值
   * @return 验证码信息
   */
  protected CaptchaData createCaptchaData(String code, BufferedImage image) {

    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      image = handleFuzziness(image);

      ImgUtil.writePng(image, out);
      byte[] imageBytes = out.toByteArray();

      String key = IdUtil.fastSimpleUUID();
      captchaCacheTemplate.put(key, code);
      return new CaptchaData().key(key).code(code).category(getCategory()).captchaImage(imageBytes);
    } catch (IOException e) {
      throw CaptchaErrorCode.CAPTCHA_GENERATE_FAILURE.throwed(e);
    }
  }

  /**
   * 校验用户输入的验证码是否正确，校验成功后立即删除缓存中的验证码（一次性使用）。
   *
   * @param key 验证码缓存 key
   * @param userCode 用户输入的验证码
   * @return 校验是否通过
   * @throws IllegalArgumentException key 或 userCode 为空时抛出
   */
  @Override
  public boolean verify(String key, String userCode) {
    Assert.hasText(key, "key must not be null or empty");
    Assert.hasText(userCode, "userCode must not be null or empty");

    String cacheCode = captchaCacheTemplate.get(key);
    if (!StringUtils.hasText(cacheCode)) {
      throw CaptchaErrorCode.CAPTCHA_HAS_EXPIRED.throwed().param("key", key);
    }

    // 删除缓存: 验证码key只使用一次
    captchaCacheTemplate.delete(key);

    boolean success = false;
    Boolean ignoreCase = getValidIgnoreCase();
    if (Boolean.TRUE.equals(ignoreCase)) {
      success = cacheCode.equalsIgnoreCase(userCode);
    } else {
      success = cacheCode.equals(userCode);
    }

    return success;
  }
}
