package tutorials4j.framework.common.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link  SpelExpressionResolver} 单元测试。注意需要开启<arg>-parameters</arg>，否则测试失败
 *
 * @author Yun Jiao
 */
class SpelExpressionResolverTest {

    private SpelExpressionResolver resolver;
    private Method testMethod;   // 用于测试的方法对象
    private Object[] testArgs;   // 示例参数

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        resolver = new SpelExpressionResolver();
        // 选择一个带参数名的测试方法（需编译时开启 -parameters）
        testMethod = SampleService.class.getMethod("sampleMethod", String.class, Integer.class);
        testArgs = new Object[]{"Hello", 42};
    }

    // ---------- 基本算术表达式 ----------
    @Test
    void testSimpleArithmeticExpression() throws Exception {
        String expression = "1 + 2 * 3";
        Integer result = resolver.evaluate(expression, testMethod, testArgs, Integer.class);
        assertEquals(7, result);
    }

    // ---------- 引用参数 #p0 / #a0 ----------
    @Test
    void testParameterByPIndex() throws Exception {
        String expression = "#p0 + ' World'";
        String result = resolver.evaluate(expression, testMethod, testArgs, String.class);
        assertEquals("Hello World", result);
    }

    @Test
    void testParameterByAIndex() throws Exception {
        String expression = "#a1 + 100";
        Integer result = resolver.evaluate(expression, testMethod, testArgs, Integer.class);
        assertEquals(142, result);
    }

    // ---------- 引用参数名（需 -parameters 编译选项） ----------
    @Test
    void testParameterByName() throws Exception {
        // 假设 sampleMethod 的参数名为 str 和 num
        String expression = "#str + ' ' + #num";
        String result = resolver.evaluate(expression, testMethod, testArgs, String.class);
        assertEquals("Hello 42", result);
    }

    // ---------- 访问 #root 对象 ----------
    @Test
    void testRootObjectArgsAccess() throws Exception {
        // 访问 root.args[0]
        String expression = "#root.args[0] + ' Root'";
        String result = resolver.evaluate(expression, testMethod, testArgs, String.class);
        assertEquals("Hello Root", result);
    }

    @Test
    void testRootObjectMethodName() throws Exception {
        String expression = "#root.methodName";
        String result = resolver.evaluate(expression, testMethod, testArgs, String.class);
        assertEquals("sampleMethod", result);
    }

    // ---------- 混合使用变量 ----------
    @Test
    void testMixedVariables() throws Exception {
        String expression = "#p0.length() + #num";
        Integer result = resolver.evaluate(expression, testMethod, testArgs, Integer.class);
        assertEquals(5 + 42, result);
    }

    // ---------- 类型转换 ----------
    @Test
    void testTypeConversionToString() throws Exception {
        String expression = "#a1";
        String result = resolver.evaluate(expression, testMethod, testArgs, String.class);
        assertEquals("42", result);
    }

    // ---------- 无效表达式异常 ----------
    @Test
    void testInvalidExpressionThrowsException() {
        String invalidExpression = "1 + unknownVar";
        assertThrows(Exception.class, () ->
                resolver.evaluate(invalidExpression, testMethod, testArgs, Object.class)
        );
    }

    // ---------- 空参数列表 ----------
    @Test
    void testEmptyArgs() throws Exception {
        Method emptyArgsMethod = SampleService.class.getMethod("emptyArgsMethod");
        Object[] emptyArgs = new Object[0];
        String expression = "'constant'";
        String result = resolver.evaluate(expression, emptyArgsMethod, emptyArgs, String.class);
        assertEquals("constant", result);
    }

    // ---------- 辅助内部类：被测试的方法声明 ----------
    static class SampleService {
        // 方法名和参数名会被测试代码引用
        @SuppressWarnings("unused")
        public void sampleMethod(String str, Integer num) {
            // 仅用于反射获取 Method 对象
        }

        @SuppressWarnings("unused")
        public void emptyArgsMethod() {
        }
    }
}