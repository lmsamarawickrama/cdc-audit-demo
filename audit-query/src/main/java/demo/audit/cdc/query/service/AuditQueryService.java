package demo.audit.cdc.query.service;

import demo.audit.cdc.query.model.AuditTrail;
import demo.audit.cdc.query.repository.AuditTrailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuditQueryService {

    @Autowired
    private AuditTrailRepository auditTrailRepository;

    public Page<AuditTrail> getAuditTrails(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditTrailRepository.findAll(pageable);
    }

    public Page<AuditTrail> getAuditTrailsByAggregate(String aggregateType, Long aggregateId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditTrailRepository.findByAggregateTypeAndAggregateId(aggregateType, aggregateId, pageable);
    }
}