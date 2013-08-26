import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import infrastructure.Sender;
import play.Application;
import play.GlobalSettings;


public class Global extends GlobalSettings {

    private static final Injector INJECTOR = createInjector();

    @Override
    public void onStart(Application app) {
        Sender sender = INJECTOR.getInstance(Sender.class);
        sender.start();
    }

    @Override
    public void onStop(Application app) {
        Sender sender = INJECTOR.getInstance(Sender.class);
        sender.stop();
    }

    @Override
    public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
        return INJECTOR.getInstance(controllerClass);
    }

    private static Injector createInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Sender.class);
            }
        });
    }
}