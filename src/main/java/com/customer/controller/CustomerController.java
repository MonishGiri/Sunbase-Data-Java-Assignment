package com.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.customer.service.CustomerService;
import com.customer.model.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	CustomerService customerService;
	
	
	@GetMapping("/allCustomers")
	public ResponseEntity<Page<Customer>> getAllCustomers(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uuid") String sortBy, Model model){
		
		Page<Customer> customers = customerService.getAllCustomers(page, size, sortBy);
		List<Customer> customerList = customers.getContent();
		model.addAttribute("customers",customerList);
		return ResponseEntity.ok(customers);
	}
	
	@GetMapping("id/{id}")
	public Optional<Customer> getCustomerById(@PathVariable String id) {
		return customerService.getCustomerById(id);
	}
	
	@PostMapping("addcustomer")
	public String addCustomer(@RequestBody Customer customer) {
		return customerService.addCustomer(customer);
	}
	
	@PutMapping("updatecustomer/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable String id,
            @RequestBody Customer updatedCustomer) {
        
        Customer customer = customerService.updateCustomer(id, updatedCustomer);
        return ResponseEntity.ok(customer);
    }
	
	@DeleteMapping("deletecustomer/{id}")
	public String deleteCustomer(@PathVariable String id){
		 customerService.deleteCustomer(id);
		 return "redirect:/login";
	}
}
