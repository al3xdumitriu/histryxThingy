package com.example.myproject.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myproject.DBEntities.DBCustomer;
import com.example.myproject.repositories.CustomerRepository;
import com.example.myproject.utils.ResourceNotFound;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	@HystrixCommand(fallbackMethod = "defaultBehaviour")
	public DBCustomer save(DBCustomer customer) {
		logger.info("DBCustomer -> save " + customer.getFirstName());
		if (customer.getFirstName().equals("timeout")) {
//			try {
//				logger.info("DBCustomer -> timeout");
//				Thread.sleep(400000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			double i=1;
			 while(i>0)
			 {
				 i++;
			 }
			logger.info("i:"+i);
		}
		return customerRepository.save(customer);
	}

	public DBCustomer defaultBehaviour(DBCustomer customer) {
		logger.info("DBCustomer -> defaultBehaviour");
		return customerRepository.save(new DBCustomer("default", "behaviour"));
	}

}
