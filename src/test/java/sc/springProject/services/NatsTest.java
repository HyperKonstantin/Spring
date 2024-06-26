package sc.springProject.services;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.Subscription;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class NatsTest {

    @Test
    public void batsConnectionTest() throws IOException, InterruptedException {
        Connection natsConnection = Nats.connect("192.168.246.64:4222");
        Subscription subscription1 = natsConnection.subscribe("test");

        natsConnection.publish("test", "HelloFromNuts!!!".getBytes());
        Message message = subscription1.nextMessage(10_000);
        //subscription.unsubscribe();

        Assertions.assertNotNull(message);
        Assertions.assertEquals("HelloFromNuts!!!", new String(message.getData()));
    }
}
