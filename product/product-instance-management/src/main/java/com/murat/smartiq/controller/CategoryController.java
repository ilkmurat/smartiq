package com.murat.smartiq.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.murat.smartiq.model.Category;
import com.murat.smartiq.repository.ICategoryRepository;
import com.murat.smartiq.service.ICategoryService;
import com.murat.smartiq.util.SmartIqUtils;

@RestController
public class CategoryController implements ICategoryService {

	@Autowired
	ICategoryRepository categoryRepo;

	/**
	 * creating new category
	 */
	@Override
	public ResponseEntity addCategory(Category category) {
		try {
			List<String> objectList = Arrays.asList(category.getCategoryName(), category.getShortCode());

			if (category == null || SmartIqUtils.isNullorEmpty(objectList)) {
				return new ResponseEntity<>(
						SmartIqUtils.errorResponse("Category not created", "Please provide name and short code"),
						HttpStatus.NOT_FOUND);
			}
			category.setCreateDate(new Date());
			categoryRepo.save(category);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(SmartIqUtils.errorResponse("Service Exception", e.toString()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * update exist category
	 */
	@Override
	public ResponseEntity updateCategory(Category category) {
		try {
			if (category == null || SmartIqUtils.isNullorEmpty(Arrays.asList(category.getCategoryId()))) {
				return new ResponseEntity<>(SmartIqUtils.errorResponse("Category Empty", "Please provide category id"),
						HttpStatus.NOT_FOUND);
			}
			Optional<Category> cat = categoryRepo.findById(category.getCategoryId());
			if (cat != null) {
				if (!SmartIqUtils.isNullorEmpty(Arrays.asList(category.getCategoryName())))
					cat.get().setCategoryName(category.getCategoryName());

				if (!SmartIqUtils.isNullorEmpty(Arrays.asList(category.getShortCode())))
					cat.get().setShortCode(category.getShortCode());

				categoryRepo.save(cat.get());
				return new ResponseEntity<Void>(HttpStatus.OK);
			}
			return new ResponseEntity<>(SmartIqUtils.errorResponse("Category not updated", "Record not found"),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(SmartIqUtils.errorResponse("Service Exception", e.toString()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * remove exist category with short code
	 */
	@Override
	public ResponseEntity removeCategory(@PathVariable("shortCode") String shortCode) {
		try {
			if (shortCode == null) {
				return new ResponseEntity<>(
						SmartIqUtils.errorResponse("ShortCode Empty", "Please provide category short code"),
						HttpStatus.NOT_FOUND);
			}
			List<Category> category = categoryRepo.getCategoryByShortCode(shortCode);
			if (category != null) {
				categoryRepo.deleteById(category.get(0).getCategoryId());
				return new ResponseEntity<Category>(HttpStatus.OK);
			}

			return new ResponseEntity<>(SmartIqUtils.errorResponse("Category not deleted", "Record not found"),
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(SmartIqUtils.errorResponse("Service Exception", e.toString()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * list all categories
	 */
	@Override
	public List<Category> allCategories() {
		return categoryRepo.findAll();
	}

	/**
	 * get category info by category id
	 */
	@Override
	public ResponseEntity<Category> getCategoryById(@PathVariable("categoryId") String categoryId) {
		return categoryRepo.findById(categoryId).map(category -> ResponseEntity.ok().body(category))
				.orElse(ResponseEntity.notFound().header("error", "category not found").build());
	}

}
