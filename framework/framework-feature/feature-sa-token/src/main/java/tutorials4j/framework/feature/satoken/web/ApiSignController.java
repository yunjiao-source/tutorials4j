package tutorials4j.framework.feature.satoken.web;

import cn.hutool.core.util.DesensitizedUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.feature.satoken.component.ApiSignConfigComponent;
import tutorials4j.framework.feature.satoken.model.ApiSignEntity;
import tutorials4j.framework.feature.satoken.model.ApiSignModel;
import tutorials4j.framework.feature.satoken.model.ApiSignQuery;
import tutorials4j.framework.feature.satoken.service.ApiSignService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sa-token/api-sign")
public class ApiSignController {
  private final ApiSignConfigComponent apiSignConfigComponent;
  private final ApiSignService apiSignService;

  @PostMapping("create")
  public Result<ApiSignEntity> create(@RequestBody ApiSignModel model) {
    ApiSignEntity entity = apiSignService.create(model);
    apiSignConfigComponent.addConfig(entity);

    return Result.success(entity);
  }

  @PutMapping("{id}")
  public Result<ApiSignEntity> update(
      @PathVariable("id") String id, @RequestBody ApiSignModel model) {
    ApiSignEntity entity = apiSignService.update(id, model);
    apiSignConfigComponent.addConfig(entity);

    return Result.success(maskSecretKey(entity));
  }

  @DeleteMapping("{id}")
  public Result<Void> delete(@PathVariable("id") String id) {
    ApiSignEntity entity = apiSignService.delete(id);
    apiSignConfigComponent.delConfig(entity.getAppName());

    return Result.success();
  }

  @GetMapping("page")
  public Result<PagedModel<ApiSignEntity>> queryByPage(ApiSignQuery query, Pageable pageable) {
    Page<ApiSignEntity> page = apiSignService.findByPage(query, pageable);
    return Result.success(new PagedModel<>(page.map(this::maskSecretKey)));
  }

  private ApiSignEntity maskSecretKey(ApiSignEntity entity) {
    String secretKey = entity.getSecretKey();
    if (StringUtils.isNotBlank(secretKey)) {
      secretKey = DesensitizedUtil.idCardNum(secretKey, 4, 4);
    }
    entity.setSecretKey(secretKey);
    return entity;
  }
}
