package infrastructure;

import com.rabbitmq.client.Channel;

public interface RabbitConsumerFactory {
    public Consumer create(Channel channel);
}
