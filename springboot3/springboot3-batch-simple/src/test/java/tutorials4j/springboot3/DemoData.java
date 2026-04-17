package tutorials4j.springboot3;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

/**
 * 示例数据生成
 *
 * @author Yun Jiao
 */
public class DemoData {
    @Test
    public void demoData() {
        Faker faker = new Faker();

        StringBuilder data = new StringBuilder();
        IntStream.range(0, 1000).forEach(i -> data
                .append(faker.name().fullName())
                .append(",")
                .append(faker.internet().emailAddress())
                .append(System.lineSeparator()));
        System.out.println(data.toString());
    }
}
