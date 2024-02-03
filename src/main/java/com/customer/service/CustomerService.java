package com.customer.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.customer.dao.CustomerDao;
import com.customer.model.Customer;

import lombok.Data;

@Service
public class CustomerService {

	@Autowired
	CustomerDao customerDao;
	public Page<Customer> getAllCustomers(int page, int size, String sortBy) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		return customerDao.findAll(pageable);
	}
	
	public List<Customer> getListofAllCustomers() {
		return customerDao.findAll();
	}
	public Optional<Customer> getCustomerById(String id) {
		return customerDao.findById(id);
	}
	public String addCustomer(Customer customer) {
		customerDao.save(customer);
		return "Customer Added Successfully";
	}
	public Customer updateCustomer(String id, Customer updatedCustomer) {
		 Customer existingCustomer = customerDao.findById(id)
		            .orElseThrow();

		        // Update existing customer fields with the new values
		        existingCustomer.setFirstName(updatedCustomer.getFirstName());
		        existingCustomer.setLastName(updatedCustomer.getLastName());
		        existingCustomer.setAddress(updatedCustomer.getAddress());
		        existingCustomer.setCity(updatedCustomer.getCity());
		        existingCustomer.setEmail(updatedCustomer.getEmail());
		        existingCustomer.setPhone(updatedCustomer.getPhone());
		        existingCustomer.setState(updatedCustomer.getState());
		        existingCustomer.setStreet(updatedCustomer.getStreet());
		        
		        return customerDao.save(existingCustomer);
	}
	public String deleteCustomer(String id) {
		 customerDao.deleteById(id);
		 return "success";
	}
	
	public Customer saveOrUpdateCustomer(Customer customer) {
        return customerDao.save(customer);
    }
	
	public List<Customer> findAllByKeyword(String keyword){
		if(keyword != null) {
			return customerDao.findAll();
		}
		return customerDao.findAll();
	}
}
