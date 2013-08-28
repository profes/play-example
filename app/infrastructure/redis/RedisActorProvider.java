package infrastructure.redis;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.google.inject.Provider;
import infrastructure.ActorFactory;
import play.libs.Akka;

import javax.inject.Inject;

public class RedisActorProvider implements Provider<ActorRef> {

    private final ActorFactory actorFactory;

    @Inject
    public RedisActorProvider(ActorFactory actorFactory) {
        this.actorFactory = actorFactory;
    }

    @Override
    public ActorRef get() {
        return Akka.system().actorOf(new Props(actorFactory));
    }
}
