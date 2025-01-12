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

    public void updateCustomerPhone(Long customerId, String phone) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setPhone(phone);
            customer.setCorrelationId(UUID.randomUUID().toString());
            customerRepository.save(customer);
        } else {
            throw new IllegalArgumentException("customer not found for given id: " + customerId);
        }
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

            final String correlationId = UUID.randomUUID().toString();
            if (addressOpt.isPresent()) {
                Address address = addressOpt.get();

                // Soft delete the old address
                address.setRecordStatus(RecordStatus.DELETED);
                address.setCorrelationId(correlationId);
                addressRepository.save(address);

                // Create a new address for the new customer
                Address newAddress = getAddress(address, correlationId, toCustomer);
                addressRepository.save(newAddress);
            }
        }
    }

    private static Address getAddress(Address address, String correlationId, Customer toCustomer) {
        Address newAddress = new Address();
        newAddress.setAddressType(address.getAddressType());
        newAddress.setAddressLine1(address.getAddressLine1());
        newAddress.setAddressLine2(address.getAddressLine2());
        newAddress.setCity(address.getCity());
        newAddress.setPostalCode(address.getPostalCode());
        newAddress.setCountry(address.getCountry());
        newAddress.setRecordStatus(RecordStatus.ACTIVE);
        newAddress.setCorrelationId(correlationId);
        newAddress.setModifiedBy(address.getModifiedBy());
        newAddress.setCustomer(toCustomer);
        return newAddress;
    }

    public void updateAddress(Long customerId, String addressType, Address address) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            Optional<Address> addressOpt = customer.getAddresses().stream()
                    .filter(addr -> addressType.equals(addr.getAddressType().name()) && RecordStatus.ACTIVE.equals(addr.getRecordStatus()))
                    .findFirst();

            if (addressOpt.isPresent()) {
                Address existingAddress = addressOpt.get();
                existingAddress.setAddressLine1(address.getAddressLine1());
                existingAddress.setAddressLine2(address.getAddressLine2());
                existingAddress.setCity(address.getCity());
                existingAddress.setPostalCode(address.getPostalCode());
                existingAddress.setCountry(address.getCountry());
                existingAddress.setCorrelationId(UUID.randomUUID().toString());
                addressRepository.save(existingAddress);
            }
        } else {
            throw new IllegalArgumentException("customer not found for given id: " + customerId);
        }
    }


    public void deleteCustomer(Long customerId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            String correlationId = UUID.randomUUID().toString();

            customer.getAddresses().forEach(address -> {
                address.setRecordStatus(RecordStatus.DELETED);
                address.setCorrelationId(correlationId);
                addressRepository.save(address);
            });
            customer.setRecordStatus(RecordStatus.DELETED);
            customer.setCorrelationId(correlationId);
            customerRepository.save(customer);
        } else {
            throw new IllegalArgumentException("customer not found for given id: " + customerId);
        }
    }

    public Address addAddress(Long customerId, Address address) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            address.setCustomer(customer);
            address.setCorrelationId(UUID.randomUUID().toString());
            return addressRepository.save(address);
        } else {
            throw new IllegalArgumentException("customer not found for given id: " + customerId);
        }
    }

    public void deleteAddress(Long addressId) {
        Optional<Address> addressOpt = addressRepository.findById(addressId);
        if (addressOpt.isPresent()) {
            Address address = addressOpt.get();
            address.setRecordStatus(RecordStatus.DELETED);
            address.setCorrelationId(UUID.randomUUID().toString());
            addressRepository.save(address);
        } else {
            throw new IllegalArgumentException("Address not found for given id: " + addressId);
        }
    }
}
