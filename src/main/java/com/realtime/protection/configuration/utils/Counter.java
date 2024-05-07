package com.realtime.protection.configuration.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Counter {
    private final StringRedisTemplate stringRedisTemplate;

    public Counter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public Long generateId(String typeName) {
        String key = String.format("counter::%s", typeName.toLowerCase());
        BoundValueOperations<String, String> op = stringRedisTemplate.boundValueOps(key);
        return op.increment();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetId() {
        Cursor<String> cursor = stringRedisTemplate.scan(ScanOptions.scanOptions().build());
        while (cursor.hasNext()) {
            String key = cursor.next();
            if (key.startsWith("counter::")) {
                stringRedisTemplate.delete(key);
                log.debug("删除计时器key: {}", key);
            }
        }
    }
}
