package tutorials4j.feature.basic.api;

import java.util.List;
import java.util.Set;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import tutorials4j.feature.basic.model.DictDataVO;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@HttpExchange
public interface BasicApiClient {
  @GetExchange("basic/dict-type/by-id/{id}/dict-data")
  List<DictDataVO> getDictDataById(
      @PathVariable("id") String id,
      @RequestParam(name = "allEnabled", required = false) Set<YesNoEnum> allEnabled);

  @GetExchange("basic/dict-type/by-code/{code}/dict-data")
  List<DictDataVO> getDictDataByCode(
      @PathVariable("code") String code,
      @RequestParam(name = "allEnabled", required = false) Set<YesNoEnum> allEnabled);
}
