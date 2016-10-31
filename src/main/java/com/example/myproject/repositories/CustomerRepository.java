package com.example.myproject.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.example.myproject.DBEntities.DBCustomer;

@Repository
public interface CustomerRepository extends JpaRepository<DBCustomer, Long> {
    List<DBCustomer> findByLastName(String lastName);
}