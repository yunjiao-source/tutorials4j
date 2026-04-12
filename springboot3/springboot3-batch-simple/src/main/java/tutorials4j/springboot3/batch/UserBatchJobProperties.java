package tutorials4j.springboot3.batch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.BatchJobProperties;

/**
 * 用户批处理配置
 *
 * @author Yun Jiao
 */
@Data
@Component
@ConfigurationProperties(prefix = "batch.user")
public class UserBatchJobProperties {
    private String name = "importUserByCSV";
    private Resource inputPath;
    private Integer linesToSkip = 1;
    private String delimiter = ",";

    private String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private Integer nameMinLength = 5;

    private Integer nameMaxLength = 30;

    private BatchJobProperties job = new BatchJobProperties();
}
