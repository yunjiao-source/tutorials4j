package tutorials4j.framework.feature.signin.service;

import java.time.LocalDate;
import lombok.Builder;

/**
 * 签到结果
 *
 * <p>记录一次签到或签到查询的结果，包含签到账号、日期、来源、是否签到成功、是否重复签到 以及连续签到天数、当月累计签到天数等信息。
 *
 * @param account 签到账号
 * @param signDate 签到日期
 * @param source 签到来源标识
 * @param signedIn 本次是否签到成功
 * @param repeatedSignIn 是否为重复签到
 * @param continuousDays 连续签到天数
 * @param monthlySignedDays 当月累计签到天数
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
