package infrastructure;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

public class RabbitConnection {
    private static Connection connection;

    public static Connection getConnection() throws IOException {
        if (connection == null) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(RabbitConfig.getRabbitHost());
            connection = factory.newConnection();
        }
        return connection;
    }
}