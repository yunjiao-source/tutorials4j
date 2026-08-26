-- 带值校验的过期时间重置（CHECK AND RESET EXPIRE）
-- 用途：仅当键的值等于期望值时，重置该键的过期时间（续期），用于锁续命。
-- 参数：
--   KEYS[1] : 要操作的键
--   ARGV[1] : 期望的当前值（锁持有者标识）
--   ARGV[2] : 新的过期时间（单位：毫秒）
-- 返回值：
--   1 : 续期成功（键存在且值匹配，PEXPIRE 返回 1）
--   0 : 续期失败（键不存在、值不匹配，或 PEXPIRE 返回 0）

if redis.call('get', KEYS[1]) == ARGV[1] then
    return redis.call('pexpire', KEYS[1], ARGV[2])
else
    return 0
end