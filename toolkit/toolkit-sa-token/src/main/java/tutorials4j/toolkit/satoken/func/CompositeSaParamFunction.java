package tutorials4j.toolkit.satoken.func;

import cn.dev33.satoken.fun.SaParamFunction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Slf4j
public class CompositeSaParamFunction implements SaParamFunction<Object> {
  private final List<NamedSaParamFunction> functions;

  public CompositeSaParamFunction(Collection<? extends NamedSaParamFunction> saParamFunctions) {
    Assert.notNull(saParamFunctions, "saParamFunctions must not be null");
    this.functions = new ArrayList<>(saParamFunctions);
  }

  @Override
  public void run(Object r) {
    functions.forEach(
        e -> {
          if (log.isDebugEnabled()) {
            log.debug("执行参数函数：{}", e.getClass().getName());
          }
          e.run(r);
        });
  }
}
