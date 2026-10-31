package tutorials4j.feature.basic.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.feature.basic.entity.ConfigEntity;
import tutorials4j.feature.basic.model.ConfigCreateDTO;
import tutorials4j.feature.basic.model.ConfigUpdateDTO;
import tutorials4j.feature.basic.model.ConfigVO;
import tutorials4j.feature.basic.service.ConfigQuery;
import tutorials4j.feature.basic.service.ConfigService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/basic/configs")
@RequiredArgsConstructor
public class ConfigBasicController {
  private final ConfigService configService;

  @PostMapping("create")
  @ResponseStatus(HttpStatus.CREATED)
  public ConfigVO create(@Valid @RequestBody ConfigCreateDTO dto) {
    ConfigEntity entity = configService.create(dto);
    return ConfigVO.of(entity);
  }

  @PutMapping("{config-id}")
  public ConfigVO update(
      @PathVariable("config-id") String id, @Valid @RequestBody ConfigUpdateDTO dto) {
    ConfigEntity entity = configService.update(id, dto);
    return ConfigVO.of(entity);
  }

  @DeleteMapping("{config-id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("config-id") String id) {
    configService.delete(id);
  }

  @GetMapping("{config-id}")
  public ConfigVO getById(@PathVariable("config-id") String id) {
    ConfigEntity entity = configService.findById(id);
    return ConfigVO.of(entity);
  }

  @GetMapping("/by-code/{config-code}")
  public ConfigVO getByCode(@PathVariable("config-code") String code) {
    return configService.getConfigRepository().findByCode(code).map(ConfigVO::of).orElse(null);
  }

  @GetMapping("/by-category/{config-category}")
  public List<ConfigVO> getValidByCategory(@PathVariable("config-category") String category) {
    return configService.findValidByCategory(category).stream().map(ConfigVO::of).toList();
  }

  @GetMapping("page")
  public PagedModel<ConfigVO> getByPage(ConfigQuery query, Pageable pageable) {
    Page<ConfigEntity> entities = configService.findByPage(query, pageable);
    return new PagedModel<>(entities.map(ConfigVO::of));
  }
}
