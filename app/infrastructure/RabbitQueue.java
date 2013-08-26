package infrastructure;

import com.google.inject.name.Named;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import play.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

import static play.Logger.debug;

@Singleton
public class RabbitQueue implements Queue {

    private final String host;
    private final String queue;

    private Channel channel;
    private Connection connection;

    @Inject
    public RabbitQueue(@Named("rabbitmq.host") String host, @Named("rabbitmq.queue") String queue) {
        this.host = host;
        this.queue = queue;
    }

    public void start() {
        try {
            debug("Connecting to rabbitmq");

            connection = getConnection();
            channel = connection.createChannel();
            channel.queueDeclare(queue, false, false, false, null);
        } catch (IOException e) {
            Logger.error("failed connecting to rabbitmq", e);
        }
    }

    private Connection getConnection() throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        return factory.newConnection();
    }

    public void stop() {
        try {
            debug("Stopping rabbitmq connection");

            channel.close();
            connection.close();
        } catch (IOException e) {
            Logger.error("Failed closing channel/stopping rabbitmq", e);
        }
    }

    @Override
    public void send(String message) throws IOException {
        debug("message " + message);
        channel.basicPublish("", queue, null, message.getBytes());
    }
}