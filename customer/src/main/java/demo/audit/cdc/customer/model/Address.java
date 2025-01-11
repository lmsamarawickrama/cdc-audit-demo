package demo.audit.cdc.customer.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;

@Entity
@Data
@SQLDelete(sql = "UPDATE address SET record_status = 'Deleted', modified_date = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "record_status = 'ACTIVE'")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postalCode;
    private String country;
    private String correlationId;
    private String modifiedBy;

    @Enumerated(EnumType.STRING)
    private RecordStatus recordStatus = RecordStatus.ACTIVE;
    @Version
    private Timestamp modifiedDate;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;
}
