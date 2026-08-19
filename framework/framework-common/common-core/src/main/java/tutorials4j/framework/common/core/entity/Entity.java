package tutorials4j.framework.common.core.entity;

import java.io.Serializable;

/**
 * 实体标记接口，所有实体（包括 DTO、POJO 等）的基础标识。
 *
 * <p>继承自 {@link Serializable}，实现该接口的实体默认支持 Java 序列化。
 *
 * @author Yun Jiao
 */
public interface Entity extends Serializable {}
