package tutorials4j.springboot3.batch;

import org.springframework.batch.item.ItemProcessor;
import tutorials4j.springboot3.User;

/**
 * 清洗处理器
 *
 * @author Yun Jiao
 */
public class CleanItemProcessor implements ItemProcessor<User, User> {
    @Override
    public User process(User item) throws Exception {
        // 数据清洗
        String name = cleanName(item.getName());
        String email = cleanEmail(item.getEmail());

        // 数据转换
        name = name.toUpperCase();
        email = email.toLowerCase();

        return User.of(name, email);
    }

    private String cleanName(String name) {
        if (name == null) return "";
        return name.trim()
                .replaceAll("\\s+", " ")  // 多个空格替换为单个空格
                .replaceAll("[^\\p{L}\\p{N}\\s]", ""); // 移除非字母数字字符
    }

    private String cleanEmail(String email) {
        if (email == null) return "";
        return email.trim().toLowerCase();
    }

}
