package demo.audit.cdc.ingestion.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import demo.audit.cdc.model.UniformAuditTrail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private final ConsumerFactory<String, UniformAuditTrail> consumerFactory;

    public KafkaConsumerConfig(ConsumerFactory<String, UniformAuditTrail> consumerFactory) {
        this.consumerFactory = consumerFactory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UniformAuditTrail> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UniformAuditTrail> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}