package controllers;

import infrastructure.redis.Redis;
import infrastructure.Sender;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Akka;
import play.libs.F;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.Callable;

import static play.Logger.debug;

@Singleton
public class Application extends Controller {

    @Inject
    Sender sender;
    @Inject
    Redis redis;

    public Result index() {
        return ok(("Your new application is ready. <br /><a href='/messages'>Read messages</a>")).as("text/html");
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
}
