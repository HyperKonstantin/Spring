package sc.springProject.repositories;

import io.nats.client.*;
import io.nats.client.impl.NatsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NatsRepository {
    private final String NATS_SUBJECT = "info.response";

    private Connection natsConnection;
    private Subscription subscription;

    public NatsRepository() {
        try {
            natsConnection = Nats.connect("192.168.246.64:4222");
        } catch (Exception e) {
            log.error("--- Не удалось подключиться к NATS-серверу ---");
            throw new RuntimeException(e);
        }

        subscription = natsConnection.subscribe(NATS_SUBJECT);
    }

    public Message getResponce(){
        try {
            return subscription.nextMessage(1_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String data, String topic) {
        Message message = NatsMessage.builder()
                .data(data.getBytes())
                .subject(topic)
                .replyTo(NATS_SUBJECT)
                .build();

        natsConnection.publish(message);
    }
}
