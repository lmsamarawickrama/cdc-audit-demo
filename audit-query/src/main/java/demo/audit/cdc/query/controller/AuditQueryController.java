package demo.audit.cdc.query.controller;

import demo.audit.cdc.query.model.AuditTrail;
import demo.audit.cdc.query.service.AuditQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audit-trail")
public class AuditQueryController {

    @Autowired
    private AuditQueryService auditQueryService;

    @GetMapping
    public Page<AuditTrail> getAuditTrails(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        return auditQueryService.getAuditTrails(page, size);
    }

    @GetMapping("/{aggregateType}/{aggregateId}")
    public Page<AuditTrail> getAuditTrailsByAggregate(@PathVariable String aggregateType,
                                                      @PathVariable Long aggregateId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "20") int size) {
        return auditQueryService.getAuditTrailsByAggregate(aggregateType, aggregateId, page, size);
    }
}
