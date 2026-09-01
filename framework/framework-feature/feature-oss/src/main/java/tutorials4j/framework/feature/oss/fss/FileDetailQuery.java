package tutorials4j.framework.feature.oss.fss;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

/**
 * 文件详情查询参数对象，用于构建动态查询条件。
 *
 * <p>包含文件名、平台、对象ID/类型、创建时间范围等过滤条件，通过{@link #buildSpecification()}生成JPA Specification。
 *
 * @author Yun Jiao
 */
@Data
public class FileDetailQuery {

  private String filename;
  private String platform;
  private String objectId;
  private String objectType;
  private Date createTimeBegin;
  private Date createTimeEnd;

  /**
   * 根据当前查询参数构建JPA Specification。
   *
   * <p>将非空条件组合为AND关系，若所有条件均为空则返回恒真条件。
   *
   * @return Specification对象
   */
  public Specification<FileDetailEntity> buildSpecification() {
    List<Specification<FileDetailEntity>> specList = new ArrayList<>();
    specList.add(FileDetailSpecification.filenameLike(filename));
    specList.add(FileDetailSpecification.platformEqual(platform));
    specList.add(FileDetailSpecification.objectIdEqual(objectId));
    specList.add(FileDetailSpecification.objectTypeEqual(objectType));
    specList.add(FileDetailSpecification.createTimeBegin(createTimeBegin));
    specList.add(FileDetailSpecification.createTimeEnd(createTimeEnd));

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
