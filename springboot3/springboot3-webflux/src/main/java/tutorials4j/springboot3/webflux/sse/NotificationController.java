package tutorials4j.springboot3.webflux.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Spring MVC 传统方案
 *
 * @author yangyunjiao
 */
@RestController
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping("/notifications/{userId}")
  public SseEmitter subscribe(@PathVariable("userId") String userId) {
    return notificationService.subscribe(userId);
  }
}
