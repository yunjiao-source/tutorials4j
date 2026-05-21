package tutorials4j.framework.web.mvc.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.media.Schema;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import tutorials4j.framework.web.core.validation.LocalDateTimeFormat;

/**
 * 自定义的 {@link ModelResolver}，扩展 Swagger 默认模型解析器， 为 Swagger 原生不处理的校验注解添加供应商扩展（{@code
 * x-validation-*}）。
 *
 * <p>该解析器在生成 Schema 时处理 Bean Validation 注解。 对于 Swagger 已原生处理的注解（如 {@code NotNull}、{@code
 * Min}、{@code Size} 等）， 会跳过扩展添加逻辑。对于其他属于允许包（Jakarta Validation、Hibernate Validator 以及 {@code
 * LocalDateTimeFormat} 所在包）的校验注解，该解析器会提取其属性值， 并将其作为扩展添加到对应的 Schema 属性上。
 *
 * <p>扩展键格式为 {@code x-validation-<注解简单名称>}，扩展值根据注解是否有属性而定：
 *
 * <ul>
 *   <li>有属性时：返回属性名到属性值的映射（{@link LinkedHashMap}）
 *   <li>无属性时：返回 {@code Boolean.TRUE}
 * </ul>
 *
 * @author Yun Jiao
 * @see ModelResolver
 * @see io.swagger.v3.oas.models.media.Schema#addExtension(String, Object)
 */
public class DefaultSwaggerModelResolver extends ModelResolver {

  /**
   * Swagger 原生 {@code applyBeanValidatorAnnotations} 逻辑已处理的校验注解类型集合。
   *
   * <p>这些注解在添加自定义扩展时将被跳过，以避免重复或与内置 Schema 属性冲突。
   */
  private final Set<Class<? extends Annotation>> HANDLED_VALIDATIONS =
      Set.of(
          jakarta.validation.constraints.NotNull.class,
          jakarta.validation.constraints.NotBlank.class,
          jakarta.validation.constraints.NotEmpty.class,
          jakarta.validation.constraints.Min.class,
          jakarta.validation.constraints.Max.class,
          jakarta.validation.constraints.DecimalMin.class,
          jakarta.validation.constraints.DecimalMax.class,
          jakarta.validation.constraints.Pattern.class,
          jakarta.validation.constraints.Size.class);

  /**
   * 允许为其添加自定义扩展的校验注解所在包名集合。
   *
   * <p>包含 Jakarta Validation 约束、Hibernate Validator 约束 以及自定义注解 {@code LocalDateTimeFormat} 所在的包。
   * 只有声明包位于此集合中的注解才会生成 {@code x-validation-*} 扩展。
   */
  private static final Set<String> ALLOWED_PACKAGES =
      Set.of(
          "jakarta.validation.constraints", // 只对“未处理”的该包注解添加扩展（谨慎）
          "org.hibernate.validator.constraints",
          LocalDateTimeFormat.class.getPackage().getName());

  public DefaultSwaggerModelResolver(ObjectMapper mapper) {
    super(mapper);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected boolean applyBeanValidatorAnnotations(
      Schema property, Annotation[] annotations, Schema parent, boolean applyNotNullAnnotations) {
    boolean result =
        super.applyBeanValidatorAnnotations(property, annotations, parent, applyNotNullAnnotations);
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

  /**
   * 提取注解的所有属性值，并将其以键值对形式返回。
   *
   * <p>如果注解没有声明任何方法（即没有属性），则返回 {@code Boolean.TRUE}。 否则返回一个 {@link
   * LinkedHashMap}，键为属性名，值为通过反射获取的属性值。
   *
   * @param annotation 要提取属性的注解
   * @return 属性名到属性值的映射；若注解无属性则返回 {@code true}
   */
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
