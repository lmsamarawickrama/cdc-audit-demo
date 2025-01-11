package demo.audit.cdc.customer.repository;


import demo.audit.cdc.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
