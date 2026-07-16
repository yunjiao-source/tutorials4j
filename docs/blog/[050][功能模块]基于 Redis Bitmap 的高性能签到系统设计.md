## [050][功能模块]基于 Redis Bitmap 的高性能签到系统设计

本项目代码: https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework


在用户激励体系中，签到功能几乎是每个互联网产品的标配。签到数据通常需要记录用户每天的签到状态、连续签到天数、月度签到日历，同时还要支持日活（DAU）和月活（MAU）的统计。如果直接使用关系数据库存储每条签到记录，在用户量较大时会产生巨大的写入和查询压力。**利用 Redis 的 Bitmap 数据结构，可以将一个用户一个月的签到状态压缩到几个字节内，同时支持毫秒级的位操作和统计**。

本文基于一套开源的 `tutorials4j` 签到模块代码，详细分析其设计思想、核心实现以及关键技术点，帮助读者快速构建高并发、低成本的签到系统。

---

### 一、整体架构与模块划分

整个签到模块围绕以下核心类构建：

| 类名 | 职责 |
|------|------|
| `SignInService` | 入口服务，管理不同业务来源（source）的 `SignInTemplate` 实例 |
| `SignInTemplate` | 签到模板，封装签到、查询、统计等核心操作 |
| `SignInConfig` | 配置对象（记录类型），定义 `source`、`keyPrefix`、`expireTime` |
| `SignInProperties` | Spring Boot 配置属性，提供全局默认值 |
| `SignInResultHandler` | 函数式接口，用于处理签到结果（如记录日志、发放奖励） |
| `SignInUtils` | 工具类，负责生成 Redis Key 和计算 offset |
| `SignInResult` / `SignInCalendar` | 返回值对象 |

设计上采用了**模板模式**：`SignInService` 根据 `source` 创建或获取对应的 `SignInTemplate`，每个 `SignInTemplate` 持有独立的 `SignInConfig`，使得不同业务线（如“每日任务”、“节日活动”）可以使用不同的 Key 前缀和过期策略。

```mermaid
graph LR
    A[客户端] --> B[SignInService]
    B --> C1[SignInTemplate (source=task)]
    B --> C2[SignInTemplate (source=event)]
    C1 --> D[Redis Bitmap]
    C2 --> D
    C1 --> E[SignInResultHandler]
    C2 --> E
```

---

### 二、核心功能实现解析

#### 1. 签到 – `SignInTemplate.signIn()`

**流程**：
1. 根据 `account` 和 `signDate` 生成月份 Key（格式：`{keyPrefix}{source}:{account}:yyyyMM`）。
2. 计算当天的 offset = `date.getDayOfMonth() - 1`。
3. 调用 `RedisBitmapUtils.setBit(key, offset, true)`，返回旧值。
4. 若旧值为 `false`（首次签到），则设置 Key 的过期时间，并记录日活/月活。
5. 查询本月累计签到天数（`bitCount`）和连续签到天数。
6. 构造 `SignInResult` 并交给 `SignInResultHandler` 处理。

**关键点**：
- **防重复签到**：通过 `setBit` 返回的旧值判断，若已为 `true`，`repeatedSignIn=true`，且不重复累加日活。
- **过期时间**：签到 Key 的过期时间由 `SignInConfig.expireTime` 控制（默认为 365 天），避免 Redis 中残留过多历史数据。

```java
boolean firstSigned = doSign(monthKey, offset);
if(firstSigned) {
    doActive(signDate, account);  // 记录日活/月活
}
```

#### 2. 连续签到天数计算 – `calculateContinuousDays()`

这是签到系统中最有技术含量的部分。代码中利用 Redis 的 `BITFIELD` 命令，一次性读取从当月第1天到当前天的所有 bit 位，作为一个无符号整数，然后通过位运算从低位向高位统计连续为 1 的个数。

**算法步骤**：
- 设当前日期为当月第 `day` 天（例如 2026-06-08，则 `day=8`）。
- 使用 `BITFIELD key GET u{day} 0` 获取从 offset 0 开始的 `day` 位无符号整数。这意味着该整数的二进制表示中，最低位（bit 0）对应当月的第1天，最高位（bit day-1）对应第 `day` 天。
- 循环检查该整数的最低有效位（`value & 1`）是否为 1，若为 1 则计数加一，然后右移一位继续检查，直到遇到 0 或检查完所有位。
- 最终计数即为从当前天向前连续签到的天数（包含当天）。

