package tutorials4j.framework.feature.oss.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tutorials4j.framework.data.hibernate.domain.BaseIdEntity;

/**
 * 文件分片详情实体，对应表 {@code feat_file_part_detail}。
 *
 * <p>用于记录手动分片上传时每个分片的信息，包括分片编号、ETag、大小等。
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(name = "feat_file_part_detail")
@EqualsAndHashCode(callSuper = false)
public class FilePartDetailEntity extends BaseIdEntity {

  /** 存储平台 */
  @Column(name = "platform", length = 32)
  private String platform;

  /** 关联的上传ID（与{@link FileDetailEntity#uploadId}对应） */
  @Column(name = "upload_id", length = 128)
  private String uploadId;

  /** 分片的ETag，用于校验 */
  @Column(name = "e_tag", length = 255)
  private String eTag;

  /** 分片编号（通常1~10000） */
  @Column(name = "part_number")
  private Integer partNumber;

  /** 分片大小（字节） */
  @Column(name = "part_size")
  private Long partSize;

  /** 分片的哈希信息（JSON格式） */
  @Column(name = "hash_info", columnDefinition = "text")
  private String hashInfo;

  /** 创建时间 */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_time")
  private Date createTime;
}
