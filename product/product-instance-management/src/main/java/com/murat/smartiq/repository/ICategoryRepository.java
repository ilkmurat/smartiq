package com.murat.smartiq.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.murat.smartiq.model.Category;

public interface ICategoryRepository extends MongoRepository<Category, String> {

    @Query("{short_code : ?0}") 
    List<Category> getCategoryByShortCode(String shortCode);
}
