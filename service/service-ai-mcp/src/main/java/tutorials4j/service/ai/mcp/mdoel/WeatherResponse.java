package tutorials4j.service.ai.mcp.mdoel;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class WeatherResponse {
  @JsonPropertyDescription("城市")
  private String city;

  @JsonPropertyDescription("日期")
  private String date;

  @JsonPropertyDescription("区县")
  private String i;

  @JsonPropertyDescription("街道")
  private String s;

  @JsonPropertyDescription("描述")
  private String description;

  @JsonPropertyDescription("温度")
  private double temperature;

  public WeatherResponse(
      String city, String date, String i, String s, String description, double temperature) {
    this.city = city;
    this.date = date;
    this.i = i;
    this.s = s;
    this.description = description;
    this.temperature = temperature;
  }
}
