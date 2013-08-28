package infrastructure;

import akka.actor.Actor;
import akka.actor.UntypedActorFactory;
import com.google.inject.Injector;

import javax.inject.Inject;

public class ActorFactory implements UntypedActorFactory {

    private final Injector injector;

    @Inject
    public ActorFactory(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Actor create() throws Exception {
        return injector.getInstance(RedisWriterActor.class);
    }
}
