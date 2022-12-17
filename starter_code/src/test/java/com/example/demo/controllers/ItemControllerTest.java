package com.example.demo.controllers;

import static com.example.demo.TestConstants.ANY_ITEM_DESCRIPTION;
import static com.example.demo.TestConstants.ANY_ITEM_ID;
import static com.example.demo.TestConstants.ANY_ITEM_NAME;
import static com.example.demo.TestConstants.ANY_ITEM_PRICE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@RunWith(SpringRunner.class)
public class ItemControllerTest {
	
	@InjectMocks
	private ItemController itemController;
	
	@Mock
	private ItemRepository itemRepository;
	
	List<Item> itemList = new ArrayList<>();
	Item item1 = new Item();
	Item item2 = new Item();
	
	@Before
	public void prepareTestObjects() {
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
	}
	
	@Test
	public void shouldReturnAllItems() {
		when(itemRepository.findAll()).thenReturn(itemList);
		ResponseEntity<List<Item>> itemResp = itemController.getItems();
		assertNotNull(itemResp);
		assertEquals(HttpStatus.OK, itemResp.getStatusCode());
	}
	
	@Test
	public void shouldReturnItemById() {
		when(itemRepository.findById(ANY_ITEM_ID)).thenReturn(Optional.of(item1));
		ResponseEntity<Item> itemResp = itemController.getItemById(ANY_ITEM_ID);
		assertNotNull(itemResp);
		assertEquals(HttpStatus.OK, itemResp.getStatusCode());
	}
	
	@Test
	public void shouldReturnNotFoundWhenItemIdNotExists() {
		ResponseEntity<Item> itemResp = itemController.getItemById(ANY_ITEM_ID);
		assertEquals(HttpStatus.NOT_FOUND, itemResp.getStatusCode());
	}
	
	@Test
	public void shouldReturnItemsByName() {
		when(itemRepository.findByName(ANY_ITEM_NAME)).thenReturn(itemList);
		ResponseEntity<List<Item>> itemResp = itemController.getItemsByName(ANY_ITEM_NAME);
		assertNotNull(itemResp);
		assertEquals(HttpStatus.OK, itemResp.getStatusCode());
	}
	
	@Test
	public void shouldReturnNotFoundWhenNoItems() {
		ResponseEntity<List<Item>> itemResp = itemController.getItemsByName(ANY_ITEM_NAME);
		assertNotNull(itemResp);
		assertEquals(HttpStatus.NOT_FOUND, itemResp.getStatusCode());
	}

}