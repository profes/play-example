package infrastructure.rabbitmq;

import com.rabbitmq.client.Channel;
import infrastructure.rabbitmq.Consumer;

public interface RabbitConsumerFactory {
    public Consumer create(Channel channel);
}
