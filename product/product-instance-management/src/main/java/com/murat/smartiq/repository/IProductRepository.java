package com.murat.smartiq.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.murat.smartiq.model.Product;

public interface IProductRepository extends MongoRepository<Product, String> {

	@Query("{category_id : ?0}")
	List<Product> getProductsByCategoryId(String categoryId);

}
