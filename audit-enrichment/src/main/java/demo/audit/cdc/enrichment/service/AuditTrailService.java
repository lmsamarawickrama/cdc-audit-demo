package demo.audit.cdc.enrichment.service;

import demo.audit.cdc.enrichment.model.AggregateType;
import demo.audit.cdc.enrichment.model.Operation;
import demo.audit.cdc.enrichment.model.RecordStatus;
import demo.audit.cdc.model.UniformAuditTrail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuditTrailService {

    private static final Logger log = LoggerFactory.getLogger(AuditTrailService.class);

    private static final List<String> TECHNICAL_COLUMNS = List.of("modified_by", "correlation_id", "created_date", "modified_date", "record_status");

    @SuppressWarnings("unchecked")
    public UniformAuditTrail createUniformAuditTrail(Map<String, Object> valueMap, AggregateType aggregateType) {
        UniformAuditTrail auditTrail = new UniformAuditTrail();
        Map<String, Object> before = (Map<String, Object>) valueMap.get("before");
        Map<String, Object> after = (Map<String, Object>) valueMap.get("after");
        String operationCode = convertToString(valueMap.get("op"));
        Operation operation = determineOperation(operationCode, after, before);
        auditTrail.setAggregateType(aggregateType.name());
        auditTrail.setAggregateId((Long) (after != null ? (after.get("customer_id") != null ? after.get("customer_id") : after.get("id")) : (before.get("customer_id") != null ? before.get("customer_id") : before.get("id"))));
        auditTrail.setCorrelationId(convertToString((after != null ? after.get("correlation_id") : before.get("correlation_id"))));
        auditTrail.setDeduplicationId(UUID.randomUUID().toString());

        if (Operation.INVALID.equals(operation)) {
            return auditTrail;
        }

        auditTrail.setCorrelationId(convertToString(after.get("correlation_id")));
        auditTrail.setAuditDate(convertToString(after.get("modified_date")));
        auditTrail.setModifiedBy(convertToString(after.get("modified_by")));
        auditTrail.setTableName(convertToString(valueMap.get("table")));
        auditTrail.setOperation(operation.name());

        Map<String, Object> oldValue = new HashMap<>();
        Map<String, Object> newValue = new HashMap<>();

        switch (operation) {
            case CREATION:
                oldValue = null;
                newValue = filterTechnicalColumns(after);
                break;
            case UPDATE:
                oldValue = getUpdatedColumns(before, after, true);
                newValue = getUpdatedColumns(before, after, false);
                break;
            case DELETION:
                oldValue = filterTechnicalColumns(before);
                break;
        }

        auditTrail.setOldValue(convertMapToCharSequenceMap(oldValue));
        auditTrail.setNewValue(convertMapToCharSequenceMap(newValue));

        return auditTrail;
    }

    private Map<String, Object> filterTechnicalColumns(Map<String, Object> map) {
        return map.entrySet().stream()
                .filter(entry -> !TECHNICAL_COLUMNS.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, value -> convertToString(value.getValue())));
    }

    private Operation determineOperation(String operationCode, Map<String, Object> after, Map<String, Object> before) {
        switch (operationCode) {
            case "c":
                return Operation.CREATION;
            case "u":
                if (isDeletion(before, after)) {
                    return Operation.DELETION;
                } else {
                    return Operation.UPDATE;
                }
            default:
                log.warn("unhandled opcode");
        }
        return Operation.INVALID;
    }

    private boolean isDeletion(Map<String, Object> before, Map<String, Object> after) {
        return before != null && after != null &&
                RecordStatus.ACTIVE.getCode().equals(before.get("record_status")) &&
                RecordStatus.DELETED.getCode().equals(after.get("record_status"));
    }

    private Map<String, Object> getUpdatedColumns(Map<String, Object> before, Map<String, Object> after, boolean isOldValue) {
        Map<String, Object> updatedColumns = new HashMap<>();
        for (String key : after.keySet()) {
            if (!TECHNICAL_COLUMNS.contains(key) && !Objects.equals(after.get(key), before.get(key))) {
                updatedColumns.put(key, isOldValue ? before.get(key) : after.get(key));
            }
        }
        return updatedColumns;
    }

    private String convertToString(Object obj) {
        if (obj instanceof org.apache.avro.util.Utf8) {
            return obj.toString();
        } else {
            return Objects.toString(obj);
        }
    }

    private Map<CharSequence, CharSequence> convertMapToCharSequenceMap(Map<String, Object> map) {
        Map<CharSequence, CharSequence> charSequenceMap = new HashMap<>();
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                charSequenceMap.put(entry.getKey(), convertToString(entry.getValue()));
            }
        }
        return charSequenceMap;
    }
}