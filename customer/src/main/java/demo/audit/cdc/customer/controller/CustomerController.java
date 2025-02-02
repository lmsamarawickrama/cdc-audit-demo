package demo.audit.cdc.customer.controller;

import demo.audit.cdc.customer.model.Address;
import demo.audit.cdc.customer.model.Customer;
import demo.audit.cdc.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @PutMapping("/{customerId}/phone")
    public void updateCustomerPhone(@PathVariable Long customerId, @RequestBody Map<String, String> requestBody) {
        String phone = requestBody.get("phone");
        customerService.updateCustomerPhone(customerId, phone);
    }

    @PutMapping("/{fromCustomerId}/transfer-delivery-address/{toCustomerId}")
    public void transferDeliveryAddress(@PathVariable Long fromCustomerId, @PathVariable Long toCustomerId) {
        customerService.transferDeliveryAddress(fromCustomerId, toCustomerId);
    }

    @PutMapping("/{customerId}/address/{addressType}")
    public void updateAddress(@PathVariable Long customerId, @PathVariable String addressType, @RequestBody Address address) {
        customerService.updateAddress(customerId, addressType, address);
    }

    @DeleteMapping("/{customerId}")
    public void deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
    }

    @PostMapping("/{customerId}/address")
    public Address addAddress(@PathVariable Long customerId, @RequestBody Address address) {
        return customerService.addAddress(customerId, address);
    }

    @DeleteMapping("/address/{addressId}")
    public void deleteAddress(@PathVariable Long addressId) {
        customerService.deleteAddress(addressId);
    }
}