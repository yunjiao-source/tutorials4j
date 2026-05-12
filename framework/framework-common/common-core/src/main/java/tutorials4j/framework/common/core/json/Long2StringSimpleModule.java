package tutorials4j.framework.common.core.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * {@code Long} 类型序列化为字符串 —— 避免前端 JavaScript 数值溢出问题
 *
 * @author Yun Jiao
 * @see JsonConsts
 */
public class Long2StringSimpleModule extends SimpleModule {
    public Long2StringSimpleModule() {
        super(Long2StringSimpleModule.class.getName(), JsonConsts.JSON_VERSION);
        // 前端js数值溢出问题
        this.addSerializer(Long.class, ToStringSerializer.instance);
        this.addSerializer(Long.TYPE, ToStringSerializer.instance);
    }


}
