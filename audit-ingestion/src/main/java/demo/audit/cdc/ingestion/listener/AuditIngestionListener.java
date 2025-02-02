package demo.audit.cdc.ingestion.listener;

import demo.audit.cdc.ingestion.service.AuditIngestionService;
import demo.audit.cdc.model.UniformAuditTrail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuditIngestionListener {

    @Autowired
    private AuditIngestionService auditIngestionService;

    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(UniformAuditTrail auditTrail) {
        try {
            auditIngestionService.saveAuditTrail(auditTrail);
        } catch (Exception e) {
            log.error("Error processing audit trail message", e);
        }
    }
}