package tutorials4j.framework.common.core.condition;

import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;

import java.util.Map;

/**
 * {@link ConditionalOnMapProperty} 处理逻辑
 *
 * @author Yun Jiao
 */
public class OnCollectionMapCondition extends AbstractOnCollectionCollecitonCondition {


    @Override
    protected Decision makeDecision(String fullKey, ConditionContext context, AnnotationAttributes attributes) {
        boolean isEmpty = attributes.getBoolean("isEmpty");
        boolean matchIfMissing = attributes.getBoolean("matchIfMissing");

        // 尝试将 fullKey 下的所有属性绑定为 Map
        BindResult<Map<String, Object>> bindResult = Binder.get(context.getEnvironment()).bind(fullKey, Bindable.mapOf(String.class, Object.class));

        boolean conditionMatches;
        boolean isBoundEmpty = false;
        boolean notFound = !bindResult.isBound();
        if (notFound) {
            // 配置缺失
            conditionMatches = matchIfMissing;
        } else {
            Map<String, Object> map = bindResult.get();
            isBoundEmpty = (map == null || map.isEmpty());
            conditionMatches = (isEmpty == isBoundEmpty);
        }

        return new Decision(notFound, isBoundEmpty, conditionMatches);
    }

    @Override
    protected Class<?> getAnnotationClass() {
        return ConditionalOnMapProperty.class;
    }
}
