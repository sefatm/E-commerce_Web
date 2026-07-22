package com.mgt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

import java.util.List;

@Entity
@Table(name="product_category")
public class ProductCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@JsonIgnore
	@OneToMany(mappedBy = "category")
	private List<AddProduct> products;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<AddProduct> getProducts() {
		return products;
	}

	public void setProducts(List<AddProduct> products) {
		this.products = products;
	}
}
