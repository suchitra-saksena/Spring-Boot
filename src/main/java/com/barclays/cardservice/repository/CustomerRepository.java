package com.barclays.cardservice.repository;

import org.springframework.data.repository.CrudRepository;

import com.barclays.cardservice.entity.Customer;

/**
 * CustomerRepository - Interface for customer relation in database
 */
public interface CustomerRepository extends CrudRepository<Customer, Integer> {
	
}
