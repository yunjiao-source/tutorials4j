package tutorials4j.framework.web.mvc.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.media.Schema;
import tutorials4j.framework.common.core.validation.LocalDateTimeFormat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class DefaultValidationModelResolver extends ModelResolver {

    private final Set<Class<? extends Annotation>> HANDLED_VALIDATIONS = Set.of(
            jakarta.validation.constraints.NotNull.class,
            jakarta.validation.constraints.NotBlank.class,
            jakarta.validation.constraints.NotEmpty.class,
            jakarta.validation.constraints.Min.class,
            jakarta.validation.constraints.Max.class,
            jakarta.validation.constraints.DecimalMin.class,
            jakarta.validation.constraints.DecimalMax.class,
            jakarta.validation.constraints.Pattern.class,
            jakarta.validation.constraints.Size.class
    );


    private static final Set<String> ALLOWED_PACKAGES = Set.of(
            "jakarta.validation.constraints",          // 只对“未处理”的该包注解添加扩展（谨慎）
            "org.hibernate.validator.constraints",
            LocalDateTimeFormat.class.getPackage().getName()
    );

    public DefaultValidationModelResolver(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    protected boolean applyBeanValidatorAnnotations(Schema property, Annotation[] annotations,
                                                    Schema parent, boolean applyNotNullAnnotations) {
        boolean result = super.applyBeanValidatorAnnotations(property, annotations, parent, applyNotNullAnnotations);
        if (annotations == null) return result;

        for (Annotation annotation : annotations) {
            Class<? extends Annotation> type = annotation.annotationType();
            if (HANDLED_VALIDATIONS.contains(type)) continue;

            String pkgName = type.getPackage().getName();
            if (!ALLOWED_PACKAGES.contains(pkgName)) continue;

            String extKey = "x-validation-" + type.getSimpleName();
            Map<String, Object> extensions = property.getExtensions();
            if (extensions != null && extensions.containsKey(extKey)) continue;

            Object value = extractAnnotationAttributes(annotation);
            property.addExtension(extKey, value);
        }
        return result;
    }

    private Object extractAnnotationAttributes(Annotation annotation) {
        // 其他注解：返回属性 Map
        Map<String, Object> attrs = new LinkedHashMap<>();
        for (Method m : annotation.annotationType().getDeclaredMethods()) {
            try {
                attrs.put(m.getName(), m.invoke(annotation));
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return attrs.isEmpty() ? Boolean.TRUE : attrs;
    }
}
