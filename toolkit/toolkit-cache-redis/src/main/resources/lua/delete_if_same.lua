-- 安全删除脚本（DELETE IF SAME）
-- 用途：仅当键的当前值等于给定值时，删除该键，用于释放分布式锁。
-- 参数：
--   KEYS[1] : 要操作的键
--   ARGV[1] : 期望的值（锁持有者标识）
-- 返回值：
--   1 : 删除成功（值匹配且 DEL 返回 1）
--   0 : 键不存在或值不匹配，未删除

local value = redis.call('GET', KEYS[1])
if not value then
    return 0
end
if value == ARGV[1] then
    local val = redis.call('DEL', KEYS[1])
    if val == 1 then
        return 1
    end
end
return 0