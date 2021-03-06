package com.murat.smartiq.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.murat.smartiq.model.Product;
import com.murat.smartiq.repository.IProductRepository;
import com.murat.smartiq.service.IProductInstanceService;
import com.murat.smartiq.util.SmartIqUtils;

@RestController
public class ProductController implements IProductInstanceService {

	@Autowired
	IProductRepository productRepo;

	private static final String serviceURL = "http://localhost:8082/order/";

	/**
	 * add new product
	 */
	@Override
	public ResponseEntity addProduct(Product product) {
		try {
			if (product == null || SmartIqUtils.isNullorEmpty(Arrays.asList(product.getProductName()))
					|| product.getStockCount() < 1 || product.getPrice() < 0) {
				return new ResponseEntity<>(
						SmartIqUtils.errorResponse("Missing Parameter", "Required product info : id , count and price"),
						HttpStatus.METHOD_NOT_ALLOWED);
			}

			product.setCreateDate(new Date());
			productRepo.save(product);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(SmartIqUtils.errorResponse("Service Exception", e.toString()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * update existing product
	 */
	@Override
	public ResponseEntity updateProduct(Product product) {
		try {
			if (product.getProductId() == null) {
				return new ResponseEntity<>(SmartIqUtils.errorResponse("Missing Parameter", "productId required"),
						HttpStatus.METHOD_NOT_ALLOWED);
			}
			Optional<Product> prod = productRepo.findById(product.getProductId());
			if (prod.isPresent()) {
				if (product.getProductName() != null)
					prod.get().setProductName(product.getProductName());

				if (product.getPrice() >= 0.0)
					prod.get().setPrice(product.getPrice());

				if (product.getStockCount() >= 0)
					prod.get().setStockCount(product.getStockCount());

				if (product.getCategoryId() != null)
					prod.get().setCategoryId(product.getCategoryId());

				productRepo.save(prod.get());
				return new ResponseEntity<Void>(HttpStatus.OK);
			}
			return new ResponseEntity<>(SmartIqUtils.errorResponse("Product Empty", "Record not found"),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(SmartIqUtils.errorResponse("Service Exception", e.toString()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * remove product with product id
	 */
	@Override
	public ResponseEntity removeProduct(@PathVariable("productId") String productId) {
		try {
			if (productId == null) {
				return new ResponseEntity<>(SmartIqUtils.errorResponse("Missing Parameter", "productId required"),
						HttpStatus.METHOD_NOT_ALLOWED);
			}
			Optional<Product> product = productRepo.findById(productId);
			if (product.isPresent()) {
				productRepo.deleteById(product.get().getProductId());
				return new ResponseEntity<Product>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(SmartIqUtils.errorResponse("Product Empty", "Record not found"),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(SmartIqUtils.errorResponse("Service Exception", e.toString()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public List<Product> allProducts() {
		return productRepo.findAll();
	}

	@Override
	public ResponseEntity<Product> getProductById(@PathVariable("productId") String productId) {
		return productRepo.findById(productId).map(product -> ResponseEntity.ok().body(product))
				.orElse(ResponseEntity.notFound().header("error", "product not found").build());
	}

	@Override
	public ResponseEntity updateProductStock(Product product) {
		try {

			if (product.getProductId() == null) {
				return new ResponseEntity<>(SmartIqUtils.errorResponse("Missing Parameter", "productId required"),
						HttpStatus.METHOD_NOT_ALLOWED);
			}

			Optional<Product> productStock = productRepo.findById(product.getProductId());
			if (productStock.isPresent()) {
				if (product.getStockCount() >= 0)
					productStock.get().setStockCount(product.getStockCount());
				productRepo.save(productStock.get());
				return new ResponseEntity<Void>(HttpStatus.OK);
			}
			return new ResponseEntity<>(SmartIqUtils.errorResponse("Product Empty", "Record not found"),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(SmartIqUtils.errorResponse("Service Exception", e.toString()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity incrementProductStock(String product) {
		try {
			JSONObject request = new JSONObject(product);
			String productId = request.optString("productId");
			int soldCount = request.optInt("count");
			Optional<Product> productStock = productRepo.findById(productId);
			if (productStock != null) {
				int totalStock = productStock.get().getStockCount() + soldCount;
				productStock.get().setStockCount(totalStock);
				productRepo.save(productStock.get());
				// callback
				return new ResponseEntity<Void>(HttpStatus.OK);
			}
			return new ResponseEntity<>(SmartIqUtils.errorResponse("Product Empty", "Record not found"),
					HttpStatus.NOT_FOUND);
		} catch (JSONException e) {
			return new ResponseEntity<>(SmartIqUtils.errorResponse("Service Exception", e.toString()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity decrementProductStock(String productList) {
		try {
			List<Product> prodList = new ArrayList<>();
			// Integer orderId = null;
			JSONArray array = new JSONArray(productList);
			for (int i = 0; i < array.length(); ++i) {
				JSONObject request = array.getJSONObject(i);
				String productId = request.optString("productId");
				int soldCount = request.optInt("count");
				// orderId = request.optInt("orderId");
				Optional<Product> productStock = productRepo.findById(productId);
				if (productStock != null) {
					int totalStock = productStock.get().getStockCount() - soldCount;
					if (totalStock > 0) {
						productStock.get().setStockCount(totalStock);
						prodList.add(productStock.get());
					} else {
						return new ResponseEntity<>(
								SmartIqUtils.errorResponse("Stock error",
										"Up to" + productStock.get().getStockCount() + "can be taken"),
								HttpStatus.METHOD_NOT_ALLOWED);
					}
				} else {
					return new ResponseEntity<>(SmartIqUtils.errorResponse("Product Empty", "Record not found"),
							HttpStatus.NOT_FOUND);
				}

			}
			productRepo.saveAll(prodList);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (JSONException e) {
			return new ResponseEntity<>(SmartIqUtils.errorResponse("Service Exception", e.toString()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * get products by specified category id
	 */
	@Override
	public List<Product> getProductsOfCategory(@PathVariable("categoryId") String categoryId) {
		List<Product> products = productRepo.getProductsByCategoryId(categoryId);
		if (products != null) {
			return products;
		}

		return null;
	}

	public void faultCallback(Integer orderId) {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL + "faultCallback/" + orderId)).GET().build();
		try {
			client.send(req, null);
		} catch (IOException e) {
			// log
		} catch (InterruptedException e) {
			// log
		}

	}

	public void successCallback(Integer orderId) {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL + "successCallback/" + orderId)).GET().build();
		try {
			client.send(req, null);
		} catch (IOException e) {
			// log
		} catch (InterruptedException e) {
			// log
		}
	}

}
