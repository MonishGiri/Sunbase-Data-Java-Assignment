package com.customer.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.customer.model.Customer;

@Repository
public interface CustomerDao extends JpaRepository<Customer,String>{
	
	@Query("SELECT c from Customer c where c.firstName LIKE %?1%")
	public List<Customer> findAllByKeyword(String keyword);
}
