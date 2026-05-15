package tutorials4j.springboot3.convert;

import java.util.Set;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.util.Assert;

/**
 * 将字符串转换成对象
 *
 * @author Yun Jiao
 */
public class StringToUserFormatter implements AnnotationFormatterFactory<UserFormat> {
  @Override
  public Set<Class<?>> getFieldTypes() {
    return Set.of(User.class);
  }

  @Override
  public Printer<User> getPrinter(UserFormat annotation, Class<?> fieldType) {
    return (object, locale) -> object.toString();
  }

  @Override
  public Parser<User> getParser(UserFormat annotation, Class<?> fieldType) {
    return (text, locale) -> {
      Assert.hasText(text, "数据错误");
      String[] s = text.split(",");
      User user = new User();
      user.setId(Long.parseLong(s[0]));
      user.setName(s[1]);
      return user;
    };
  }
}