**示例**：假设当月已签到 1、2、3、5 天，今天是第 5 天。`BITFIELD` 读出的前 5 位二进制为 `10111`（低→高：第1天=1，第2天=1，第3天=1，第4天=0，第5天=1）。从低位开始检测：第1位=1 → count=1，右移得 `1011`；最低位=1 → count=2，右移得 `101`；最低位=1 → count=3，右移得 `10`；最低位=0 → 停止。结果 `continuousDays=3`，正确反映了 1、2、3 号的连续签到，5 号虽然签到了但与前一次签到中断。

```java
List<Long> result = redisTemplate.opsForValue().bitField(key,
    BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(day)).valueAt(0));
long value = result.getFirst();
long count = 0;
for (int i = 0; i < day; i++) {
    if ((value & 1) == 0) break;
    count++;
    value >>= 1;
}
```

> ⚠️ 注意：此算法假设 `BITFIELD` 返回的整数最低位对应当月第1天，这与 Redis 的位图偏移量定义一致（offset 0 为第1天）。另外，该算法**不处理跨月连续签到**，如需跨月需额外查询上个月的连续签到情况。

#### 3. 签到日历 – `queryCalendar()`

返回用户当月的签到日历，包含：
- 已签到的日期列表（`signedDays`）
- 本月累计签到天数
- 今天是否签到
- 当前连续签到天数

实现方式简单直接：遍历当月每一天的 offset（0 到 `daysInMonth-1`），逐个调用 `getBit` 检查，收集签到的日期。时间复杂度 O(N)，N ≤ 31，性能可接受。

```java
List<Integer> signedDays = new ArrayList<>();
for (int i = 0; i < daysInMonth; i++) {
    if (Boolean.TRUE.equals(RedisBitmapUtils.instance.getBit(monthKey, i))) {
        signedDays.add(i + 1);
    }
}
```

#### 4. 日活/月活统计 – `doActive()` 与 `countXXXActive()`

**统计思路**：
- 为每一天（或月）创建一个全局 Bitmap，Key 格式为 `{keyPrefix}{source}:dau:yyyyMMdd`（或 `mau:yyyyMM`）。
- 每个用户通过哈希算法映射到该 Bitmap 中的某一个 bit 位。
- 首次签到当天，设置对应的 bit 为 1，表示该用户今日活跃。
- 统计时直接调用 `bitCount` 获取 Bitmap 中 1 的数量。

**哈希映射**：代码中使用 `RedisBitmapUtils.hash(account) % MAX_BITS`，其中 `MAX_BITS = 100_000`，意味着单个 Bitmap 最多支持 10 万用户。若用户量超过此值，可能出现哈希冲突导致统计略微偏低，但通常可以接受，也可以调整 `MAX_BITS` 为更大值（如 1 亿，对应约 12MB 内存）。

```java
long offset = Math.abs(RedisBitmapUtils.instance.hash(account) / MAX_BITS);
String dauKey = getDauKey(date);
Boolean oldValue = RedisBitmapUtils.instance.setBit(dauKey, offset, true);
if (Boolean.FALSE.equals(oldValue)) {
    RedisBitmapUtils.instance.setExpireTime(dauKey, config.expireTime());
}
```

#### 5. 签到结果处理 – `SignInResultHandler`

`SignInService` 和 `SignInTemplate` 构造时接收一个 `SignInResultHandler` 实例，每次签到完成后会自动调用其 `handle(result)` 方法。开发者可以实现该接口来记录日志、发放奖励、发送消息等。代码中提供了一个简单的日志实现 `LoggingSignInResultHandler`。

这体现了**开闭原则**：签到核心逻辑与后续处理解耦，便于扩展。

---

### 三、配置与扩展性

#### 1. Spring Boot 自动配置风格

`SignInProperties` 绑定前缀 `tutorials4j.feature.sign-in`（通过 `PropertiesConsts.PROPERTY_PREFIX_FEATURE_SIGN_IN` 定义），支持以下配置项：

