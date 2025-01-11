package demo.audit.cdc.audit_enrichment.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.audit.cdc.audit_enrichment.model.UniformAuditTrail;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class UniformAuditTrailSerializer implements Serializer<UniformAuditTrail> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No configuration needed
    }

    @Override
    public byte[] serialize(String topic, UniformAuditTrail data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing UniformAuditTrail", e);
        }
    }

    @Override
    public void close() {
        // No resources to close
    }
}