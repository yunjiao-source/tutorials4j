package tutorials4j.framework.feature.signin.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.feature.signin.domain.SignInResultEntity;
import tutorials4j.framework.feature.signin.domain.SignInResultQuery;
import tutorials4j.framework.feature.signin.domain.SignInResultService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/sign-in-result")
@RequiredArgsConstructor
public class SignInResultEndpoint {
  private final SignInResultService signInResultService;

  @GetMapping
  public PagedModel<SignInResultVO> find(SignInResultQuery query, Pageable pageable) {
    Page<SignInResultEntity> page = signInResultService.find(query, pageable);
    return new PagedModel<>(page.map(SignInResultVO::of));
  }
}
