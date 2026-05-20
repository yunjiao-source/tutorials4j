package tutorials4j.framework.captcha.hutool;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.IdUtil;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import tutorials4j.framework.captcha.BehaviorCaptchaCacheTemplate;
import tutorials4j.framework.captcha.CaptchaService;
import tutorials4j.framework.captcha.exception.CaptchaException;
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
  protected final BehaviorCaptchaCacheTemplate captchaCacheTemplate;

  /**
   * 获取参数
   *
   * @return 校验时是否忽略大小写
   */
  protected abstract Boolean getValidIgnoreCase();

  /**
   * 获取参数
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
      throw new CaptchaException("生成验证码图片异常", e);
    }
  }

  @Override
  public boolean verify(String key, String userCode) {
    if (!StringUtils.hasText(userCode)) {
      throw new CaptchaException();
    }

    String cacheCode = captchaCacheTemplate.get(key);
    if (!StringUtils.hasText(cacheCode)) {
      throw new CaptchaException();
    }

    boolean success = false;
    Boolean ignoreCase = getValidIgnoreCase();
    if (Boolean.TRUE.equals(ignoreCase)) {
      success = cacheCode.equalsIgnoreCase(userCode);
    } else {
      success = cacheCode.equals(userCode);
    }

    if (success) {
      // 删除缓存
      captchaCacheTemplate.delete(key);
    }
    return success;
  }
}
