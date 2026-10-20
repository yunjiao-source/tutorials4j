package tutorials4j.toolkit.data.domain;

import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@ToString(callSuper = true)
public class BaseVO<ID extends Serializable> extends BaseIdVO<ID> {
  private Instant createDate;
  private String createBy;

  private Instant lastModifiedDate;
  private String lastModifiedBy;
}
