package infrastructure;

import akka.actor.ActorRef;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import play.Logger;

import java.io.IOException;

public class Consumer extends DefaultConsumer {
    private final ActorRef actor;
    private final Channel channel;

    public Consumer(Channel channel, ActorRef actor) {
        super(channel);
        this.channel = channel;
        this.actor = actor;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
        final String bodyText = new String(body);

        Logger.debug("Received message: " + bodyText);
        actor.tell(bodyText, null);
        channel.basicAck(envelope.getDeliveryTag(), false);
    }
}
