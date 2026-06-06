local key = KEYS[1]
local offset = tonumber(ARGV[1])
local expireSeconds = tonumber(ARGV[2])

local old = redis.call('GETBIT', key, offset)
if old == 1 then
    return 0
end

redis.call('SETBIT', key, offset, 1)
if expireSeconds > 0 then
    redis.call('EXPIRE', key, expireSeconds)
end
return 1