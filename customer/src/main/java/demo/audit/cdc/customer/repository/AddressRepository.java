package demo.audit.cdc.customer.repository;


import demo.audit.cdc.customer.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}


