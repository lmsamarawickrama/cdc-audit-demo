package demo.audit.cdc.audit_enrichment.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.audit.cdc.audit_enrichment.model.UniformAuditTrail;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class UniformAuditTrailSerde implements Serde<UniformAuditTrail> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Serializer<UniformAuditTrail> serializer() {
        return (topic, data) -> {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (Exception e) {
                throw new RuntimeException("Error serializing UniformAuditTrail", e);
            }
        };
    }

    @Override
    public Deserializer<UniformAuditTrail> deserializer() {
        return (topic, data) -> {
            try {
                return objectMapper.readValue(data, UniformAuditTrail.class);
            } catch (Exception e) {
                throw new RuntimeException("Error deserializing UniformAuditTrail", e);
            }
        };
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
