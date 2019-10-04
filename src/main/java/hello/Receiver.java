package hello;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = Configuration.INCOMING_QUEUE_NAME, containerFactory = "myRabbitListenerContainerFactory")
    public void receiveMessage(String message, @Header(required = false, name = "x-death") List<Map<String, Object>> xDeath) throws InterruptedException {
        System.out.println("Received <" + message + ">");
        if (!process(message)) {
            System.out.println("Fail to process message: <" + message + ">");
        }

        if (checkIfNeedToRetry(xDeath)) {
            throw new AmqpRejectAndDontRequeueException("Failed to process message");
        } else {
            rabbitTemplate.convertAndSend(Configuration.PARKING_LOT_QUEUE_NAME, message);
        }
    }

    private boolean process(String message) {
        // TODO: Dump code to demo
        return false;
    }

    private boolean checkIfNeedToRetry(List<Map<String, Object>> xDeath) {
        Long retryCount = new Long(0);
        if (xDeath != null) {
            Optional<Long> count = xDeath.stream()
                    .flatMap(m -> m.entrySet().stream())
                    .filter(e -> e.getKey().equals("count"))
                    .findFirst().map(e -> (Long) e.getValue());
            if (count.isPresent()) {
                retryCount = count.get().longValue();
                System.out.println("Retry: " + retryCount);
            }
        }

        if (retryCount < Configuration.MAX_RETRY_ATTEMPTS) {
            return true;
        } else {
            System.out.println("exceed max retry " + retryCount + " -> send to parking lot");
            return false;
        }
    }
}
