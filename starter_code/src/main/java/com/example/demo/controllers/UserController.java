package com.example.demo.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private Logger logger = LogManager.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		logger.info("<UserController: findById> Getting user with id {} ...", id);
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		logger.info("<UserController: findById> Getting user with name {} ...", username);
		User user = userRepository.findByUsername(username);
		if (user == null) {
			logger.error("<UserController: findByUserName> User with name {} could not be found", username);
			return ResponseEntity.notFound().build();
		} else {
			logger.info("<UserController: findByUserName> User with name {} found", username);
			return ResponseEntity.ok(user);
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		logger.info("<UserController: createUser> Execution started...");
		User user = new User();
		logger.info("<UserController: createUser> Setting username to {}", createUserRequest.getUsername());
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		logger.info("<UserController: createUser> Cart saved");
		user.setCart(cart);
		if(createUserRequest.getPassword().length()<7 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			logger.error("<UserController: createUser> Error - Either length is less than 7 or pass and conf pass do not match. Unable to create ",
					createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		userRepository.save(user);
		logger.info("<UserController: createUser> User successfully created.");
		return ResponseEntity.ok(user);
	}
	
	
}