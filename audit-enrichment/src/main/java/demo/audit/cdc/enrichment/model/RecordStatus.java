package demo.audit.cdc.enrichment.model;

public enum RecordStatus {
    ACTIVE("ACTIVE"),
    DELETED("DELETED");

    private final String code;

    RecordStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
