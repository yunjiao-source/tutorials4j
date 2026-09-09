package tutorials4j.framework.feature.satoken.web;

import cn.dev33.satoken.apikey.model.ApiKeyModel;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.feature.satoken.component.ApiKeyComponent;
import tutorials4j.framework.feature.satoken.model.ApiKeyCreateModel;
import tutorials4j.framework.feature.satoken.model.ApiKeyUpdateModel;
import tutorials4j.framework.feature.satoken.model.ValidGroup;
import tutorials4j.framework.oauth.satoken.common.SaTokenOauthConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sa-token/api-key")
public class ApiKeyController {
  private final ApiKeyComponent apiKeyComponent;

  @PostMapping("create")
  public Result<ApiKeyModel> create(
      @Validated(ValidGroup.AdminGroup.class) @RequestBody ApiKeyCreateModel model) {
    ApiKeyModel apiKeyModel = apiKeyComponent.create(model);

    return Result.success(apiKeyModel);
  }

  @PutMapping("update")
  public Result<ApiKeyModel> update(
      @Validated(ValidGroup.AdminGroup.class) @RequestBody ApiKeyUpdateModel model) {
    ApiKeyModel apiKeyModel = apiKeyComponent.update(model);
    return Result.success(apiKeyModel);
  }

  @DeleteMapping
  public Result<Void> delete(
      @RequestParam(name = "namespace", defaultValue = SaTokenOauthConsts.DEFAULT_NAMESPACE)
          String namespace,
      @RequestParam("loginId") String loginId,
      @RequestParam("apiKey") String apiKey) {
    apiKeyComponent.delete(namespace, loginId, apiKey);
    return Result.success();
  }

  @DeleteMapping("all")
  public Result<Void> delete(
      @RequestParam(name = "namespace", defaultValue = SaTokenOauthConsts.DEFAULT_NAMESPACE)
          String namespace,
      @RequestParam("loginId") String loginId) {
    apiKeyComponent.deleteAll(namespace, loginId);
    return Result.success();
  }
}
