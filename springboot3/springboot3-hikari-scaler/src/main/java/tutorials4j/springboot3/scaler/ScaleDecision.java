package tutorials4j.springboot3.scaler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 扩缩容决策类
 *
 * @author Yun Jiao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScaleDecision {
    private boolean shouldScale;
    private ScaleType scaleType;
    private int targetPoolSize;
    private String reason;

    public enum ScaleType {
        UP, DOWN
    }

    public static ScaleDecision noScale(String reason) {
        return ScaleDecision.builder()
                .shouldScale(false)
                .reason(reason)
                .build();
    }

    public static ScaleDecision scaleUp(int targetSize, String reason) {
        return ScaleDecision.builder()
                .shouldScale(true)
                .scaleType(ScaleType.UP)
                .targetPoolSize(targetSize)
                .reason(reason)
                .build();
    }

    public static ScaleDecision scaleDown(int targetSize, String reason) {
        return ScaleDecision.builder()
                .shouldScale(true)
                .scaleType(ScaleType.DOWN)
                .targetPoolSize(targetSize)
                .reason(reason)
                .build();
    }
}
