package tutorials4j.framework.examples.uid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.uid.UidUtils;

/**
 * UID 生成示例控制器。
 *
 * <p>演示通过 {@link UidUtils} 的默认生成器与缓存生成器批量生成 UID，并以列表形式返回。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("uid")
public class UidController {
  /**
   * 使用默认生成器批量生成 UID。
   *
   * @param count 生成数量
   * @return 生成的 UID 列表
   */
  @GetMapping("default")
  public Result<List<Long>> def(@RequestParam("count") Integer count) {
    List<Long> ids = new ArrayList<>();
    IntStream.range(0, count).forEach(i -> ids.add(UidUtils.DEFAULTED.nextUid()));
    return Result.success(ids);
  }

  /**
   * 使用缓存生成器批量生成 UID。
   *
   * @param count 生成数量
   * @return 生成的 UID 列表
   */
  @GetMapping("cache")
  public Result<List<Long>> cache(@RequestParam("count") Integer count) {
    List<Long> ids = new ArrayList<>();
    IntStream.range(0, count).forEach(i -> ids.add(UidUtils.CACHED.nextUid()));
    return Result.success(ids);
  }
}
