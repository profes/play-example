import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import infrastructure.Queue;
import infrastructure.RabbitQueue;
import play.Application;
import play.GlobalSettings;

import static com.google.inject.name.Names.named;
import static com.typesafe.config.ConfigFactory.load;

public class Global extends GlobalSettings {

    private static final Injector INJECTOR = createInjector();

    @Override
    public void onStart(Application app) {
        RabbitQueue queue = INJECTOR.getInstance(RabbitQueue.class);
        queue.start();
    }

    @Override
    public void onStop(Application app) {
        RabbitQueue queue = INJECTOR.getInstance(RabbitQueue.class);
        queue.stop();
    }

    @Override
    public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
        return INJECTOR.getInstance(controllerClass);
    }

    private static Injector createInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(String.class).annotatedWith(named("rabbitmq.host")).toInstance(load().getString("rabbitmq.host"));
                bind(String.class).annotatedWith(named("rabbitmq.queue")).toInstance(load().getString("rabbitmq.queue"));

                bind(Queue.class).to(RabbitQueue.class);
            }
        });
    }
}