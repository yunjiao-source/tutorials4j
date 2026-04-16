package tutorials4j.springboot3.test;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试执行接口
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/test-exec")
@RequiredArgsConstructor
public class TestExecController {
    private final SimulationComponent simulationComponent;

    @GetMapping()
    public void test(@RequestParam("concurrentRequests") int concurrentRequests
            , @RequestParam("durationSeconds")  int durationSeconds) {
        simulationComponent.simulateHighLoad(concurrentRequests, durationSeconds);
    }
}
