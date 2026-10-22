package tutorials4j.feature.basic.model;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class DictDataDTO {
  private String parentId;

  @NotBlank(message = "名称是必须的")
  private String name;

  @NotBlank(message = "代码是必须的")
  private String code;

  private String description;
  private Integer rank;

  @NotBlank(message = "字典类型是必须的")
  private String dictTypeId;

  private Map<String, String> jsonData;
}
