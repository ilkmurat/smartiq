package com.murat.smartiq.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.murat.smartiq.model.OrderItem;

public interface IOrderRepository extends MongoRepository<OrderItem, String> {
	
    @Query("{order_id : ?0}") 
    List<OrderItem> getOrderItemsByOrderId(int orderId);

}
