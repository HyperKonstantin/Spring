package sc.springProject.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sc.springProject.entities.UserView;

@Service
@RequiredArgsConstructor
public class KafkaObjectProducer {

    private final KafkaTemplate<String, UserView> kafkaTemplate;

    @Transactional
    public void sendMessage(UserView user){
        kafkaTemplate.send("user", user);
    }

    public void sendTransactionalMessage(UserView user){
        kafkaTemplate.send("transaction", user);
    }

    @Transactional
    public void sendUsersToBatchConsume(UserView user){
        kafkaTemplate.send("batch", user);
    }
}
