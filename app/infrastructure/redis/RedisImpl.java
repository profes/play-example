package infrastructure.redis;

import com.lambdaworks.redis.RedisConnection;
import play.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class RedisImpl implements Redis {

    private static final String REDIS_KEY = "messages";

    private final RedisConnection<String, String> connection;

    @Inject
    public RedisImpl(RedisConnection<String, String> connection) {
        this.connection = connection;
    }

    @Override
    public void save(String message) {
        connection.rpush(REDIS_KEY, message);
        Logger.debug("Saved message " + message + " in redis");
    }

    @Override
    public List<String> messages() {
        return connection.lrange(REDIS_KEY, 0, -1);
    }
}
