package tutorials4j.framework.feature.oss.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tutorials4j.framework.data.hibernate.domain.BaseEntity;

/**
 * 文件详细信息实体，对应数据库表 {@code feat_file_detail}。
 *
 * <p>存储文件元数据、哈希信息、缩略图信息等，与x-file-storage的{@code FileInfo}对应。
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(name = "feat_file_detail")
@EqualsAndHashCode(callSuper = false)
public class FileDetailEntity extends BaseEntity {

  /** 文件访问地址（URL） */
  @Column(name = "url", length = 512, nullable = false)
  private String url;

  /** 文件大小（字节） */
  @Column(name = "size")
  private Long size;

  /** 存储时的文件名 */
  @Column(name = "filename", length = 256)
  private String filename;

  /** 原始上传文件名 */
  @Column(name = "original_filename", length = 256)
  private String originalFilename;

  /** 基础存储路径（如bucket下的目录） */
  @Column(name = "base_path", length = 256)
  private String basePath;

  /** 存储路径（相对basePath） */
  @Column(name = "path", length = 256)
  private String path;

  /** 文件扩展名（不含点） */
  @Column(name = "ext", length = 32)
  private String ext;

  /** MIME类型 */
  @Column(name = "content_type", length = 128)
  private String contentType;

  /** 存储平台标识（如aliyun、qiniu等） */
  @Column(name = "platform", length = 32)
  private String platform;

  /** 缩略图访问地址 */
  @Column(name = "th_url", length = 512)
  private String thUrl;

  /** 缩略图文件名 */
  @Column(name = "th_filename", length = 256)
  private String thFilename;

  /** 缩略图大小（字节） */
  @Column(name = "th_size")
  private Long thSize;

  /** 缩略图MIME类型 */
  @Column(name = "th_content_type", length = 128)
  private String thContentType;

  /** 文件所属对象ID（如用户ID、订单ID等） */
  @Column(name = "object_id", length = 32)
  private String objectId;

  /** 文件所属对象类型（如用户头像、评价图片等） */
  @Column(name = "object_type", length = 32)
  private String objectType;

  /** 文件元数据（JSON格式） */
  @Column(name = "metadata", columnDefinition = "text")
  private String metadata;

  /** 用户自定义元数据（JSON格式） */
  @Column(name = "user_metadata", columnDefinition = "text")
  private String userMetadata;

  /** 缩略图元数据（JSON格式） */
  @Column(name = "th_metadata", columnDefinition = "text")
  private String thMetadata;

  /** 缩略图用户自定义元数据（JSON格式） */
  @Column(name = "th_user_metadata", columnDefinition = "text")
  private String thUserMetadata;

  /** 附加属性（扩展字段，JSON格式） */
  @Column(name = "attr", columnDefinition = "text")
  private String attr;

  /** 文件访问控制列表（ACL） */
  @Column(name = "file_acl", length = 32)
  private String fileAcl;

  /** 缩略图ACL */
  @Column(name = "th_file_acl", length = 32)
  private String thFileAcl;

  /** 哈希信息（如MD5、SHA1等，JSON格式） */
  @Column(name = "hash_info", columnDefinition = "text")
  private String hashInfo;

  /** 上传ID（用于手动分片上传场景） */
  @Column(name = "upload_id", length = 128)
  private String uploadId;

  /** 上传状态：1-初始化完成，2-上传完成（手动分片上传使用） */
  @Column(name = "upload_status")
  private Integer uploadStatus;

  /** 创建时间 */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_time")
  private Date createTime;
}
