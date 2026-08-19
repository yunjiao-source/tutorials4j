package tutorials4j.framework.feature.signin.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.bean.Result;

/**
 * 签到结果查询接口。
 *
 * <p>提供签到结果的分页查询能力。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/sign-in-result")
@RequiredArgsConstructor
public class SignInResultEndpoint {
  private final SignInResultService signInResultService;

  /**
   * 分页查询签到结果。
   *
   * @param query 查询条件
   * @param pageable 分页参数
   * @return 分页的签到结果视图对象
   */
  @GetMapping
  public Result<PagedModel<SignInResultVO>> find(SignInResultQuery query, Pageable pageable) {
    Page<SignInResultEntity> page = signInResultService.find(query, pageable);
    PagedModel<SignInResultVO> result = new PagedModel<>(page.map(SignInResultVO::of));
    return Result.success(result);
  }
}
