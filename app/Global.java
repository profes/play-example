import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.lambdaworks.redis.RedisConnection;
import infrastructure.RabbitConsumerFactory;
import infrastructure.rabbitmq.Queue;
import infrastructure.rabbitmq.RabbitQueue;
import infrastructure.redis.Redis;
import infrastructure.redis.RedisImpl;
import infrastructure.redis.RedisProvider;
import play.Application;
import play.GlobalSettings;

import static com.google.inject.name.Names.named;
import static com.typesafe.config.ConfigFactory.load;

public class Global extends GlobalSettings {

    private static final Injector INJECTOR = createInjector();

    private static Injector createInjector() {
        return Guice.createInjector(new GuiceModule());
    }

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

    private static class GuiceModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(String.class).annotatedWith(named("rabbitmq.host")).toInstance(load().getString("rabbitmq.host"));
            bind(String.class).annotatedWith(named("rabbitmq.queue")).toInstance(load().getString("rabbitmq.queue"));

            bind(Queue.class).to(RabbitQueue.class);

            bind(String.class).annotatedWith(named("redis.host")).toInstance(load().getString("redis.host"));

            bind(new TypeLiteral<RedisConnection<String, String>>() {
            }).toProvider(RedisProvider.class);

            bind(Redis.class).to(RedisImpl.class);

            install(new FactoryModuleBuilder().build(RabbitConsumerFactory.class));
        }
    }
}