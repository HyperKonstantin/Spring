package sc.springProject.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic newTopic(){
        return new NewTopic("user", 1, (short) 1);
    }

    @Bean
    public NewTopic transactionsTopic() {
        return new NewTopic("transaction", 3, (short) 1);
    }
}
