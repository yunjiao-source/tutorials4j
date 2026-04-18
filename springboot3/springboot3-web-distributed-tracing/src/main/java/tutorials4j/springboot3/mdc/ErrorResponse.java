package tutorials4j.springboot3.mdc;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * web错误响应
 *
 * @author Yun Jiao
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private String traceId;

    private String path;

    private String code;

    // 构造方法
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

}