package tutorials4j.framework.captcha.tianai.web;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.captcha.CaptchaCategory;
import tutorials4j.framework.captcha.CaptchaService;
import tutorials4j.framework.captcha.CaptchaServiceFactory;

/**
 * 天意验证码控制器，提供生成和校验验证码的 REST API。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("captcha/tianai")
@RequiredArgsConstructor
public class TianaiCaptchaController {
  private final CaptchaServiceFactory factory;
  private final ImageCaptchaApplication imageCaptchaApplication;

  /**
   * 生成验证码。
   *
   * @param request HTTP 请求
   * @param type 验证码类型，可选
   * @return 包含验证码图片及数据的响应
   */
  @RequestMapping("/gen")
  @ResponseBody
  public ApiResponse<Map<String, Object>> genCaptcha(
      HttpServletRequest request,
      @RequestParam(value = "type", required = false) CaptchaCategory type) {
    CaptchaService service = factory.findService(type);
    return ApiResponse.ofSuccess(service.draw());
  }

  /**
   * 校验验证码。
   *
   * @param data 请求体，包含验证码 id 和用户轨迹数据
   * @param request HTTP 请求
   * @return 校验结果响应
   */
  @PostMapping("/check")
  @ResponseBody
  public ApiResponse<?> checkCaptcha(@RequestBody Data data, HttpServletRequest request) {
    ApiResponse<?> response = imageCaptchaApplication.matching(data.getId(), data.getData());
    if (response.isSuccess()) {
      return ApiResponse.ofSuccess(Collections.singletonMap("id", data.getId()));
    }
    return response;
  }

  /** 验证码校验请求数据封装。 */
  @lombok.Data
  public static class Data {
    private String id;
    private ImageCaptchaTrack data;
  }
}
