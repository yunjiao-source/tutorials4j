package tutorials4j.framework.data.core.condition;

import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;

import java.util.List;

/**
 * {@link ConditionalOnListProperty} 处理逻辑
 *
 * @author Yun Jiao
 */
public class OnCollectionListCondition extends AbstractOnCollectionCollecitonCondition {


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
}
