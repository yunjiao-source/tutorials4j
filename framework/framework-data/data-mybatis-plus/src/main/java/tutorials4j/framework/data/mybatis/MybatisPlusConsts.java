package tutorials4j.framework.data.mybatis;

/**
 * MyBatis Plus 框架内部常量定义。
 *
 * @author Yun Jiao
 */
public interface MybatisPlusConsts {
  /** 拦截器默认顺序值。 */
  int INTERCEPTOR_ORDER_DEFAULT = 100;

  /** 租户拦截器顺序值。 */
  int INTERCEPTOR_ORDER_TENANT = INTERCEPTOR_ORDER_DEFAULT + 10;

  /** 分页拦截器顺序值。 */
  int INTERCEPTOR_ORDER_PAGINATION = INTERCEPTOR_ORDER_TENANT + 10;

  /** 乐观锁拦截器顺序值。 */
  int INTERCEPTOR_ORDER_OPTIMISTIC_LOCKER = INTERCEPTOR_ORDER_PAGINATION + 10;

  /** 防全表更新/删除拦截器顺序值。 */
  int INTERCEPTOR_ORDER_BLOCK_ATTACK = INTERCEPTOR_ORDER_OPTIMISTIC_LOCKER + 10;
}
