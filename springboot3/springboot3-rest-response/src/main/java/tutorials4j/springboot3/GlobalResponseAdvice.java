package tutorials4j.springboot3;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应拦截器：自动将Controller返回值封装为Result格式
 *
 * @author Yun Jiao
 */
@ControllerAdvice // 作用于所有Controller
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {
    /**
     * 判断是否需要拦截：除了Result和PageResult类型，其他所有返回值都需要拦截封装
     * @param returnType 方法返回值类型
     * @param converterType 消息转换器类型
     * @return true=拦截，false=不拦截
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 排除Result和PageResult类型，避免重复封装
        Class<?> clazz = returnType.getParameterType();
        return !Result.class.isAssignableFrom(clazz) && !PageResult.class.isAssignableFrom(clazz);
    }
    /**
     * 拦截后的处理：将返回值封装为Result.success(data)
     * @param body 原始返回值（如User、List、String等）
     * @param returnType 方法返回值类型
     * @param selectedContentType 响应媒体类型
     * @param selectedConverterType 消息转换器类型
     * @param request 请求对象
     * @param response 响应对象
     * @return 封装后的Result对象
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 自动封装为成功响应，body为业务数据
        return Result.success(body);
    }

}

