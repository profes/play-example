package infrastructure;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.google.inject.Provider;
import play.libs.Akka;

import javax.inject.Inject;

public class MessageForwardingActorProvider implements Provider<ActorRef> {

    @Override
    public ActorRef get() {
        return Akka.system().actorOf(new Props(MessageForwardingActor.class));
    }
}
