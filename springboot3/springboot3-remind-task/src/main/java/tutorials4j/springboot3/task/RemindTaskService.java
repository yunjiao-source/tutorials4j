package tutorials4j.springboot3.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 服务
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RemindTaskService {
  private final RemindTaskRepository remindTaskRepository;

  public void saveException(RemindTask task, Throwable t) {
    // 记录异常
    log.info("保存任务异常");
  }
}
