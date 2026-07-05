package tutorials4j.framework.examples.message.redis.zset;

import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequestMapping("/task")
@RestController
@RequiredArgsConstructor
public class TaskController {
  private final TaskService taskService;

  @GetMapping("add")
  public String send() {
    IntStream.range(0, 5).forEach((i) -> taskService.addTask());
    return "ok";
  }
}
