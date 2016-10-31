package com.example.myproject.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myproject.DBEntities.DBCustomer;
import com.example.myproject.repositories.CustomerRepository;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService{

	
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Override
	public DBCustomer save(DBCustomer customer) {
		return customerRepository.save(customer);
	}
	

}
