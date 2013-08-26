package controllers;

import infrastructure.Sender;
import org.codehaus.jackson.JsonNode;
import play.libs.Akka;
import play.libs.F;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.Callable;

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
}
