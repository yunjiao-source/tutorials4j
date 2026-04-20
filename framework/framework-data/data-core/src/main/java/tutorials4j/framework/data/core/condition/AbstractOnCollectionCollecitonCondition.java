package tutorials4j.framework.data.core.condition;

import lombok.Builder;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * 集合处理逻辑
 *
 * @author Yun Jiao
 */
public abstract class AbstractOnCollectionCollecitonCondition extends SpringBootCondition {

    protected abstract Decision makeDecision(String fullKey, ConditionContext context, AnnotationAttributes attributes);

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(ConditionalOnListProperty.class.getName()));
        if (attributes == null) {
            return ConditionOutcome.noMatch("@ConditionalOnMapProperty is not present");
        }

        String prefix = attributes.getString("prefix");
        String name = attributes.getString("name");
        String value = attributes.getString("value");
        boolean isEmpty = attributes.getBoolean("isEmpty");
        boolean matchIfMissing = attributes.getBoolean("matchIfMissing");

        // value 作为 name 的别名
        if (StringUtils.hasText(value) && !StringUtils.hasText(name)) {
            name = value;
        }

        // 构建完整的配置键
        String fullKey = buildFullKey(prefix, name);
        if (!StringUtils.hasText(fullKey)) {
            return ConditionOutcome.noMatch("No prefix or name specified");
        }

        Decision decision = makeDecision(fullKey, context, attributes);

        String message = String.format(
                "Map property '%s' [%s], isEmpty=%s, matchIfMissing=%s -> %s",
                fullKey,
                !decision.notFound ? (decision.boundIsEmpty? "empty" : "not empty") : "missing",
                isEmpty, matchIfMissing, decision.conditionMatches
        );

        return new ConditionOutcome(decision.conditionMatches, message);
    }

    private String buildFullKey(String prefix, String name) {
        if (!StringUtils.hasText(prefix)) {
            return StringUtils.hasText(name) ? name : "";
        }
        if (!StringUtils.hasText(name)) {
            return prefix;
        }
        return prefix + "." + name;
    }

    @Builder
    public record Decision(boolean notFound, boolean boundIsEmpty, boolean conditionMatches) {}
}
