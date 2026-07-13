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
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("uid")
public class UidController {
  @GetMapping("default")
  public Result<List<Long>> def(@RequestParam("count") Integer count) {
    List<Long> ids = new ArrayList<>();
    IntStream.range(0, count).forEach(i -> ids.add(UidUtils.DEFAULTED.nextUid()));
    return Result.success(ids);
  }

  @GetMapping("cache")
  public Result<List<Long>> cache(@RequestParam("count") Integer count) {
    List<Long> ids = new ArrayList<>();
    IntStream.range(0, count).forEach(i -> ids.add(UidUtils.CACHED.nextUid()));
    return Result.success(ids);
  }
}
