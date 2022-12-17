package com.example.demo.controllers;

import static com.example.demo.TestConstants.ANY_ITEM_DESCRIPTION;
import static com.example.demo.TestConstants.ANY_ITEM_ID;
import static com.example.demo.TestConstants.ANY_ITEM_NAME;
import static com.example.demo.TestConstants.ANY_ITEM_PRICE;
import static com.example.demo.TestConstants.ANY_ITEM_QUANTITY;
import static com.example.demo.TestConstants.ANY_USER_ORDER_ID;
import static com.example.demo.TestConstants.ANY_PASSWORD;
import static com.example.demo.TestConstants.ANY_USERNAME;
import static com.example.demo.TestConstants.ANY_USER_ID;
import static com.example.demo.TestConstants.ANY_CART_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RunWith(SpringRunner.class)
public class OrderControllerTest {

	@InjectMocks
	private OrderController orderController;

	@Mock
	private UserRepository userRepository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private ItemRepository itemRepository;
	
	private Cart cart = new Cart();
	private User user = new User();
	private UserOrder userOrder = new UserOrder();
	List<Item> itemList = new ArrayList<>();
	Item item1 = new Item();
	Item item2 = new Item();
	
	@Before
	public void prepareTestObjects() {
		cart.setId(ANY_CART_ID);
		cart.setItems(itemList);
		cart.setUser(user);
		
		user.setCart(cart);
		user.setId(ANY_USER_ID);
		user.setUsername(ANY_USERNAME);
		user.setPassword(ANY_PASSWORD);

		item1.setId(ANY_ITEM_ID);
		item1.setDescription(ANY_ITEM_DESCRIPTION);
		item1.setName(ANY_ITEM_NAME);
		item1.setPrice(ANY_ITEM_PRICE);
		itemList.add(item1);

		item2.setId(ANY_ITEM_ID);
		item2.setDescription(ANY_ITEM_DESCRIPTION);
		item2.setName(ANY_ITEM_NAME + "_2");
		item2.setPrice(ANY_ITEM_PRICE);
		itemList.add(item2);
		
		userOrder.setId(ANY_USER_ORDER_ID);
		userOrder = UserOrder.createFromCart(user.getCart());
	}
	
	@Test
	public void shouldCreateOrder() {
		when(userRepository.findByUsername(ANY_USERNAME)).thenReturn(user);
		ResponseEntity<UserOrder> orderResp = orderController.submit(ANY_USERNAME);
		assertNotNull(orderResp);
		assertEquals(HttpStatus.OK, orderResp.getStatusCode());
		
		UserOrder userOrder = orderResp.getBody();
		assertNotNull(userOrder);
	}
	
	@Test
	public void shouldReturnNotFoundWhenUserNotExists() {
		when(userRepository.findByUsername(ANY_USERNAME)).thenReturn(null);
		ResponseEntity<UserOrder> userResp = orderController.submit(ANY_USERNAME);
		assertNotNull(userResp);
        assertEquals(HttpStatus.NOT_FOUND, userResp.getStatusCode());
	}
	
	@Test
	public void shouldReurnOrderHistoryForUser() {
        when(userRepository.findByUsername(ANY_USERNAME)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(userOrder));

        ResponseEntity<List<UserOrder>> userOrderResp = orderController.getOrdersForUser(ANY_USERNAME);
        assertNotNull(userOrderResp);
        assertEquals(HttpStatus.OK, userOrderResp.getStatusCode());

        List<UserOrder> orders = userOrderResp.getBody();
        assertNotNull(orders);
        assertEquals(2, orders.get(0).getItems().size());
        assertEquals(user, orders.get(0).getUser());
        assertEquals(userOrder.getTotal(), orders.get(0).getTotal());
	}
	
    @Test
    public void shouldReturnNotFoundGetOrderHistoryNoUser(){
        when(userRepository.findByUsername(ANY_USERNAME)).thenReturn(null);
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(userOrder));

        ResponseEntity<List<UserOrder>> userOrderResp = orderController.getOrdersForUser("Artis");
        assertNotNull(userOrderResp);
        assertEquals(HttpStatus.NOT_FOUND, userOrderResp.getStatusCode());
    }
}