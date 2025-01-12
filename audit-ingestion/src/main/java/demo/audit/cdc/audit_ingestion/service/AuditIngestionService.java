package demo.audit.cdc.audit_ingestion.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.audit.cdc.audit_ingestion.model.AuditTrail;
import demo.audit.cdc.audit_ingestion.repository.AuditTrailRepository;
import demo.audit.cdc.model.UniformAuditTrail;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    public AuditTrail saveAuditTrail(UniformAuditTrail auditTrail) {

        AuditTrail entity = new AuditTrail();
        entity.setAggregateId(auditTrail.getAggregateId());
        entity.setAggregateType(auditTrail.getAggregateType().toString());
        entity.setOldValue(auditTrail.getOldValue() != null ? auditTrail.getOldValue().toString() : null);
        entity.setNewValue(auditTrail.getNewValue() != null ? auditTrail.getNewValue().toString() : null);
        entity.setCorrelationId(auditTrail.getCorrelationId() != null ? auditTrail.getCorrelationId().toString() : null);
        entity.setOpCode(auditTrail.getOperation().toString());
        entity.setTableName(auditTrail.getTableName().toString());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime localDateTime = LocalDateTime.parse(auditTrail.getAuditDate().toString(), formatter);
        entity.setAuditDate(Timestamp.valueOf(localDateTime));
        entity.setModifiedBy(auditTrail.getModifiedBy().toString());
        entity.setDeduplicationId(auditTrail.getDeduplicationId().toString());

        try {
            return auditTrailRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate entry for correlation_id: {} and deduplication_id: {}", entity.getCorrelationId(), entity.getDeduplicationId());
            return null;
        }
    }
}