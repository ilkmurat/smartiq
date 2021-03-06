package com.murat.smartiq.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.murat.smartiq.model.Category;

@RequestMapping("/category")
public interface ICategoryService {

	@RequestMapping(value = "/addCategory", method = RequestMethod.POST)
	public ResponseEntity<Void> addCategory(@RequestBody Category category);

	@RequestMapping(value = "/updateCategory", method = RequestMethod.POST)
	public ResponseEntity<Void> updateCategory(@RequestBody Category category);

	@GetMapping(value = "/removeCategory/{shortCode}")
	public ResponseEntity<Category> removeCategory(@PathVariable("shortCode") String shortCode);

	@GetMapping("/allCategories")
	public List<Category> allCategories();

	@GetMapping(value = "/getCategoryById/{categoryId}")
	public ResponseEntity<Category> getCategoryById(@PathVariable("categoryId") String categoryId);

}
