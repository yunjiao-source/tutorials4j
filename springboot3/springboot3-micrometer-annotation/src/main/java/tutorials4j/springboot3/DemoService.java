package tutorials4j.springboot3;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 示例服务
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class DemoService {

    @Timed(value = "demo.service.timed",
            description = "示例服务执行耗时")
    public String timed() {
        try {
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(500)) ;
        } catch (InterruptedException e) { log.error("异常", e);}
        return "timed" ;
    }

    @Counted(value = "demo.service.counted",
            description = "示例服务执行次数")
    public String counted(Long id) {
        if (id % 2 == 0) {
            throw new RuntimeException("参数错误") ;
        }
        return id.toString() ;
    }
}
