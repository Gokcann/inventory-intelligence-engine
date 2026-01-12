-- stock_reservation.lua
-- Atomic stock check and decrement
-- KEYS[1] = stock key (e.g., "stock:SKU123")
-- ARGV[1] = requested quantity

local current = tonumber(redis.call('GET', KEYS[1]) or 0)
local requested = tonumber(ARGV[1])

if current >= requested then
    redis.call('DECRBY', KEYS[1], requested)
    return current - requested  -- new balance
else
    return -1  -- insufficient stock
end
