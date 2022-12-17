package com.example.demo.controllers;

import static com.example.demo.TestConstants.ANY_ITEM_DESCRIPTION;
import static com.example.demo.TestConstants.ANY_ITEM_ID;
import static com.example.demo.TestConstants.ANY_ITEM_NAME;
import static com.example.demo.TestConstants.ANY_ITEM_PRICE;
import static com.example.demo.TestConstants.ANY_ITEM_QUANTITY;
import static com.example.demo.TestConstants.ANY_PASSWORD;
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
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RunWith(SpringRunner.class)
public class CartControllerTest {

	@InjectMocks
	private CartController cartController;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CartRepository cartRepository;

	@Mock
	private ItemRepository itemRepository;
	
	private User user = new User();;
	private Item item = new Item();
	private ModifyCartRequest cartRequest = new ModifyCartRequest();
	
	@Before
	public void prepareTestObjects() {
		user.setCart(new Cart());
		user.setId(ANY_USER_ID);
		user.setUsername(ANY_USERNAME);
		user.setPassword(ANY_PASSWORD);

		item.setId(ANY_ITEM_ID);
		item.setDescription(ANY_ITEM_DESCRIPTION);
		item.setName(ANY_ITEM_NAME);
		item.setPrice(ANY_ITEM_PRICE);
		
		cartRequest.setItemId(ANY_ITEM_ID);
		cartRequest.setUsername(ANY_USERNAME);
		cartRequest.setQuantity(ANY_ITEM_QUANTITY);
	}

	@Test
	public void shouldReturnOkWhenAddingToCart() {
		when(userRepository.findByUsername(cartRequest.getUsername())).thenReturn(user);
		when(itemRepository.findById(cartRequest.getItemId())).thenReturn(Optional.of(item));

		ResponseEntity<Cart> cartResp = cartController.addTocart(cartRequest);
		assertNotNull(cartResp);
		assertEquals(200, cartResp.getStatusCodeValue());
		Cart cart = cartResp.getBody();
		assertEquals(ANY_ITEM_QUANTITY, cart.getItems().size());
	}
	
	@Test
	public void shouldReturnOkWhenRemovingFromCart() {
		when(userRepository.findByUsername(cartRequest.getUsername())).thenReturn(user);
		when(itemRepository.findById(cartRequest.getItemId())).thenReturn(Optional.of(item));

		ResponseEntity<Cart> cartResp = cartController.addTocart(cartRequest);
		assertNotNull(cartResp);
		assertEquals(200, cartResp.getStatusCodeValue());
		Cart cart = cartResp.getBody();
		assertEquals(ANY_ITEM_QUANTITY, cart.getItems().size());
		
        ResponseEntity<Cart> cartRemoveReq = cartController.removeFromcart(cartRequest);
        assertNotNull(cartRemoveReq);
        assertEquals(HttpStatus.OK, cartRemoveReq.getStatusCode());

        assertNotNull(cartRemoveReq.getBody());
        assertEquals(0, cartRemoveReq.getBody().getItems().size());
	}
	
	@Test
	public void shouldReturnNotFoundWhenNoUserWhenAddingCard() {
		when(userRepository.findByUsername(cartRequest.getUsername())).thenReturn(null);

        ResponseEntity<Cart> cartResp = cartController.addTocart(cartRequest);

        assertNotNull(cartResp);
        assertEquals(HttpStatus.NOT_FOUND, cartResp.getStatusCode());
	}
	
	@Test
	public void shouldReturnNotFoundWhenNoUserWhenRemovingCard() {
		when(userRepository.findByUsername(cartRequest.getUsername())).thenReturn(user);
		when(itemRepository.findById(cartRequest.getItemId())).thenReturn(Optional.of(item));

		ResponseEntity<Cart> cartResp = cartController.addTocart(cartRequest);
		assertNotNull(cartResp);
		assertEquals(200, cartResp.getStatusCodeValue());
		Cart cart = cartResp.getBody();
		assertEquals(ANY_ITEM_QUANTITY, cart.getItems().size());
		
		when(userRepository.findByUsername(cartRequest.getUsername())).thenReturn(null);
		
		ResponseEntity<Cart> cartRemoveReq = cartController.removeFromcart(cartRequest);

        assertNotNull(cartRemoveReq);
        assertEquals(HttpStatus.NOT_FOUND, cartRemoveReq.getStatusCode());

	}
	
	@Test
	public void shouldReturnNotFoundWhenNoItemWhenAddingCard() {
		when(userRepository.findByUsername(cartRequest.getUsername())).thenReturn(user);
        when(itemRepository.findById(cartRequest.getItemId())).thenReturn(Optional.empty());
        
        ResponseEntity<Cart> cartResp = cartController.addTocart(cartRequest);

        assertNotNull(cartResp);
        assertEquals(HttpStatus.NOT_FOUND, cartResp.getStatusCode());
	}
	
	@Test
	public void shouldReturnNotFoundWhenNoItemWhenRemovingCard() {
		when(userRepository.findByUsername(cartRequest.getUsername())).thenReturn(user);
        when(itemRepository.findById(cartRequest.getItemId())).thenReturn(Optional.of(item));
        
		ResponseEntity<Cart> cartResp = cartController.addTocart(cartRequest);
		assertNotNull(cartResp);
		assertEquals(HttpStatus.OK, cartResp.getStatusCode());
		Cart cart = cartResp.getBody();
		assertEquals(ANY_ITEM_QUANTITY, cart.getItems().size());
		
		when(itemRepository.findById(cartRequest.getItemId())).thenReturn(Optional.empty());
		
		ResponseEntity<Cart> cartRemoveReq = cartController.removeFromcart(cartRequest);

        assertNotNull(cartRemoveReq);
        assertEquals(HttpStatus.NOT_FOUND, cartRemoveReq.getStatusCode());

	}

}