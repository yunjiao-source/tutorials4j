package tutorials4j.toolkit.satoken.func;

import cn.dev33.satoken.fun.SaParamFunction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Slf4j
public class CompositeSaParamFunction implements SaParamFunction<Object> {
  private final List<NamedSaParamFunction> functions;

  public CompositeSaParamFunction(Collection<? extends NamedSaParamFunction> saParamFunctions) {
    Assert.notNull(saParamFunctions, "saParamFunctions must not be null");
    this.functions = new ArrayList<>(saParamFunctions);
  }

  public CompositeSaParamFunction newInstanceBy(List<String> paramFunctionNames) {
    Assert.notNull(paramFunctionNames, "authStrategyNames must not be null");

    var newFunctions = new ArrayList<NamedSaParamFunction>();
    paramFunctionNames.forEach(
        name -> {
          for (NamedSaParamFunction function : this.functions) {
            if (Objects.equals(function.getName(), name)) {
              newFunctions.add(function);
              break;
            }
          }
        });
    return new CompositeSaParamFunction(newFunctions);
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
