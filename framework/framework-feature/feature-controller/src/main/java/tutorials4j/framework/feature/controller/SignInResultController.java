package tutorials4j.framework.feature.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.feature.domain.signin.SignInResultEntity;
import tutorials4j.framework.feature.domain.signin.SignInResultQuery;
import tutorials4j.framework.feature.domain.signin.SignInResultService;
import tutorials4j.framework.feature.domain.signin.SignInResultVO;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/sign-in-result")
@RequiredArgsConstructor
public class SignInResultController {
  private final SignInResultService signInResultService;

  @GetMapping
  public PagedModel<SignInResultVO> find(SignInResultQuery query, Pageable pageable) {
    Page<SignInResultEntity> page = signInResultService.find(query, pageable);
    return new PagedModel<>(page.map(SignInResultVO::of));
  }
}
