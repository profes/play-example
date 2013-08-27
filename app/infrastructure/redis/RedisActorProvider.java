package infrastructure.redis;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import com.google.inject.Provider;
import infrastructure.RedisWriterActor;
import play.libs.Akka;

import javax.inject.Inject;

public class RedisActorProvider implements Provider<ActorRef> {

    private final Redis redis;

    @Inject
    public RedisActorProvider(Redis redis) {
        this.redis = redis;
    }

    @Override
    public ActorRef get() {

        return Akka.system().actorOf(new Props(new UntypedActorFactory() {
            @Override
            public Actor create() throws Exception {
                return new RedisWriterActor(redis);
            }
        }));
    }
}
