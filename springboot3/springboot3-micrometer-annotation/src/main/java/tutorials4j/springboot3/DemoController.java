package tutorials4j.springboot3;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 示例接口
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class DemoController {
    private final DemoService demoService;

    @Timed(value = "demo.controller.timed",
            description = "Time taken to process timed request",
            percentiles = {0.5, 0.9, 0.95, 0.99})
    @GetMapping("/timed")
    public Object timed() {
        try {
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(500)) ;
        } catch (InterruptedException e) { log.error("异常", e);}
        return demoService.timed() ;
    }


    @Counted("demo.controller.counted")
    @GetMapping("/counted/{id}")
    public Object counted(@PathVariable("id") Long id) {
        try {
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(500)) ;
        } catch (InterruptedException e) { log.error("异常", e);}
        return demoService.counted(id) ;
    }
}
