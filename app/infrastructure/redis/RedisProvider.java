package infrastructure.redis;

import com.google.inject.Provider;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class RedisProvider implements Provider<RedisConnection<String, String>> {

    private final String host;
    private RedisConnection<String, String> connection;

    @Inject
    public RedisProvider(@Named("redis.host") String host) {
        this.host = host;
    }

    @Override
    public RedisConnection<String, String> get() {
        if (connection == null) {
            RedisClient client = new RedisClient(host);
            connection = client.connect();
        }
        return connection;
    }
}
