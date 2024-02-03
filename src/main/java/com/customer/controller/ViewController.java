package com.customer.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import com.customer.APICaller;
import com.customer.CustomerListFetcher;
import com.customer.model.Customer;
import com.customer.service.CustomerService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewController {
	
	@Autowired
	CustomerService customerService;
	
	@GetMapping("/")
	public String signin() {
        return "signin";
	}
	
	@GetMapping("/addCustomer")
	public String addCustomer() {
		return "addCustomer";
	}
	
	@GetMapping("/editCustomer")
	public String editCustomer() {
		return "editCustomer";
	}
	
	@PostMapping("/addNewCustomer")
	public String addNewCustomer(@ModelAttribute Customer customer, HttpSession session) {
		System.out.println(customer);
		UUID uuid=UUID.randomUUID();
		customer.setUuid(uuid.toString());
		customerService.addCustomer(customer);
		session.setAttribute("message", "Customer Added Successfully");
		return "addCustomer";
	}
	
	@PostMapping("/login")
	public String login(@RequestParam String login_id, @RequestParam String password, Model model)
	{	
		 int apiResponse = APICaller.callApi(login_id, password);
		 
		 if(apiResponse == 200) {
			 List<Customer> listCustomer = customerService.getListofAllCustomers();
			 model.addAttribute("customerList",listCustomer);
			 return "index";
		 }
		 else {
			 System.out.println("Access denied");
			 return "signin";
		 }
	}
	
	@GetMapping("/login")
	public String handleGetRequest(Model model) {
		List<Customer> listCustomer = customerService.getListofAllCustomers();
		 model.addAttribute("customerList",listCustomer);
		 return "index";
	}
	
	@GetMapping("/sync")
	public String syncCustomer() {
		// Prepare headers with Authorization token
        HttpHeaders headers = new HttpHeaders();
        String token = "dGVzdEBzdW5iYXNlZGF0YS5jb206VGVzdEAxMjM=";
        headers.set("Authorization", "Bearer " + token);
        
        // Prepare the HTTP request entity
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        // Define the remote API URL
        String apiUrl = "https://qa.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list";

        // Make the GET request to the remote API
        ResponseEntity<Customer[]> responseEntity = new RestTemplate().exchange(
            apiUrl, 
            HttpMethod.GET, 
            entity, 
            Customer[].class
        );
        // Check if the response is successful (HTTP status code 200)
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // Retrieve customer data from the response body
            Customer[] remoteCustomers = responseEntity.getBody();
            // Process each customer
            for (Customer remoteCustomer : remoteCustomers) {
                // Check if the customer already exists in the local database
                Optional<Customer> existingCustomerOptional = customerService.getCustomerById(remoteCustomer.getUuid());
                System.out.println(customerService.getCustomerById(remoteCustomer.getUuid()));
                
                if (existingCustomerOptional.isPresent()) {
                    Customer existingCustomer = existingCustomerOptional.get();
                    // Update existing customer details
                    existingCustomer.setFirstName(remoteCustomer.getFirstName());
                    existingCustomer.setLastName(remoteCustomer.getLastName());
                    existingCustomer.setStreet(remoteCustomer.getStreet());
                    existingCustomer.setAddress(remoteCustomer.getAddress());
                    existingCustomer.setCity(remoteCustomer.getCity());
                    existingCustomer.setState(remoteCustomer.getState());
                    existingCustomer.setPhone(remoteCustomer.getPhone());

                    // Save the updated customer
                    customerService.saveOrUpdateCustomer(existingCustomer);
                } else {
                    // Save new customer
                    customerService.saveOrUpdateCustomer(remoteCustomer);
                }
            }
            
            // Return success response
           System.out.println(ResponseEntity.ok("Customer sync successful")); 
        } else {
            // Return error response if API call fails
            System.out.println(ResponseEntity.status(responseEntity.getStatusCode()).body("Failed to sync customers")); 
        }
		return "redirect:/login";
	}
	
	@GetMapping("deletecustomer/{id}")
	public String deleteCustomer(@PathVariable String id, HttpSession session){
		 customerService.deleteCustomer(id);
		 session.setAttribute("message", "Customer Deleted");
		 return "redirect:/login";
	}
	
	@GetMapping("/edit/{id}")
	public String editCustomer(@PathVariable String id, Model model) {
		try {
			Customer customer = customerService.getCustomerById(id).get();
			
			Customer customerDtl = new Customer();
			customerDtl.setUuid(id);
			customerDtl.setFirstName(customer.getFirstName());
			customerDtl.setLastName(customer.getLastName());
			customerDtl.setAddress(customer.getAddress());
			customerDtl.setCity(customer.getCity());
			customerDtl.setEmail(customer.getEmail());
			customerDtl.setPhone(customer.getPhone());
			customerDtl.setState(customer.getState());
			customerDtl.setStreet(customer.getStreet());
			
			model.addAttribute("customerDtl",customerDtl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "editCustomer";
	}
	
	@PostMapping("/edit")
	public String saveEditedCustomer(@ModelAttribute Customer edittedCustomer, Model model, HttpSession session) {
		System.out.println("--------Hi");
		System.out.println(edittedCustomer);
		Customer updatedcustomer = customerService.getCustomerById(edittedCustomer.getUuid()).get();
		
		
		updatedcustomer.setAddress(edittedCustomer.getAddress());
		updatedcustomer.setCity(edittedCustomer.getCity());
		updatedcustomer.setEmail(edittedCustomer.getEmail());
		updatedcustomer.setFirstName(edittedCustomer.getFirstName());
		updatedcustomer.setLastName(edittedCustomer.getLastName());
		updatedcustomer.setPhone(edittedCustomer.getPhone());
		updatedcustomer.setState(edittedCustomer.getState());
		updatedcustomer.setStreet(edittedCustomer.getStreet());
		
		customerService.saveOrUpdateCustomer(updatedcustomer);
		
		session.setAttribute("message", "Customer Updated Sucessfully");
		
		
		return "redirect:/login";
	}
	
	
	
}
