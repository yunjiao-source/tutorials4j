-- 原子性条件更新（CHECK AND SET）
-- 用途：仅当键的当前值等于期望旧值时，将其更新为新值，常用于乐观锁或解锁操作。
-- 参数：
--   KEYS[1] : 要操作的键
--   ARGV[1] : 期望的旧值（必须与当前值完全匹配）
--   ARGV[2] : 要设置的新值
-- 返回值：
--   1 : 更新成功（当前值匹配，已设置为新值）
--   0 : 更新失败（当前值不等于期望旧值，或键不存在）

local current = redis.call('GET', KEYS[1])
if current == ARGV[1] then
    redis.call('SET', KEYS[1], ARGV[2])
    return 1
end
return 0