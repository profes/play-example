package infrastructure.rabbitmq;

import com.google.inject.name.Named;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import infrastructure.RabbitConsumerFactory;
import play.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

import static play.Logger.debug;

@Singleton
public class RabbitQueue implements Queue {

    public static final String CONSUMER_TAG = "consumer";
    private final String host;
    private final String queue;
    private final RabbitConsumerFactory consumerFactory;
    private Channel channel;
    private Connection connection;

    @Inject
    public RabbitQueue(@Named("rabbitmq.host") String host, @Named("rabbitmq.queue") String queue, RabbitConsumerFactory consumerFactory) {
        this.host = host;
        this.queue = queue;
        this.consumerFactory = consumerFactory;
    }

    public void start() {
        try {
            debug("Connecting to rabbitmq");

            connection = getConnection();
            channel = connection.createChannel();
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicConsume(queue, false, CONSUMER_TAG, consumerFactory.create(channel));
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

            channel.basicCancel(CONSUMER_TAG);

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