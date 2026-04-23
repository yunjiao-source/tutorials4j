package tutorials4j.framework.common.core.condition;

import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;

import java.util.List;

/**
 * {@link ConditionalOnListProperty} 的具体条件匹配逻辑实现。
 * <p>尝试将指定配置键绑定为 {@link List} 类型，并根据注解的 {@code isEmpty} 和 {@code matchIfMissing}
 * 属性决定条件是否匹配。</p>
 *
 * @author Yun Jiao
 * @see ConditionalOnListProperty
 */
public class OnCollectionListCondition extends AbstractOnCollectionCondition {

    /**
     * 实现抽象方法：根据 fullKey 绑定 List 并做出决策。
     *
     * @param fullKey    完整的配置键
     * @param context    条件上下文
     * @param attributes 注解属性（包含 isEmpty, matchIfMissing）
     * @return 决策记录，包含是否缺失、绑定的 List 是否为空、条件是否匹配
     */
    @Override
    protected Decision makeDecision(String fullKey, ConditionContext context, AnnotationAttributes attributes) {
        boolean isEmpty = attributes.getBoolean("isEmpty");
        boolean matchIfMissing = attributes.getBoolean("matchIfMissing");

        // 尝试将 fullKey 下的所有属性绑定为 List
        BindResult<List<Object>> bindResult = Binder.get(context.getEnvironment()).bind(fullKey, Bindable.listOf(Object.class));

        boolean conditionMatches;
        boolean isBoundEmpty = false;
        boolean notFound = !bindResult.isBound();
        if (notFound) {
            // 配置缺失
            conditionMatches = matchIfMissing;
        } else {
            List<Object> list = bindResult.get();
            isBoundEmpty = (list == null || list.isEmpty());
            conditionMatches = (isEmpty == isBoundEmpty);
        }

        return new Decision(notFound, isBoundEmpty, conditionMatches);
    }

    /**
     * 返回该条件对应的注解类。
     *
     * @return {@link ConditionalOnListProperty} 的 Class 对象
     */
    @Override
    protected Class<?> getAnnotationClass() {
        return ConditionalOnListProperty.class;
    }
}
