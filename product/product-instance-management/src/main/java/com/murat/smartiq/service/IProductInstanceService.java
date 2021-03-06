package com.murat.smartiq.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.murat.smartiq.model.Product;

@RequestMapping("/product")
public interface IProductInstanceService {

	@RequestMapping(value = "/addProduct", method = RequestMethod.POST)
	public ResponseEntity<Void> addProduct(@RequestBody Product product);

	@RequestMapping(value = "/updateProduct", method = RequestMethod.POST)
	public ResponseEntity<Void> updateProduct(@RequestBody Product product);

	@GetMapping(value = "/removeProduct/{productId}")
	public ResponseEntity<Product> removeProduct(@PathVariable("productId") String productId);

	@GetMapping("/allProducts")
	public List<Product> allProducts();

	@GetMapping(value = "/getProductById/{productId}")
	public ResponseEntity<Product> getProductById(@PathVariable("productId") String productId);

	@RequestMapping(value = "/updateProductStock", method = RequestMethod.POST)
	public ResponseEntity<Void> updateProductStock(@RequestBody Product product);
	
	@RequestMapping(value = "/incrementProductStock", method = RequestMethod.POST)
	public ResponseEntity<Void> incrementProductStock(@RequestBody String product) ;

	@RequestMapping(value = "/decrementProductStock", method = RequestMethod.POST)
	public ResponseEntity<Void> decrementProductStock(@RequestBody String productList);

	@GetMapping(value = "/getProductsOfCategory/{categoryId}")
	public List<Product> getProductsOfCategory(@PathVariable("categoryId") String categoryId);

}
