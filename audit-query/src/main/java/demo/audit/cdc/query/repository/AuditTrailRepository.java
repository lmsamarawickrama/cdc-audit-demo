package demo.audit.cdc.query.repository;

import demo.audit.cdc.query.model.AuditTrail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {
    Page<AuditTrail> findByAggregateTypeAndAggregateId(String aggregateType, Long aggregateId, Pageable pageable);
}