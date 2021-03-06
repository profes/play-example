package infrastructure;

import akka.actor.UntypedActor;
import infrastructure.redis.Redis;

import javax.inject.Inject;

import static play.Logger.debug;

public class RedisWriterActor extends UntypedActor {

    private final Redis redis;

    @Inject
    public RedisWriterActor(Redis redis) {
        this.redis = redis;
    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof String) {
            String message = (String) msg;

            debug("RedisWriterActor actor: " + message);
            redis.save(message);
        } else unhandled(msg);
    }
}