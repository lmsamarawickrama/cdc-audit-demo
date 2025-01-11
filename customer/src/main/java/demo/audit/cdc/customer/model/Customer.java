package demo.audit.cdc.customer.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@SQLDelete(sql = "UPDATE customer SET record_status = 'DELETED', modified_date = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "record_status = 'ACTIVE'")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phone;

    private String correlationId;
    private String modifiedBy;


    @Enumerated(EnumType.STRING)
    private RecordStatus recordStatus = RecordStatus.ACTIVE;

    @Version
    private Timestamp modifiedDate;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses;
}
