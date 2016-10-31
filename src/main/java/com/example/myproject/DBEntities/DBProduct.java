package com.example.myproject.DBEntities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "product")
public class DBProduct {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="product_id")
	private Long id;
	private String productName;
	
	@ManyToOne
	private DBCustomer dbCustomer;

	protected DBProduct() {
	}

	public DBProduct(String productName) {
		this.productName = productName;
	}

	@Override
	public String toString() {
		return String.format("Customer[id=%d, productName='%s']", id, productName);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public DBCustomer getDbCustomer() {
		return dbCustomer;
	}

	public void setDbCustomer(DBCustomer dbCustomer) {
		this.dbCustomer = dbCustomer;
	}
	
	
}
