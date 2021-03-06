package com.murat.smartiq.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.murat.smartiq.model.OrderItem;

@RequestMapping("/order")
public interface IOrderService {

	@RequestMapping(value = "/placeOrder", method = RequestMethod.POST)
	public ResponseEntity<Void> placeOrder(@RequestBody @Valid List<OrderItem> itemList);

	@RequestMapping(value = "/updateAddress", method = RequestMethod.POST)
	public ResponseEntity<Void> updateAddress(@RequestBody OrderItem item);

	@GetMapping(value = "/cancelOrder/{orderId}")
	public ResponseEntity<Void> cancelOrder(@PathVariable("orderId") Integer orderId);

	@GetMapping("/allOrders")
	public List<OrderItem> allOrders();

	@GetMapping(value = "/getOrderItemsByOrderId/{orderId}")
	public List<OrderItem> getOrderItemsByOrderId(@PathVariable("orderId") Integer orderId);

	@GetMapping(value = "/successCallback/{orderId}")
	public void successCallback(@PathVariable("orderId") Integer orderId);

	@GetMapping(value = "/faultCallback/{orderId}")
	public void faultCallback(@PathVariable("orderId") Integer orderId);

}
