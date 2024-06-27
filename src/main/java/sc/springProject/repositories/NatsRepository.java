package sc.springProject.repositories;

import io.nats.client.*;
import io.nats.client.impl.NatsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NatsRepository {
    private final String NATS_SUBJECT = "info.response";

    private Connection natsConnection;
    private Subscription subscription;

    public NatsRepository(Environment environment) {
        try {
            natsConnection = Nats.connect(environment.getProperty("values.nats.host"));
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
