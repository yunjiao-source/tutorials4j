-- 加锁脚本（SET IF ABSENT）
-- 用途：原子性地设置键值对并指定过期时间，仅当键不存在时执行。
-- 参数：
--   KEYS[1] : 要设置的键名（如 "lock:order:123"）
--   ARGV[1] : 要设置的值（通常为 UUID，作为持有者标识）
--   ARGV[2] : 过期时间（单位：毫秒）
-- 返回值：
--   1 : 键不存在，设置成功
--   0 : 键已存在，设置失败

if redis.call('exists', KEYS[1]) == 0 then
    -- 键不存在，设置键值并指定过期时间（单位：毫秒）
    redis.call('psetex', KEYS[1], tonumber(ARGV[2]), ARGV[1])
    return 1
else
    -- 键已存在，返回0表示加锁失败
    return 0
end