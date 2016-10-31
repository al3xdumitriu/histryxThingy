package com.example.myproject.rest;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myproject.DBEntities.DBCustomer;
import com.example.myproject.DBEntities.QDBCustomer;
import com.example.myproject.service.CustomerService;
import com.example.myproject.utils.ResourceNotFound;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mysema.query.jpa.impl.JPAQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/customer")
@ComponentScan("com.example.myproject")
public class CustomerRest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	EntityManager entitymanager;

	@Autowired
	CustomerService customerService;

	LoadingCache<Long, DBCustomer> customerCache = CacheBuilder.newBuilder().maximumSize(100) // maximum
																								// 100
																								// records
																								// can
																								// be
																								// cached
			.expireAfterAccess(30, TimeUnit.MINUTES) // cache will expire after
														// 30 minutes of access
			.build(new CacheLoader<Long, DBCustomer>() {
				@Override
				public DBCustomer load(Long empId) throws Exception {
					// make the expensive call
					return getFromDatabase(empId);
				}
			});

	private DBCustomer getFromDatabase(Long custId) {
		logger.debug("From Database!");
		QDBCustomer qCustomer = QDBCustomer.dBCustomer;
		JPAQuery query = new JPAQuery(entitymanager);
		DBCustomer dbCust = query.from(qCustomer).where(qCustomer.id.eq(custId)).uniqueResult(qCustomer);
		return dbCust;
	}

	@RequestMapping("/{id}")
	DBCustomer getCustomerById(@PathVariable("id") Long id) {
		DBCustomer dbCust;
		try {
			dbCust = customerCache.get(id);
		} catch (Exception e) {
			throw new ResourceNotFound();
		}

		return dbCust;

	}

	@RequestMapping("/")
	List<DBCustomer> getAllCustomers() {
		QDBCustomer qCustomer = QDBCustomer.dBCustomer;
		JPAQuery query = new JPAQuery(entitymanager);
		List<DBCustomer> dbCust = query.from(qCustomer).list(qCustomer);
		return dbCust;

	}

	@RequestMapping("/{firstName}/{lastName}")
	DBCustomer saveCustomer(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName) {
		return customerService.save(new DBCustomer(firstName, lastName));
	}
}
