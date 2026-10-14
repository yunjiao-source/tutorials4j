package tutorials4j.feature.oauth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.feature.oauth.entity.AuthHistoryEntity;
import tutorials4j.feature.oauth.service.AuthHistoryQuery;
import tutorials4j.feature.oauth.service.AuthHistoryService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/auth-histories")
@RequiredArgsConstructor
public class AuthHistoryController {
  private final AuthHistoryService authHistoryService;

  @GetMapping("page")
  public PagedModel<AuthHistoryEntity> getByPage(AuthHistoryQuery query, Pageable pageable) {
    var entities = authHistoryService.findByPage(query, pageable);
    return new PagedModel<>(entities);
  }
}
