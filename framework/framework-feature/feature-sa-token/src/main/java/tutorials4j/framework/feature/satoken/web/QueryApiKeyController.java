package tutorials4j.framework.feature.satoken.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.feature.satoken.model.ApiKeyEntity;
import tutorials4j.framework.feature.satoken.model.ApiKeyQuery;
import tutorials4j.framework.feature.satoken.service.ApiKeyService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sa-token/api-key-query")
public class QueryApiKeyController {
  private final ApiKeyService apiKeyService;

  @GetMapping("page")
  public Result<PagedModel<ApiKeyEntity>> queryByPage(ApiKeyQuery query, Pageable pageable) {
    Page<ApiKeyEntity> page = apiKeyService.findByPage(query, pageable);
    return Result.success(new PagedModel<>(page));
  }

  @GetMapping("{id}")
  public Result<ApiKeyEntity> queryById(@PathVariable("id") String id) {
    ApiKeyEntity entity = apiKeyService.findById(id);
    return Result.success(entity);
  }
}
