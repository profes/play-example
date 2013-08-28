package infrastructure;

import akka.actor.UntypedActor;
import com.google.common.collect.Maps;
import play.mvc.WebSocket;

import java.util.Map;

public class MessageForwardingActor extends UntypedActor {

    private Map<WebSocket.In<String>, WebSocket.Out<String>> sockets = Maps.newHashMap();

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Join) {
            Join join = (Join) o;
            connect(join.in, join.out);
        } else if (o instanceof Quit) {
            Quit quit = (Quit) o;
            disconnect(quit.in);
        } else if (o instanceof Message) {
            Message message = (Message) o;
            notifyAll(message.message);
        } else unhandled(o);
    }

    private void connect(WebSocket.In<String> in, WebSocket.Out<String> out) {
        sockets.put(in, out);
    }

    private void disconnect(WebSocket.In<String> in) {
        sockets.remove(in);
    }

    public void notifyAll(String message) {
        for (WebSocket.Out<String> channel : sockets.values()) {
            channel.write(message);
        }
    }

    public static class Join {
        final WebSocket.In<String> in;
        final WebSocket.Out<String> out;

        public Join(WebSocket.In<String> in, WebSocket.Out<String> out) {
            this.in = in;
            this.out = out;
        }
    }

    public static class Quit {
        final WebSocket.In<String> in;

        public Quit(WebSocket.In<String> in) {
            this.in = in;
        }
    }

    public static class Message {
        final String message;

        public Message(String message) {
            this.message = message;
        }
    }
}
