package tutorials4j.framework.captcha.support;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.RandomUtil;
import java.util.function.Function;

/**
 * 验证码生成类型。实现{@link Function}接口，创建验证码生成器
 *
 * @author Yun Jiao
 */
public enum CodeGeneratorType implements Function<Integer, CodeGenerator> {
  /** 算术验证码，如 2 + 3 = ？ */
  math {
    /** 创建指定长度的算术验证码生成器。 */
    @Override
    public CodeGenerator apply(Integer length) {
      return new MathGenerator(length);
    }
  },

  /** 数字+大小写字母 验证码 */
  numAndChar {
    /** 创建指定长度的数字与大小写字母验证码生成器。 */
    @Override
    public CodeGenerator apply(Integer length) {
      return new RandomGenerator(length);
    }
  },

  /** 数字+大写字母 验证码 */
  numAndUpperChar {
    /** 创建指定长度的数字与大写字母验证码生成器。 */
    @Override
    public CodeGenerator apply(Integer length) {
      return new RandomGenerator(
          RandomUtil.BASE_NUMBER + RandomUtil.BASE_CHAR.toUpperCase(), length);
    }
  },

  /** 数字+小写字母 验证码 */
  numAndLowerChar {
    /** 创建指定长度的数字与小写字母验证码生成器。 */
    @Override
    public CodeGenerator apply(Integer length) {
      return new RandomGenerator(RandomUtil.BASE_CHAR_NUMBER_LOWER, length);
    }
  },

  /** 数字 验证码 */
  num {
    /** 创建指定长度的纯数字验证码生成器。 */
    @Override
    public CodeGenerator apply(Integer length) {
      return new RandomGenerator(RandomUtil.BASE_NUMBER, length);
    }
  },

  /** 大写字母 验证码 */
  upperChar {
    /** 创建指定长度的纯大写字母验证码生成器。 */
    @Override
    public CodeGenerator apply(Integer length) {
      return new RandomGenerator(RandomUtil.BASE_CHAR.toUpperCase(), length);
    }
  },

  /** 小写字母 验证码 */
  lowerChar {
    /** 创建指定长度的纯小写字母验证码生成器。 */
    @Override
    public CodeGenerator apply(Integer length) {
      return new RandomGenerator(RandomUtil.BASE_CHAR, length);
    }
  },

  /** 大小写字母 验证码 */
  upperAndLowerChar {
    /** 创建指定长度的大小写字母验证码生成器。 */
    @Override
    public CodeGenerator apply(Integer length) {
      return new RandomGenerator(RandomUtil.BASE_CHAR + RandomUtil.BASE_CHAR.toUpperCase(), length);
    }
  }
}
