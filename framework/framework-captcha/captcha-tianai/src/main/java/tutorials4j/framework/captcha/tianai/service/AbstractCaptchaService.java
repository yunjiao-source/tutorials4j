package tutorials4j.framework.captcha.tianai.service;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.common.model.dto.MatchParam;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.StringUtils;
import tutorials4j.framework.captcha.exception.CaptchaException;
import tutorials4j.framework.captcha.support.CaptchaService;
import tutorials4j.framework.captcha.tianai.support.CaptchaGenerateParamBuilder;
import tutorials4j.framework.common.core.util.GsonUtils;

/**
 * 天意验证码服务抽象基类，提供生成和校验的公共逻辑。
 *
 * @author Yun Jiao
 */
public abstract class AbstractCaptchaService implements CaptchaService {
  /** 图片验证码应用实例 */
  protected final ImageCaptchaApplication imageCaptchaApplication;

  /** 生成参数构建器 */
  protected final CaptchaGenerateParamBuilder builder;

  /**
   * 构造抽象验证码服务。
   *
   * @param imageCaptchaApplication 图片验证码应用
   * @param builder 生成参数构建器
   */
  protected AbstractCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, CaptchaGenerateParamBuilder builder) {
    this.imageCaptchaApplication = imageCaptchaApplication;
    this.builder = builder;
  }

  @Override
  public Map<String, Object> draw() {
    ApiResponse<ImageCaptchaVO> res =
        imageCaptchaApplication.generateCaptcha(builder.createGenerateParam());
    if (!res.isSuccess()) {
      throw new RuntimeException("aaa");
    }

    ImageCaptchaVO captcha = res.getData();
    Map<String, Object> data = new HashMap<>();
    data.put("id", captcha.getId());
    data.put("type", captcha.getType());
    data.put("category", getCategory());
    data.put("backgroundImage", captcha.getBackgroundImage());
    data.put("templateImage", captcha.getTemplateImage());
    data.put("backgroundImageTag", captcha.getBackgroundImageTag());
    data.put("templateImageTag", captcha.getTemplateImageTag());
    data.put("backgroundImageWidth", captcha.getBackgroundImageWidth());
    data.put("backgroundImageHeight", captcha.getBackgroundImageHeight());
    data.put("templateImageWidth", captcha.getTemplateImageWidth());
    data.put("templateImageHeight", captcha.getTemplateImageHeight());
    data.put("data", captcha.getData());
    return data;
  }

  @Override
  public boolean verify(String key, String userCode) {
    if (!StringUtils.hasText(userCode)) {
      throw new CaptchaException();
    }

    ImageCaptchaTrack imageCaptchaTrack = GsonUtils.toObject(userCode, ImageCaptchaTrack.class);
    ApiResponse<?> valid = imageCaptchaApplication.matching(key, new MatchParam(imageCaptchaTrack));
    return valid.isSuccess();
  }
}
