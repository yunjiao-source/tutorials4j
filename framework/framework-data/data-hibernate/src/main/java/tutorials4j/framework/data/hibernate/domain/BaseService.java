package tutorials4j.framework.data.hibernate.domain;

import java.io.Serializable;
import tutorials4j.framework.common.core.entity.Entity;

/**
 * 基础服务接口，组合了可写服务能力。
 *
 * <p>目前等价于 {@link WriteableService}，后续可扩展通用业务方法。业务服务可直接继承本接口获得完整的读写能力。
 *
 * @param <E> 实体类型
 * @param <ID> 主键类型
 * @author Yun Jiao
 */
public interface BaseService<E extends Entity, ID extends Serializable>
    extends WriteableService<E, ID> {}
