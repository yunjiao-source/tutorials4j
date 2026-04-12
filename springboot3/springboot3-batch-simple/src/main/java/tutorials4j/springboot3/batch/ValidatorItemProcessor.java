package tutorials4j.springboot3.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

/**
 * 用户校验处理
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class ValidatorItemProcessor implements ItemProcessor<UserCsvRecord, UserCsvRecord> {
    private final UserCsvValidator userCsvValidator;

    @Override
    public UserCsvRecord process(UserCsvRecord item) throws Exception {
        userCsvValidator.validate(item);
        return item;
    }
}
