package infrastructure;

import play.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

import static play.Logger.debug;

@Singleton
public class Sender {
    private final Queue queue;

    @Inject
    public Sender(Queue queue) {
        this.queue = queue;
    }

    public void send(String message) {
        try {
            debug("Sending message: " + message);
            queue.send(message);
        } catch (IOException e) {
            Logger.error("failed sending message " + message, e);
        }
    }
}
