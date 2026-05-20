package tutorials4j.framework.captcha.tianai.service;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.common.model.dto.MatchParam;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.StringUtils;
import tutorials4j.framework.captcha.CaptchaService;
import tutorials4j.framework.captcha.exception.CaptchaException;
import tutorials4j.framework.captcha.tianai.TianAiCaptchaGenerateParamBuilder;
import tutorials4j.framework.common.core.util.GsonUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public abstract class AbstractCaptchaService implements CaptchaService {
  protected final ImageCaptchaApplication imageCaptchaApplication;
  protected final TianAiCaptchaGenerateParamBuilder builder;

  protected AbstractCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, TianAiCaptchaGenerateParamBuilder builder) {
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
