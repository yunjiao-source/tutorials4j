package tutorials4j.framework.common.core;

import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * 自定义spEL表达式解析，一般用于切面编程
 *
 * @author Yun Jiao
 */
public class SpelExpressionResolver {

    private final SpelExpressionParser PARSER = new SpelExpressionParser();

    public <T> T evaluate(String expression, Method method, Object[] args, Class<T> desiredResultType) {
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 设置 #root 为方法调用上下文
        context.setRootObject(new MethodArgsHolder(args, method.getName()));

        // 支持 #p0, #p1 ... #a0, #a1
        for (int i = 0; i < args.length; i++) {
            context.setVariable("p" + i, args[i]);
            context.setVariable("a" + i, args[i]);
        }

        // 如果编译时保留了参数名，也可以设置 #paramName
        String[] paramNames = getParameterNames(method);
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        return PARSER.parseExpression(expression).getValue(context, desiredResultType);
    }

    // 获取参数名（需 -parameters 编译选项）
    private String[] getParameterNames(Method method) {
        return Arrays.stream(method.getParameters())
                .map(Parameter::getName)
                .toArray(String[]::new);
    }

    // 辅助类，用于支持 #root.args[0]
    public record MethodArgsHolder(Object[] args, String methodName) {

    }
}
