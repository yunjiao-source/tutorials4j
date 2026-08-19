package tutorials4j.framework.examples;

import java.util.Date;

/**
 * 汽车信息记录。
 *
 * <p>以 record 形式定义的汽车领域对象，承载唯一标识、名称与相关日期等基础属性。
 *
 * @author Yun Jiao
 */
public record Car(Long id, String Name, Date date) {}
