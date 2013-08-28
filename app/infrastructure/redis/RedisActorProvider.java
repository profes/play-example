package infrastructure.redis;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.google.inject.Provider;
import infrastructure.RedisWriterActorFactory;
import play.libs.Akka;

import javax.inject.Inject;

public class RedisActorProvider implements Provider<ActorRef> {

    private final RedisWriterActorFactory actorFactory;

    @Inject
    public RedisActorProvider(RedisWriterActorFactory actorFactory) {
        this.actorFactory = actorFactory;
    }

    @Override
    public ActorRef get() {
        return Akka.system().actorOf(new Props(actorFactory));
    }
}
