package demo.audit.cdc.ingestion.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "audit_trail")
@Data
public class AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_id", nullable = false)
    private Long aggregateId;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "object_id", nullable = false)
    private Long objectId;

    @Column(name = "old_value", columnDefinition = "JSON")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "JSON")
    private String newValue;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "op_code", nullable = false)
    private String opCode;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "audit_date", nullable = false)
    private Timestamp auditDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "deduplication_id")
    private String deduplicationId;

}