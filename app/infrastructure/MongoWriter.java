package infrastructure;

import akka.actor.UntypedActor;

import static play.Logger.debug;

public class MongoWriter extends UntypedActor {

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof String) {
            String message = (String) msg;

            debug("MongoWriter actor: " + message);
        } else unhandled(msg);
    }
}