package controllers;

import akka.actor.ActorRef;
import infrastructure.MessageForwardingActor;
import infrastructure.Sender;
import infrastructure.redis.Redis;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Akka;
import play.libs.F;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.Callable;
import views.html.*;

import static play.Logger.debug;


@Singleton
public class Application extends Controller {

    @Inject
    Sender sender;
    @Inject
    Redis redis;
    @Inject
    @Named("message.actor")
    ActorRef messageForwardingActor;

    public Result index() {
        return ok(index.render()).as("text/html");
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result send() {
        final JsonNode json = request().body().asJson();

        debug("Got JSON: " + json);

        if (json == null) {
            return badRequest("Expecting Json data");
        }
        F.Promise<Boolean> promise = Akka.future(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                sender.send(json.toString());
                messageForwardingActor.tell(new MessageForwardingActor.Message(json.toString()), null);
                return true;
            }
        });
        return async(
                promise.map(new F.Function<Boolean, Result>() {
                    @Override
                    public Result apply(Boolean o) throws Throwable {
                        return ok("Hello " + json);
                    }
                })
        );
    }

    public Result messages() {
        F.Promise<List<String>> promise = Akka.future(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return redis.messages();
            }
        });

        return async(
                promise.map(new F.Function<List<String>, Result>() {
                    @Override
                    public Result apply(List<String> messages) throws Throwable {
                        // transform list of JSON messages to a single JSON message
                        ObjectNode result = Json.newObject();
                        ArrayNode messagesNode = result.putArray("messages");
                        for (String message : messages) {
                            messagesNode.add(Json.parse(message));
                        }
                        return ok(result);
                    }
                })
        );
    }

    public Result websockets() {
        return ok(websockets.render()).as("text/html");
    }

    public WebSocket<String> listen() {
        return new WebSocket<String>() {

            // Called when the Websocket Handshake is done.
            public void onReady(final WebSocket.In<String> in, final WebSocket.Out<String> out) {
                debug("WS connected");
                messageForwardingActor.tell(new MessageForwardingActor.Join(in, out), null);

                // When the socket is closed.
                in.onClose(new F.Callback0() {
                    public void invoke() {
                        debug("WS disconnected");
                        messageForwardingActor.tell(new MessageForwardingActor.Quit(in), null);
                    }
                });
//                out.write("Hello!");
            }
        };
    }
}
