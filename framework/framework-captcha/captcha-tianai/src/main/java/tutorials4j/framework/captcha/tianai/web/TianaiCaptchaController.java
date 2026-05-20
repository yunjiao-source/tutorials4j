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
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("captcha/tianai")
@RequiredArgsConstructor
public class TianaiCaptchaController {
  private final CaptchaServiceFactory factory;
  private final ImageCaptchaApplication imageCaptchaApplication;

  @RequestMapping("/gen")
  @ResponseBody
  public ApiResponse<Map<String, Object>> genCaptcha(
      HttpServletRequest request,
      @RequestParam(value = "type", required = false) CaptchaCategory type) {
    CaptchaService service = factory.findService(type);
    return ApiResponse.ofSuccess(service.draw());
  }

  @PostMapping("/check")
  @ResponseBody
  public ApiResponse<?> checkCaptcha(@RequestBody Data data, HttpServletRequest request) {
    ApiResponse<?> response = imageCaptchaApplication.matching(data.getId(), data.getData());
    if (response.isSuccess()) {
      return ApiResponse.ofSuccess(Collections.singletonMap("id", data.getId()));
    }
    return response;
  }

  @lombok.Data
  public static class Data {
    private String id;
    private ImageCaptchaTrack data;
  }
}
