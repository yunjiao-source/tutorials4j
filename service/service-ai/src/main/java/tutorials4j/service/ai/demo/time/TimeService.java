package tutorials4j.service.ai.demo.time;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
public class TimeService {
  public Response getTimeByZoneId(Request request) {
    ZoneId zid = ZoneId.of(request.zoneId);
    ZonedDateTime zonedDateTime = ZonedDateTime.now(zid);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    return new Response(zonedDateTime.format(formatter));
  }

  public record Request(
      @JsonProperty(required = true, value = "zoneId")
          @JsonPropertyDescription("时区，比如 Asia/Shanghai")
          String zoneId) {}

  public record Response(String time) {}
}