```yaml
tutorials4j:
  feature:
    sign-in:
      key-prefix: "myapp:sign:"   # 默认 "sign-in:"
      expire-time: 365D           # 默认 365 天
```

#### 2. 多源隔离

`SignInService.template(String source)` 方法允许传入不同的 `source`（如 `"daily"`、`"lottery"`），内部根据 `SignInConfig`（可部分覆盖默认值）创建独立的 `SignInTemplate`。不同源的 Redis Key 前缀不同，彼此数据完全隔离。

```java
// 使用默认配置
SignInTemplate template = signInService.template("daily");

// 自定义前缀和过期时间
SignInConfig config = SignInConfig.builder()
    .source("activity")
    .keyPrefix("act:sign:")
    .expireTime(Duration.ofDays(30))
    .build();
SignInTemplate activityTemplate = signInService.template(config);
```

---

### 四、使用示例

假设已经配置好 `RedisTemplate` 并初始化了 `SignInService` Bean：

```java
@Autowired
private SignInService signInService;

public void demo() {
    // 获取签到模板
    SignInTemplate template = signInService.template("demo");

    // 用户今日签到
    LocalDate today = LocalDate.now();
    SignInResult result = template.signIn("user123", today);
    System.out.println("签到成功：" + result.signedIn());
    System.out.println("连续签到：" + result.continuousDays() + "天");

    // 查询某天是否签到
    boolean signed = template.checkStatus("user123", today);
    
    // 获取月度日历
    SignInCalendar calendar = template.queryCalendar("user123", today);
    System.out.println("本月已签日期：" + calendar.getSignedDays());

    // 统计今日活跃用户数
    long dau = template.countDailyActive(today);
}
```

---

### 五、注意事项与优化建议

1. **连续签到跨月问题**  
   当前实现仅计算当月的连续签到，如果用户在上月最后几天连续签到，跨月后连续天数会重置。如需真正的跨月连续，需要在 `calculateContinuousDays` 中加入上月最后一天的检测逻辑，或者查询上个月的 Bitmap 并递归计算。

2. **用户量超过 MAX_BITS**  
   `MAX_BITS` 硬编码为 10 万，当用户数超过该值时哈希冲突概率增大，导致日活统计偏低。建议将 `MAX_BITS` 设计为可配置参数，或者使用更精确的 HyperLogLog 替代 Bitmap 做日活统计（但 Bitmap 可以获取具体哪些用户活跃）。

3. **BITFIELD 命令的兼容性**  
   Redis 4.0 及以上版本支持 `BITFIELD`。如果使用较低版本，需要改用多次 `GETBIT` 循环计算连续天数（性能会明显下降）。

4. **分布式环境下的并发**  
   单次 `setBit` 是原子操作，但“签到 + 日活记录”两步并非原子。极端情况下（如同一个用户并发签到两次），可能导致日活被重复计数。可以通过分布式锁或 Lua 脚本将两步合并为一个原子操作。

5. **Key 的过期策略**  
   签到 Key 和 DAU/MAU Key 都使用了相同的 `expireTime`。实际上 DAU Key 的保留时间可以更短（例如保留 30 天），而签到记录可能需要更长。可以根据业务需要为不同 Key 单独设置过期时间。

---

### 六、总结

这套签到模块充分利用了 Redis Bitmap 的特性：
- **内存占用极低**：每个用户每月仅占用 N/8 字节（N=当月天数，最多 4 字节）。
- **操作高效**：`setBit` / `getBit` / `bitCount` 均为 O(1) 或 O(N) 但 N 上限 31 天。
- **统计灵活**：通过 `BITFIELD` 一条命令即可完成连续签到计算，无需多次查询。

同时，代码通过 `SignInService` + `SignInTemplate` 的设计支持多业务源隔离，并通过 `SignInResultHandler` 提供了良好的扩展点。开发者可以快速将此模块集成到自己的项目中，稍作调整即可满足大部分签到场景的需求。

如果你正在设计一个低成本的签到系统，不妨参考这种基于 Redis Bitmap 的实现方式。它轻量、高效，足以支撑千万级用户的产品。