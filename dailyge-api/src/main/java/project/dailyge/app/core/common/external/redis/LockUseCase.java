package project.dailyge.app.core.common.external.redis;

import org.redisson.api.RedissonClient;
import project.dailyge.app.common.annotation.ApplicationLayer;
import project.dailyge.lock.Lock;
import project.dailyge.lock.LockService;

@ApplicationLayer(value = "LockUseCase")
public class LockUseCase implements LockService {

    private final RedissonClient redissonClient;

    public LockUseCase(final RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public Lock getLock(final Long userId) {
        return RedisUtils.getLock(redissonClient, userId);
    }

    @Override
    public void releaseLock(final Lock lock) {
        RedisUtils.releaseLock(lock);
    }
}
