package tutorials4j.framework.feature.signin.service;

import java.time.LocalDate;
import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record SignInResult(
    String account,
    LocalDate signDate,
    String source,
    Boolean signedIn,
    Boolean repeatedSignIn,
    Long continuousDays,
    Long monthlySignedDays) {}
