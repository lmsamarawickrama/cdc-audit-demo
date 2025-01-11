package demo.audit.cdc.customer.service;

import demo.audit.cdc.customer.model.Address;
import demo.audit.cdc.customer.model.AddressType;
import demo.audit.cdc.customer.model.Customer;
import demo.audit.cdc.customer.model.RecordStatus;
import demo.audit.cdc.customer.repository.AddressRepository;
import demo.audit.cdc.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    public Customer createCustomer(Customer customer) {
        String correlationId = UUID.randomUUID().toString();
        customer.setRecordStatus(RecordStatus.ACTIVE);
        customer.setCorrelationId(correlationId);
        customer.getAddresses().forEach(address -> {
            address.setCorrelationId(correlationId);
            address.setCustomer(customer);
        });
        return customerRepository.save(customer);
    }

    public Customer updateCustomerPhone(Long customerId, String phone) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setPhone(phone);
            customer.setCorrelationId(UUID.randomUUID().toString());
            return customerRepository.save(customer);
        }
        return null;
    }

    @Transactional
    public void transferDeliveryAddress(Long fromCustomerId, Long toCustomerId) {
        Optional<Customer> fromCustomerOpt = customerRepository.findById(fromCustomerId);
        Optional<Customer> toCustomerOpt = customerRepository.findById(toCustomerId);

        if (fromCustomerOpt.isPresent() && toCustomerOpt.isPresent()) {
            Customer fromCustomer = fromCustomerOpt.get();
            Customer toCustomer = toCustomerOpt.get();

            // Find the delivery address for the fromCustomer
            Optional<Address> addressOpt = fromCustomer.getAddresses().stream()
                    .filter(address -> AddressType.DELIVERY.equals(address.getAddressType()) && RecordStatus.ACTIVE.equals(address.getRecordStatus()))
                    .findFirst();

            if (addressOpt.isPresent()) {
                Address address = addressOpt.get();

                // Soft delete the old address
                address.setRecordStatus(RecordStatus.DELETED);
                address.setCorrelationId(UUID.randomUUID().toString());
                addressRepository.save(address);

                // Create a new address for the new customer
                Address newAddress = new Address();
                newAddress.setAddressType(address.getAddressType());
                newAddress.setAddressLine1(address.getAddressLine1());
                newAddress.setAddressLine2(address.getAddressLine2());
                newAddress.setCity(address.getCity());
                newAddress.setPostalCode(address.getPostalCode());
                newAddress.setCountry(address.getCountry());
                newAddress.setRecordStatus(RecordStatus.ACTIVE);
                newAddress.setCorrelationId(UUID.randomUUID().toString());
                newAddress.setModifiedBy(address.getModifiedBy());
                newAddress.setCustomer(toCustomer);
                addressRepository.save(newAddress);
            }
        }
    }

    public Address updateAddress(Address address) {
        address.setCorrelationId(UUID.randomUUID().toString());
        return addressRepository.save(address);
    }

    public void deleteCustomer(Long customerId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setRecordStatus(RecordStatus.DELETED);
            customer.setCorrelationId(UUID.randomUUID().toString());
            customerRepository.save(customer);
        }
    }

    public Address addAddress(Long customerId, Address address) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            address.setCustomer(customer);
            address.setCorrelationId(UUID.randomUUID().toString());
            return addressRepository.save(address);
        }
        return null;
    }

    public void deleteAddress(Long addressId) {
        Optional<Address> addressOpt = addressRepository.findById(addressId);
        if (addressOpt.isPresent()) {
            Address address = addressOpt.get();
            address.setRecordStatus(RecordStatus.DELETED);
            address.setCorrelationId(UUID.randomUUID().toString());
            addressRepository.save(address);
        }
    }
}
