package com.example.demo.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@RestController
@RequestMapping("/api/item")
public class ItemController {

	private Logger logger = LogManager.getLogger(ItemController.class);

	@Autowired
	private ItemRepository itemRepository;
	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		logger.info("<ItemController: getItems> Getting all items...");
		return ResponseEntity.ok(itemRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		logger.info("<ItemController: getItemById> Getting item with id {} ...", id);
		return ResponseEntity.of(itemRepository.findById(id));
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		logger.info("<ItemController: getItemsByName> Getting item with name {} ...", name);
		List<Item> items = itemRepository.findByName(name);
		if (items ==null || items.isEmpty()) {
			logger.info("<ItemController: getItemsByName> Item with name {} could not be found", name);
			return ResponseEntity.notFound().build();
		} else {
			logger.info("<ItemController: getItemsByName> Item with name {} found", name);
			return ResponseEntity.ok(items);
		}

	}
}