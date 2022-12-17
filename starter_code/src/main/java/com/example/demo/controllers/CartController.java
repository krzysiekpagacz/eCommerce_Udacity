package com.example.demo.controllers;

import static com.example.demo.util.Mapper.mapToJsonString;

import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.catalina.mapper.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	
	private Logger logger = LogManager.getLogger(CartController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		logger.info("<CartController: addToCart> Execution started...");
		logger.info("<CartController: addTocart> Request payload {}", mapToJsonString(request)); 
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			logger.error("<CartController: addToCart> User with username: {} could not be found!", request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			logger.error("<CartController: addToCart> Item with id: {} could not be found!", request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);
		logger.info("<CartController: addToCart> Cart successfully saved.");
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		logger.info("<CartController: removeFromcart> Execution started...");
		logger.info("<CartController: removeFromcart> Request payload {}", mapToJsonString(request)); 
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			logger.error("<CartController: removeFromcart> User with username: {} could not be found!", request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			logger.error("<CartController: removeFromcart> Item with id: {} could not be found!", request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		cartRepository.save(cart);
		logger.info("<CartController: removeFromcart> Item successfully removed from Cart.");
		return ResponseEntity.ok(cart);
	}
		
}