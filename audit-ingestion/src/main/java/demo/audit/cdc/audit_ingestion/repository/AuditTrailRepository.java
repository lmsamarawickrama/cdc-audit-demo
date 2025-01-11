package demo.audit.cdc.audit_ingestion.repository;

import demo.audit.cdc.audit_ingestion.model.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {
}