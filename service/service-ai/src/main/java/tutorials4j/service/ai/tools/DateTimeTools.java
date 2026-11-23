package tutorials4j.service.ai.tools;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;
import tutorials4j.service.ai.util.Utils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class DateTimeTools {

  @Tool(description = "Get the current date and time in the user's timezone")
  String getCurrentDateTime() {
    ZonedDateTime dateTime =
        LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId());
    Utils.greenSystemOut("工具调用：" + dateTime);
    return dateTime.toString();
  }

  @Tool(description = "Set a user alarm for the given time, provided in ISO-8601 format")
  void setAlarm(String time) {
    LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
    Utils.greenSystemOut("Alarm set for：" + alarmTime);
    System.out.println("Alarm set for " + alarmTime);
  }
}
