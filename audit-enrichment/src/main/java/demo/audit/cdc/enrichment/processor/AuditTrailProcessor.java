package demo.audit.cdc.enrichment.processor;

import demo.audit.cdc.enrichment.model.AggregateType;
import demo.audit.cdc.enrichment.service.AuditTrailService;
import demo.audit.cdc.model.UniformAuditTrail;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class AuditTrailProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AuditTrailProcessor.class);

    @Autowired
    private AuditTrailService auditTrailService;

    @Bean
    public Function<KStream<String, app_main.APP_MAIN.customer.Envelope>, KStream<String, UniformAuditTrail>> processCustomer() {
        return input -> input.mapValues(value -> {
            try {
                // Convert CustomerEnvelope to Map<String, Object> if necessary
                Map<String, Object> valueMap = convertCustomerEnvelopeToMap(value);
                return auditTrailService.createUniformAuditTrail(valueMap, AggregateType.CUSTOMER);
            } catch (Exception e) {
                logger.error("Error processing customer message", e);
                return null;
            }
        });
    }

    @Bean
    public Function<KStream<String, app_main.APP_MAIN.address.Envelope>, KStream<String, UniformAuditTrail>> processAddress() {
        return input -> input.mapValues(value -> {
            try {
                // Convert AddressEnvelope to Map<String, Object> if necessary
                Map<String, Object> valueMap = convertAddressEnvelopeToMap(value);
                return auditTrailService.createUniformAuditTrail(valueMap, AggregateType.CUSTOMER);
            } catch (Exception e) {
                logger.error("Error processing address message", e);
                return null;
            }
        });
    }

    private Map<String, Object> convertCustomerEnvelopeToMap(app_main.APP_MAIN.customer.Envelope envelope) {
        Map<String, Object> map = new HashMap<>();
        map.put("before", envelope.getBefore() != null ? specificRecordToMap(envelope.getBefore()) : null);
        map.put("after", envelope.getAfter() != null ? specificRecordToMap(envelope.getAfter()) : null);
        map.put("source", envelope.getSource().getName());
        map.put("table", envelope.getSource().getTable());
        map.put("op", envelope.getOp());
        map.put("ts_ms", envelope.getTsMs());
        map.put("transaction", envelope.getTransaction() != null ? envelope.getTransaction().getId() : null);
        return map;
    }

    private Map<String, Object> convertAddressEnvelopeToMap(app_main.APP_MAIN.address.Envelope envelope) {
        Map<String, Object> map = new HashMap<>();
        map.put("before", envelope.getBefore() != null ? specificRecordToMap(envelope.getBefore()) : null);
        map.put("after", envelope.getAfter() != null ? specificRecordToMap(envelope.getAfter()) : null);
        map.put("source", envelope.getSource().getName());
        map.put("table", envelope.getSource().getTable());
        map.put("op", envelope.getOp());
        map.put("ts_ms", envelope.getTsMs());
        map.put("transaction", envelope.getTransaction() != null ? envelope.getTransaction().getId() : null);
        return map;
    }

    private Map<String, Object> specificRecordToMap(SpecificRecordBase record) {
        Map<String, Object> map = new HashMap<>();
        for (org.apache.avro.Schema.Field field : record.getSchema().getFields()) {
            Object value = record.get(field.pos());
            if (value instanceof org.apache.avro.util.Utf8) {
                value = value.toString();
            }
            map.put(field.name(), value);
        }
        return map;
    }
}
