package com.murat.smartiq.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.validation.Valid;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.murat.smartiq.model.OrderItem;
import com.murat.smartiq.repository.IOrderRepository;
import com.murat.smartiq.service.IOrderService;

@RestController
public class OrderController implements IOrderService {

	@Autowired
	IOrderRepository orderRepo;

	private static final String serviceURL = "http://localhost:8081/product/";

	/**
	 * creating new order order id assing with random number , between 1-999999
	 */
	@Override
	public ResponseEntity placeOrder(@RequestBody @Valid List<OrderItem> itemList) {
		try {
			if (itemList.isEmpty()) {
				return new ResponseEntity<>(errorResponse("Items Empty", "Please add to cart a product!"),
						HttpStatus.NOT_FOUND);
			}
			Random random = new Random();
			int orderId = random.nextInt(999999);
			for (OrderItem item : itemList) {
				if (item.getProductId().isEmpty() || item.getTotalCount() < 1 || item.getTotalPrice() < 0.0) {
					return new ResponseEntity<>(
							errorResponse("Missing Parameter", "Required product info : id , count and price"),
							HttpStatus.METHOD_NOT_ALLOWED);
				}
				item.setOrderId(orderId);
				item.setStatus("WAITING");
				orderRepo.save(item);
			}
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(errorResponse("Service Exception", e.toString()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * update order address
	 */
	@Override
	public ResponseEntity updateAddress(OrderItem item) {
		try {
			if (item == null || item.getOrderId() <= 0 || item.getAddress() == null || item.getAddress().isEmpty()) {
				return new ResponseEntity<>(errorResponse("Item is empty", "Please provide a order id and address"),
						HttpStatus.NOT_FOUND);
			}
			List<OrderItem> orderAddress = orderRepo.getOrderItemsByOrderId(item.getOrderId());
			if (orderAddress != null && orderAddress.size() > 0) {
				for (OrderItem orderItem : orderAddress) {
					orderItem.setAddress(item.getAddress());
					orderRepo.save(orderItem);
				}
				return new ResponseEntity<Void>(HttpStatus.OK);
			}
			return new ResponseEntity<>(
					errorResponse("Parameter error", "Order id : " + item.getOrderId() + "has no address"),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {

			return new ResponseEntity<>(errorResponse("Service Exception", e.toString()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity cancelOrder(@PathVariable("orderId") Integer orderId) {
		try {
			List<OrderItem> orderItems = orderRepo.getOrderItemsByOrderId(orderId);
			if (orderItems != null && orderItems.size() > 0) {
				for (OrderItem orderItem : orderItems) {
					orderItem.setStatus("CANCELED");
					orderRepo.save(orderItem);
					// incrementStock(orderItem);
				}
				return new ResponseEntity<Void>(HttpStatus.OK);
			}
			return new ResponseEntity<>(errorResponse("No record", "There is no order with order id :" + orderId),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {

			return new ResponseEntity<>(errorResponse("Service Exception", e.toString()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public List<OrderItem> allOrders() {
		return orderRepo.findAll();
	}

	@Override
	public List<OrderItem> getOrderItemsByOrderId(@PathVariable("orderId") Integer orderId) {
		try {
			List<OrderItem> orderItems = orderRepo.getOrderItemsByOrderId(orderId);

			if (!orderItems.isEmpty()) {
				return orderItems;
			}
			return null;

		} catch (Exception e) {
			// log
		}
		return null;
	}

	/**
	 * if stock process is success that updated order status
	 * 
	 */
	@Override
	public void successCallback(@PathVariable("orderId") Integer orderId) {
		try {
			List<OrderItem> orderItems = orderRepo.getOrderItemsByOrderId(orderId);
			for (OrderItem item : orderItems) {
				item.setStatus("FINISHED");
				orderRepo.save(item);
			}
		} catch (Exception e) {
			// log
		}

	}

	/**
	 * if stock process is success that updated order status
	 * 
	 */
	@Override
	public void faultCallback(@PathVariable("orderId") Integer orderId) {
		try {
			List<OrderItem> orderItems = orderRepo.getOrderItemsByOrderId(orderId);
			for (OrderItem item : orderItems) {
				item.setStatus("CANCELLED");
				orderRepo.save(item);
			}
		} catch (Exception e) {
			// log
		}
	}

	public String incrementStock(OrderItem item) {
		try {
			JSONObject product = new JSONObject();
			product.put("productId", item.getProductId());
			product.put("count", item.getTotalCount());

			String inputJson = product.toString();
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL + "incrementProductStock"))
					.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(inputJson))
					.build();

			client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (JSONException e) {
			// log
		} catch (IOException e) {
			// log
		} catch (InterruptedException e) {
			// log
		}

		return null;
	}

	public String decrementStock(List<OrderItem> itemList) {

		try {
			List<String> orderItemsString = new ArrayList<>();

			for (OrderItem item : itemList) {
				JSONObject product = new JSONObject();
				product.put("productId", item.getProductId());
				product.put("count", item.getTotalCount());
				product.put("orderId", item.getOrderId());
				String inputJson = product.toString();
				orderItemsString.add(inputJson);
			}

			String arrayString = orderItemsString.toString();
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL + "decrementProductStock"))
					.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(arrayString))
					.build();
			client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (JSONException e) {
			// log
		} catch (IOException e) {
			// log
		} catch (InterruptedException e) {
			// log
		}

		return null;
	}

	private Map<String, Object> errorResponse(String error, String message) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("error", error);
		errorResponse.put("message", message);
		return errorResponse;
	}

}
