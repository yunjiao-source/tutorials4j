package tutorials4j.springboot3;

import lombok.Data;

/**
 * 批处理任务属性
 *
 * @author Yun Jiao
 */
@Data
public class BatchJobProperties {
    /**
     * 批量分块
     */
    private Integer chunkSize = 100;

    /**
     * 跳过限制
     */
    private Integer skipLimit = 100;

    /**
     * 重试次数
     */
    private Integer retryLimit = 3;

}
