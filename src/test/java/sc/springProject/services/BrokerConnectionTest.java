package sc.springProject.services;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.Subscription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sc.springProject.kafka.KafkaMessageProducer;

import java.io.IOException;

@SpringBootTest
public class BrokerConnectionTest {

    @Autowired
    private KafkaMessageProducer kafkaMessageProducer;

    @Test
    public void natsConnectionTest() throws IOException, InterruptedException {
        Connection natsConnection = Nats.connect("192.168.246.64:4222");
        Subscription subscription1 = natsConnection.subscribe("test");

        natsConnection.publish("test", "HelloFromNuts!!!".getBytes());
        Message message = subscription1.nextMessage(10_000);
        //subscription.unsubscribe();

        Assertions.assertNotNull(message);
        Assertions.assertEquals("HelloFromNuts!!!", new String(message.getData()));
    }

    @Test
    public void kafkaConnectionTest(){
        kafkaMessageProducer.sendMessage("message","Connection Test");
    }

}
