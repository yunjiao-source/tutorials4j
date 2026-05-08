package tutorials4j.framework.examples;

import lombok.Getter;
import tutorials4j.framework.common.core.support.BaseEnum;

/**
 * 性别
 *
 * @author Yun Jiao
 */
@Getter
public enum SexEnum implements BaseEnum<String> {
    male("nan"), female("nv");

    private final String code;

    SexEnum(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name();
    }
}

