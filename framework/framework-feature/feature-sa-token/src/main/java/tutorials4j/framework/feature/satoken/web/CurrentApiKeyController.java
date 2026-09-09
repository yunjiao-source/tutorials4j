package tutorials4j.framework.feature.satoken.web;

import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.dev33.satoken.stp.StpUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/sa-token/api-key-current")
public class CurrentApiKeyController {
  private final ApiKeyComponent apiKeyComponent;

  @PostMapping("create")
  public Result<ApiKeyModel> create(
      @Validated(ValidGroup.UserGroup.class) @RequestBody ApiKeyCreateModel model) {
    model.setLoginId(StpUtil.getLoginId());
    ApiKeyModel apiKeyModel = apiKeyComponent.create(model);

    return Result.success(apiKeyModel);
  }

  @GetMapping("list")
  public Result<List<ApiKeyModel>> list(
      @RequestParam(name = "namespace", defaultValue = SaTokenOauthConsts.DEFAULT_NAMESPACE)
          String namespace) {
    List<ApiKeyModel> apiKeyModels = apiKeyComponent.list(namespace, StpUtil.getLoginId());
    return Result.success(apiKeyModels);
  }

  @PutMapping("update")
  public Result<ApiKeyModel> update(
      @Validated(ValidGroup.UserGroup.class) @RequestBody ApiKeyUpdateModel model) {
    model.setLoginId(StpUtil.getLoginId());
    ApiKeyModel apiKeyModel = apiKeyComponent.update(model);
    return Result.success(apiKeyModel);
  }

  @DeleteMapping
  public Result<Void> delete(
      @RequestParam(name = "namespace", defaultValue = SaTokenOauthConsts.DEFAULT_NAMESPACE)
          String namespace,
      @RequestParam("apiKey") String apiKey) {
    apiKeyComponent.delete(namespace, StpUtil.getLoginId(), apiKey);
    return Result.success();
  }

  @DeleteMapping("all")
  public Result<Void> delete(
      @RequestParam(name = "namespace", defaultValue = SaTokenOauthConsts.DEFAULT_NAMESPACE)
          String namespace) {
    apiKeyComponent.deleteAll(namespace, StpUtil.getLoginId());
    return Result.success();
  }
}
