package hello;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

    public static final String EXCHANGE_NAME = "data.incoming.x";
    public static final String DEAD_LETTER_EXCHANGE_NAME = "data.dl.x";
    public static final String ROUTING_KEY_NAME = "foo.bar.#";
    public static final String INCOMING_QUEUE_NAME = "data.incoming.q";
    public static final String DEAD_LETTER_QUEUE_NAME = "data.dl.q";
    public static final String PARKING_LOT_QUEUE_NAME = "data.parking.q";
    public static final long DELAYED_RETRY_TIME = 30000;
    public static final int MAX_RETRY_ATTEMPTS = 3;

    @Bean
    Queue queue() {
        return QueueBuilder.nonDurable(INCOMING_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE_NAME)
                .build();
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME)
                .withArgument("x-message-ttl", DELAYED_RETRY_TIME)
                .build();
    }

    @Bean
    Queue parkingLotQueue() {
        return QueueBuilder.durable(PARKING_LOT_QUEUE_NAME)
                .build();
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME, true, true);
    }

    @Bean
    TopicExchange deadLetterExchange() {
        return new TopicExchange(DEAD_LETTER_EXCHANGE_NAME, true, true);
    }

    @Bean
    Binding binding(@Qualifier("queue") Queue queue, @Qualifier("exchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_NAME);
    }

    @Bean
    Binding deadLetterBinding(@Qualifier("deadLetterQueue") Queue queue, @Qualifier("deadLetterExchange") TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange).with(ROUTING_KEY_NAME);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory myRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // remove comment to entirely config
//        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
