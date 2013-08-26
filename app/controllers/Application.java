package controllers;

import infrastructure.Sender;
import org.codehaus.jackson.JsonNode;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

import static play.Logger.debug;

@Singleton
public class Application extends Controller {

    @Inject
    Sender sender;

    public Result index() {
        return ok(("Your new application is ready."));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result send() {
        JsonNode json = request().body().asJson();

        debug("Got JSON: " + json);

        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            String message = json.findPath("message").getTextValue();
            if (message == null) {
                return badRequest("Missing parameter [message]");
            } else {
                sender.send(message);
                return ok("Hello " + message);
            }
        }
    }
}
