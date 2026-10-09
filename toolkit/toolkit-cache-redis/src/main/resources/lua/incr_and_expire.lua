-- KEYS[1] : 要操作的 key
-- ARGV[1] : 过期时间（毫秒）
local current = redis.call('INCR', KEYS[1])
if current == 1 then
    redis.call('PEXPIRE', KEYS[1], ARGV[1])
end
return current