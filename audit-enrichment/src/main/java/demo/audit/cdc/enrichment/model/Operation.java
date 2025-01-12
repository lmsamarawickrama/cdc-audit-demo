package demo.audit.cdc.enrichment.model;

public enum Operation {
    CREATION,
    UPDATE,
    DELETION,
    INVALID;
}