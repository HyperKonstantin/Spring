package sc.springProject.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message){
        kafkaTemplate.send("user", message);
    }

    public void sendTransactionalMessage(String message){
        kafkaTemplate.send("transaction", message);
    }

    public void sendUsersToBatchConsume(String message){
        kafkaTemplate.send("batch", message);
    }
}
