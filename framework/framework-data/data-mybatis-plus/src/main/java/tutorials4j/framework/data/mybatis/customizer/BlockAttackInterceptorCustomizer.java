package tutorials4j.framework.data.mybatis.customizer;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import tutorials4j.framework.data.mybatis.MybatisPlusConsts;

/**
 * 防止全表删除/更新攻击的拦截器自定义器。
 *
 * <p>向 MyBatis Plus 拦截器中添加 {@link BlockAttackInnerInterceptor}， 用于拦截没有 WHERE 条件的 UPDATE 或 DELETE
 * 语句，防止误操作。
 *
 * @author Yun Jiao
 * @see BlockAttackInnerInterceptor
 */
public class BlockAttackInterceptorCustomizer implements MybatisPlusInterceptorCustomizer {

  /**
   * 向拦截器中添加防全表更新/删除拦截器。
   *
   * @param interceptor MyBatis Plus 拦截器实例
   */
  @Override
  public void custom(MybatisPlusInterceptor interceptor) {
    interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
  }

  /**
   * 返回防全表更新/删除拦截器的执行顺序。
   *
   * @return 拦截器顺序值
   */
  @Override
  public int getOrder() {
    return MybatisPlusConsts.INTERCEPTOR_ORDER_BLOCK_ATTACK;
  }
}
