package com.example.demo.controllers;

import static com.example.demo.TestConstants.ANY_HASHED_PASSWORD;
import static com.example.demo.TestConstants.ANY_PASSWORD;
import static com.example.demo.TestConstants.ANY_SHORT_PASSWORD;
import static com.example.demo.TestConstants.ANY_USERNAME;
import static com.example.demo.TestConstants.ANY_USER_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RunWith(SpringRunner.class)
public class UserControllerTest {

	@InjectMocks
	private UserController userController;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CartRepository cartRepository;

	@Mock
	private BCryptPasswordEncoder encoder;
	
	CreateUserRequest userRequest = new CreateUserRequest();
	
	@Before
	public void prepareTestObjects() {
		userRequest.setUsername(ANY_USERNAME);
		userRequest.setPassword(ANY_PASSWORD);
		userRequest.setConfirmPassword(ANY_PASSWORD);
	}

	@Test
	public void shouldCreateUser() {
		when(encoder.encode(ANY_PASSWORD)).thenReturn(ANY_HASHED_PASSWORD);
		ResponseEntity<User> response = userController.createUser(userRequest);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		User user = response.getBody();
		long userId = user.getId();
		assertEquals(0, userId);
		assertEquals(ANY_USERNAME, user.getUsername());
		assertEquals(ANY_HASHED_PASSWORD, user.getPassword());
	}

	@Test
	public void shouldReturnBadRequestWhenPasswordTooShort() {
		userRequest.setPassword(ANY_SHORT_PASSWORD);
		ResponseEntity<User> responseEntity = userController.createUser(userRequest);
		assertNotNull(responseEntity);
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}

	@Test
	public void shouldReturnBadRequestWhenPasswordNotMatch() {
		userRequest.setConfirmPassword(ANY_PASSWORD + "_suffix");
		ResponseEntity<User> responseEntity = userController.createUser(userRequest);
		assertNotNull(responseEntity);
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}
	
	@Test
	public void shouldReturnNotFoundWhenUserNotExists() {
		ResponseEntity<User> user = userController.findByUserName(ANY_USERNAME);
		assertEquals(HttpStatus.NOT_FOUND, user.getStatusCode());
	}

	@Test
	public void shouldReturnStatusOkWhenSearchingUserByUsername() {
		when(encoder.encode(ANY_PASSWORD)).thenReturn(ANY_HASHED_PASSWORD);
		ResponseEntity<User> response = userController.createUser(userRequest);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		User user = response.getBody();
		when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
		ResponseEntity<User> userResp = userController.findByUserName(user.getUsername());
		assertEquals(200, userResp.getStatusCodeValue());
		assertNotNull(userResp.getBody());
		assertEquals(ANY_USERNAME, userResp.getBody().getUsername());
	}



	@Test
	public void shouldReturnStatusOkWhenSearchingUserById() {
		when(encoder.encode(ANY_PASSWORD)).thenReturn(ANY_HASHED_PASSWORD);
		ResponseEntity<User> response = userController.createUser(userRequest);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		User user = response.getBody();
		
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		ResponseEntity<User> userResp = userController.findById(user.getId());
		assertEquals(HttpStatus.OK, userResp.getStatusCode());
		assertNotNull(userResp.getBody());
		assertEquals(0, userResp.getBody().getId());
	}

	@Test
	public void shouldReturnNotFoundWhenIdNotExists() {
		ResponseEntity<User> user = userController.findById(ANY_USER_ID);
		assertEquals(HttpStatus.NOT_FOUND, user.getStatusCode());
	}

}