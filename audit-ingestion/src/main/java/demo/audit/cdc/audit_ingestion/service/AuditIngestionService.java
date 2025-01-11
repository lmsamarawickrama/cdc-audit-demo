package demo.audit.cdc.audit_ingestion.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.audit.cdc.audit_ingestion.model.AuditTrail;
import demo.audit.cdc.audit_ingestion.repository.AuditTrailRepository;
import demo.audit.cdc.model.UniformAuditTrail;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@Transactional
public class AuditIngestionService {

    @Autowired
    private AuditTrailRepository auditTrailRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public void saveAuditTrail(AuditTrail auditTrail) {
        auditTrailRepository.save(auditTrail);
    }

    public AuditTrail saveAuditTrail(UniformAuditTrail auditTrail) {
        AuditTrail entity = new AuditTrail();
        entity.setAggregateId(auditTrail.getAggregateId());
        entity.setAggregateType(auditTrail.getAggregateType().toString());
        try {
            entity.setOldValue(auditTrail.getOldValue() != null ? objectMapper.writeValueAsString(auditTrail.getOldValue()) : null);
            entity.setNewValue(auditTrail.getNewValue() != null ? objectMapper.writeValueAsString(auditTrail.getNewValue()) : null);
        } catch (JsonProcessingException e) {
            log.error("Error serializing JSON", e);
            throw new RuntimeException("Error serializing JSON", e);
        }
        entity.setCorrelationId(auditTrail.getCorrelationId() != null ? auditTrail.getCorrelationId().toString() : null);
        entity.setOpCode(auditTrail.getOperation().toString());
        entity.setTableName(auditTrail.getTableName().toString());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime localDateTime = LocalDateTime.parse(auditTrail.getAuditDate().toString(), formatter);
        entity.setAuditDate(Timestamp.valueOf(localDateTime));

        entity.setDeduplicationId(auditTrail.getDeduplicationId().toString());

        return auditTrailRepository.save(entity);
    }
}