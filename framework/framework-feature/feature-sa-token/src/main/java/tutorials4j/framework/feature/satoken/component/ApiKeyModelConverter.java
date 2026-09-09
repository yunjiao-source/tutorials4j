package tutorials4j.framework.feature.satoken.component;

import cn.dev33.satoken.apikey.model.ApiKeyModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import tutorials4j.framework.common.core.util.TimeUtil;
import tutorials4j.framework.feature.satoken.model.ApiKeyEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface ApiKeyModelConverter {
  static ApiKeyModel convert(ApiKeyModel model, ApiKeyEntity entity) {
    BeanUtils.copyProperties(entity, model);
    if (!CollectionUtils.sizeIsEmpty(entity.getExtraData())) {
      entity.getExtraData().forEach(model::addExtra);
    }
    if (entity.getCreatedDate() != null) {
      model.setCreateTime(entity.getCreatedDate().toEpochMilli());
    }
    model.setLoginId(entity.getLoginId());
    return model;
  }

  static ApiKeyEntity convert(ApiKeyEntity entity, ApiKeyModel model) {
    BeanUtils.copyProperties(model, entity);
    if (!CollectionUtils.sizeIsEmpty(model.getExtraData())) {
      model
          .getExtraData()
          .forEach(
              (k, v) -> {
                entity.getExtraData().put(k, v == null ? null : v.toString());
              });
    }
    if (model.getCreateTime() > 0) {
      entity.setCreatedDate(TimeUtil.toInstant(model.getCreateTime()));
    }
    if (model.getLoginId() != null) {
      entity.setLoginId(model.getLoginId().toString());
    }
    return entity;
  }
}
