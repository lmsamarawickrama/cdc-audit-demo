package demo.audit.cdc.audit_enrichment.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.audit.cdc.audit_enrichment.model.UniformAuditTrail;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class UniformAuditTrailDeserializer implements Deserializer<UniformAuditTrail> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No configuration needed
    }

    @Override
    public UniformAuditTrail deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, UniformAuditTrail.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing UniformAuditTrail", e);
        }
    }

    @Override
    public void close() {
        // No resources to close
    }
}