package infrastructure;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import play.Logger;

import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class Sender {

    private static final String QUEUE_NAME = RabbitConfig.getRabbitQueue();

    private Channel channel;
    private Connection connection;

    public Sender() {
    }

    public void start() {
        try {
            Logger.debug("Connecting to rabbitmq");

            connection = RabbitConnection.getConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        } catch (IOException e) {
            Logger.error("failed connecting to rabbitmq", e);
        }
    }

    public void stop() {
        try {
            Logger.debug("Stopping rabbitmq connection");

            channel.close();
            connection.close();
        } catch (IOException e) {
            Logger.error("Failed closing channel", e);
        }
    }

    public void send(String message) {
        try {
            Logger.debug("Sending message: " + message);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        } catch (IOException e) {
            Logger.error("failed sending message " + message, e);
        }
    }
}
