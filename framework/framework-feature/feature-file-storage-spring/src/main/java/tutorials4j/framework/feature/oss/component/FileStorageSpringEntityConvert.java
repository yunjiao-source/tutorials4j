package tutorials4j.framework.feature.oss.component;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.hash.HashInfo;
import org.dromara.x.file.storage.core.upload.FilePartInfo;
import org.springframework.stereotype.Component;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.feature.oss.model.FileDetailEntity;
import tutorials4j.framework.feature.oss.model.FilePartDetailEntity;

/**
 * 文件存储实体与x-file-storage核心对象之间的转换器。
 *
 * <p>负责{@link FileInfo} ↔ {@link FileDetailEntity} 和 {@link FilePartInfo} ↔ {@link
 * FilePartDetailEntity} 的双向转换， 并将复杂对象（元数据、哈希信息等）与JSON字符串互相转换以便存储。
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class FileStorageSpringEntityConvert {

  private final JacksonRecord jacksonRecord;

  /**
   * 将{@link FileInfo}转换为{@link FileDetailEntity}。
   *
   * <p>除基础属性外，将元数据、用户元数据、缩略图元数据、附加属性、哈希信息序列化为JSON字符串。
   *
   * @param fileInfo 文件信息对象
   * @return 文件详情实体
   */
  public FileDetailEntity convert(FileInfo fileInfo) {
    FileDetailEntity entity =
        BeanUtil.copyProperties(
            fileInfo,
            FileDetailEntity.class,
            "metadata",
            "userMetadata",
            "thMetadata",
            "thUserMetadata",
            "attr",
            "hashInfo");

    entity.setMetadata(valueToJson(fileInfo.getMetadata()));
    entity.setUserMetadata(valueToJson(fileInfo.getUserMetadata()));
    entity.setThMetadata(valueToJson(fileInfo.getThMetadata()));
    entity.setThUserMetadata(valueToJson(fileInfo.getThUserMetadata()));
    entity.setAttr(valueToJson(fileInfo.getAttr()));
    entity.setHashInfo(valueToJson(fileInfo.getHashInfo()));
    return entity;
  }

  /**
   * 将{@link FileDetailEntity}转换为{@link FileInfo}。
   *
   * <p>从数据库JSON字符串反序列化为对应的复杂对象。
   *
   * @param entity 文件详情实体
   * @return 文件信息对象
   */
  public FileInfo convert(FileDetailEntity entity) {
    FileInfo info =
        BeanUtil.copyProperties(
            entity,
            FileInfo.class,
            "metadata",
            "userMetadata",
            "thMetadata",
            "thUserMetadata",
            "attr",
            "hashInfo");

    info.setMetadata(jsonToMetadata(entity.getMetadata()));
    info.setUserMetadata(jsonToMetadata(entity.getUserMetadata()));
    info.setThMetadata(jsonToMetadata(entity.getThMetadata()));
    info.setThUserMetadata(jsonToMetadata(entity.getThUserMetadata()));
    info.setAttr(jsonToDict(entity.getAttr()));
    info.setHashInfo(jsonToHashInfo(entity.getHashInfo()));
    return info;
  }

  /**
   * 将{@link FilePartInfo}转换为{@link FilePartDetailEntity}。
   *
   * @param filePartInfo 分片信息对象
   * @return 分片详情实体
   */
  public FilePartDetailEntity convert(FilePartInfo filePartInfo) {
    FilePartDetailEntity entity =
        BeanUtil.copyProperties(filePartInfo, FilePartDetailEntity.class, "hashInfo");
    entity.setHashInfo(valueToJson(filePartInfo.getHashInfo()));
    return entity;
  }

  /**
   * 将{@link FilePartDetailEntity}转换为{@link FilePartInfo}。
   *
   * @param entity 分片详情实体
   * @return 分片信息对象
   */
  public FilePartInfo convert(FilePartDetailEntity entity) {
    FilePartInfo info = BeanUtil.copyProperties(entity, FilePartInfo.class, "hashInfo");
    info.setHashInfo(jsonToHashInfo(entity.getHashInfo()));
    return info;
  }

  /** 将任意对象序列化为JSON字符串（若为null则返回null）。 */
  private String valueToJson(Object o) {
    if (o == null) {
      return null;
    }
    return jacksonRecord.toJson(o);
  }

  /** 将JSON字符串反序列化为{@code Map<String, String>}元数据。 */
  public Map<String, String> jsonToMetadata(String json) {
    if (StringUtils.isBlank(json)) return null;
    return jacksonRecord.toObject(json, new TypeReference<Map<String, String>>() {});
  }

  /** 将JSON字符串反序列化为{@link Dict}对象（用于附加属性）。 */
  public Dict jsonToDict(String json) {
    if (StringUtils.isBlank(json)) return null;
    return jacksonRecord.toObject(json, Dict.class);
  }

  /** 将JSON字符串反序列化为{@link HashInfo}对象。 */
  public HashInfo jsonToHashInfo(String json) {
    if (StringUtils.isBlank(json)) return null;
    return jacksonRecord.toObject(json, HashInfo.class);
  }
}
