package demo.audit.cdc.audit_enrichment.service;

import demo.audit.cdc.audit_enrichment.model.EntityType;
import demo.audit.cdc.audit_enrichment.model.Operation;
import demo.audit.cdc.audit_enrichment.model.UniformAuditTrail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class AuditTrailService {

    public UniformAuditTrail createUniformAuditTrail(Map<String, Object> value, EntityType entityType) {
        UniformAuditTrail auditTrail = new UniformAuditTrail();
        auditTrail.setEntityType(entityType.name());

        Map<String, Object> after = (Map<String, Object>) value.get("after");
        Map<String, Object> before = (Map<String, Object>) value.get("before");
        String operationCode = convertToString(value.get("op"));

        Operation operation = determineOperation(operationCode, after, before);
        auditTrail.setOperation(operation.name());

        if (after != null) {

            auditTrail.setEntityId((Long) after.get("id"));
            auditTrail.setCorrelationId(convertToString(after.get("correlation_id")));
            auditTrail.setAuditDate(convertToString(after.get("modified_date")));
        } else if (before != null) {
            auditTrail.setEntityId((Long) before.get("id"));
            auditTrail.setCorrelationId(convertToString(before.get("correlation_id")));
            auditTrail.setAuditDate(convertToString(before.get("modified_date")));
        }
        auditTrail.setDeduplicationId(UUID.randomUUID().toString());
        Map<String, Object> oldValue = new HashMap<>();
        Map<String, Object> newValue = new HashMap<>();

        switch (operation) {
            case CREATION:
                oldValue = null;
                newValue = after;
                break;
            case UPDATE:
                oldValue = getUpdatedColumns(before, after, true);
                newValue = getUpdatedColumns(before, after, false);
                break;
            case DELETION:
                oldValue.put("column_name", "record_status");
                oldValue.put("column_value", "Active");
                newValue.put("column_name", "record_status");
                newValue.put("column_value", "Deleted");
                break;
        }

        auditTrail.setOldValue(convertMapToCharSequenceMap(oldValue));
        auditTrail.setNewValue(convertMapToCharSequenceMap(oldValue));

        return auditTrail;
    }

    private Operation determineOperation(String operationCode, Map<String, Object> after, Map<String, Object> before) {
        switch (operationCode) {
            case "c":
                return Operation.CREATION;
            case "u":
                if (isRecordStatusChange(before, after)) {
                    return Operation.DELETION;
                } else {
                    return Operation.UPDATE;
                }
            default:
                log.warn("unhandled opcode");
        }
        return null;
    }

    private boolean isRecordStatusChange(Map<String, Object> before, Map<String, Object> after) {
        return before != null && after != null &&
                "Active".equals(before.get("record_status")) &&
                "Deleted".equals(after.get("record_status"));
    }

    private Map<String, Object> getUpdatedColumns(Map<String, Object> before, Map<String, Object> after, boolean isOldValue) {
        Map<String, Object> updatedColumns = new HashMap<>();
        for (String key : before.keySet()) {
            if (!before.get(key).equals(after.get(key))) {
                updatedColumns.put("column_name", key);
                updatedColumns.put("column_value", isOldValue ? before.get(key) : after.get(key));
            }
        }
        return updatedColumns;
    }

    private String convertToString(Object obj) {
        if (obj instanceof org.apache.avro.util.Utf8) {
            return obj.toString();
        } else if (obj instanceof String) {
            return (String) obj;
        } else {
            return null;
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