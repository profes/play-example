package infrastructure;

import com.google.inject.assistedinject.Assisted;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import infrastructure.redis.RedisActorProvider;

import javax.inject.Inject;
import java.io.IOException;

import static play.Logger.debug;

public class Consumer extends DefaultConsumer {
    private final Channel channel;
    private final RedisActorProvider actorProvider;

    @Inject
    public Consumer(@Assisted Channel channel, RedisActorProvider actorProvider) {
        super(channel);
        this.channel = channel;
        this.actorProvider = actorProvider;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
        final String bodyText = new String(body);

        debug("Received message: " + bodyText);
        actorProvider.get().tell(bodyText, null);
        channel.basicAck(envelope.getDeliveryTag(), false);
    }
}
