package tutorials4j.service.ai.tools;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class DateTimeTools {
  @Tool(description = "Get the current date and time in the user's timezone")
  String getCurrentDateTime() {
    String dataTime =
        LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    log.info("工具调用：{}", dataTime);
    return dataTime;
  }
}
