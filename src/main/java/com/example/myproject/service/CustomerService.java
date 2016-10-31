package com.example.myproject.service;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myproject.DBEntities.DBCustomer;


public interface CustomerService {

	DBCustomer save(DBCustomer customer);
}
