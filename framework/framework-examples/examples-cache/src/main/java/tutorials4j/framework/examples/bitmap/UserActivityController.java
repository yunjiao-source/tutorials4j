package tutorials4j.framework.examples.bitmap;


import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import tutorials4j.framework.cache.redis.util.RedisBitmapUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 用户签到
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/user-activity")
public class UserActivityController {

    private static final String SIGN_PREFIX = "user:sign:";
    private static final String DAU_PREFIX = "dau:";

    /**
     * 用户签到
     * POST /api/bitmap/sign?userId=1001&date=2026-05-13
     */
    @PostMapping("/sign")
    public Result<Boolean> sign(@RequestParam("userId") Long userId,
                                @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String key = SIGN_PREFIX + userId + ":" + date.format(DateTimeFormatter.ofPattern("yyyyMM"));
        int offset = date.getDayOfMonth() - 1;  // 偏移量从0开始
        Boolean oldValue = RedisBitmapUtils.setBit(key, (long) offset, true);
        return Result.success(oldValue);
    }

    /**
     * 检查某天是否签到
     * GET /api/bitmap/sign/check?userId=1001&date=2026-05-13
     */
    @GetMapping("/sign/check")
    public Result<Boolean> checkSign(@RequestParam("userId") Long userId,
                                     @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String key = SIGN_PREFIX + userId + ":" + date.format(DateTimeFormatter.ofPattern("yyyyMM"));
        int offset = date.getDayOfMonth() - 1;
        boolean signed = RedisBitmapUtils.getBit(key, offset);
        return Result.success(signed);
    }

    /**
     * 统计用户当月累计签到天数
     * GET /api/bitmap/sign/count?userId=1001&yearMonth=202605
     */
    @GetMapping("/sign/count")
    public Result<Long> countSignInMonth(@RequestParam("userId") Long userId,
                                         @RequestParam("yearMonth") String yearMonth) { // 格式 yyyyMM
        String key = SIGN_PREFIX + userId + ":" + yearMonth;
        Long count = RedisBitmapUtils.bitCount(key);
        return Result.success(count == null ? 0L : count);
    }

    /**
     * 记录当日活跃用户（用户登录时调用）
     * POST /api/bitmap/active/record?userId=1001
     */
    @PostMapping("/active/record")
    public Result<Boolean> recordActive(@RequestParam("userId") Long userId) {
        String todayKey = DAU_PREFIX + LocalDate.now().toString();
        Boolean oldValue = RedisBitmapUtils.setBit(todayKey, userId, true);
        return Result.success(oldValue);
    }

    /**
     * 获取某日活跃用户数（日活）
     * GET /api/bitmap/active/dau?date=2026-05-13
     */
    @GetMapping("/active/dau")
    public Result<Long> getDailyActiveUsers(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String key = DAU_PREFIX + date.toString();
        Long count = RedisBitmapUtils.bitCount(key);
        return Result.success(count == null ? 0L : count);
    }

    /**
     * 获取连续 N 天活跃的用户数（AND 运算）
     * GET /api/bitmap/active/consecutive?startDate=2026-05-10&days=3
     */
    @GetMapping("/active/consecutive")
    public Result<Long> getConsecutiveActiveUsers(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                  @RequestParam("days") int days) {
        if (days <= 0) return Result.success(0L);
        String tempKey = "temp:consecutive:and";
        String[] keys = new String[days];
        for (int i = 0; i < days; i++) {
            keys[i] = DAU_PREFIX + startDate.plusDays(i).toString();
        }
        // 执行 AND 操作并直接获取结果中 1 的个数（bitOpResult 内部会自动 bitCount）
        Long count = RedisBitmapUtils.bitOpResult(RedisStringCommands.BitOperation.AND, tempKey, keys);
        // 清理临时键（可选）
        // RedisBitmapUtils.stringRedisTemplate.delete(tempKey);
        return Result.success(count == null ? 0L : count);
    }

    /**
     * 获取一周内（周一~周日）有过活跃的用户数（OR 运算）
     * GET /api/bitmap/active/weekly?anyDate=2026-05-13
     */
    @GetMapping("/active/weekly")
    public Result<Long> getWeeklyActiveUsers(@RequestParam("anyDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate anyDate) {
        LocalDate monday = anyDate.with(java.time.DayOfWeek.MONDAY);
        String tempKey = "temp:weekly:or";
        String[] keys = new String[7];
        for (int i = 0; i < 7; i++) {
            keys[i] = DAU_PREFIX + monday.plusDays(i).toString();
        }
        Long count = RedisBitmapUtils.bitOpResult(RedisStringCommands.BitOperation.OR, tempKey, keys);
        // RedisBitmapUtils.stringRedisTemplate.delete(tempKey);
        return Result.success(count == null ? 0L : count);
    }
}