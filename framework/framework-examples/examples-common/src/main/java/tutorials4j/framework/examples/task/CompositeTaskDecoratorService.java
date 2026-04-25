package tutorials4j.framework.examples.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 服务
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class CompositeTaskDecoratorService {

    @Async
    public void exec() throws InterruptedException {
        log.info("async exec");
        TimeUnit.SECONDS.sleep(3);
    }
}
