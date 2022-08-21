package com.barclays.cardservice.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.barclays.cardservice.api.CustomerController;
import com.barclays.cardservice.constants.SystemConstants;
import com.barclays.cardservice.dto.CardDTO;
import com.barclays.cardservice.dto.CustomerDTO;
import com.barclays.cardservice.exception.BarclaysException;

@SpringBootTest
public class CustomerControllerTest {
	
	@Autowired
	CustomerController customerController;
	
	static CustomerDTO customer1, customer2;
	static CardDTO card1, card2;
	
	@BeforeAll
	public static void intialize() {
		customer1 = new CustomerDTO();
		customer1.setName("ABC");
		customer1.setEmail("ABC@gmail.com");
		customer1.setDob(LocalDate.of(2000, 01, 10));
		
		card1 = new CardDTO();
		card1.setCardId(123456);
		card1.setCardNumber("11111111");
		card1.setCustomer(customer1);
		card1.setExpiryDate(LocalDate.of(2007, 01, 10));
		
		customer2 = new CustomerDTO();
		customer2.setName("XYZ");
		customer2.setEmail("XYZ@gmail.com");
		customer2.setDob(LocalDate.of(2001, 07, 11));
		
		card2 = new CardDTO();
		card2.setCardId(123457);
		card2.setCardNumber("222222222");
		card2.setCustomer(customer2);
		card2.setExpiryDate(LocalDate.of(2008, 07, 11));

		List<CardDTO> cardList = new ArrayList<>();
		cardList.add(card2);
		customer2.setCards(cardList);
	}
	
	/**
	 * get Customer success
	 * @throws BarclaysException
	 */
	@Test
//	public void getValidCustomer() throws BarclaysException {
	public void test01() throws BarclaysException {
		ResponseEntity<CustomerDTO> customer = customerController.getCustomer(1001);
		Assertions.assertEquals(customer.getStatusCodeValue(), HttpStatus.OK.value());
	}

	/**
	 * get Customer fail - Customer id does not exist
	 * @throws BarclaysException
	 */
	@Test
//	public void getInValidCustomer() throws BarclaysException {
	public void test02() throws BarclaysException {
		Assertions.assertThrows(BarclaysException.class, () -> customerController.getCustomer(999999));
	}
	
	/**
	 * Add customer test1
	 * @throws BarclaysException
	 */
	@Test 
//	public void addCustomerTest1() throws BarclaysException {
	public void test03() throws BarclaysException {	
		ResponseEntity<Integer> id = customerController.addCustomer(customer1);
		customer1.setId(id.getBody());
		Assertions.assertEquals(id.getStatusCodeValue(), HttpStatus.OK.value());
	}
	
	/**
	 * add customer test2
	 * @throws BarclaysException
	 */
	@Test 
	public void test04() throws BarclaysException {
		ResponseEntity<Integer> id = customerController.addCustomer(customer2);
		customer2.setId(id.getBody());
		Assertions.assertEquals(id.getStatusCodeValue(), HttpStatus.OK.value());
	}

	/**
	 * issue card fail - Customer id does not exist
	 * @throws BarclaysException
	 */
	@Test
	public void test05() throws BarclaysException {
		Assertions.assertThrows(BarclaysException.class, () -> customerController.issueCard(1, card1));
	}
	
	/**
	 * issue card success
	 * @throws BarclaysException
	 */
	@Test
	public void test06() throws BarclaysException {
		ResponseEntity<String> res = customerController.issueCard(customer1.getId(), card1);
		Assertions.assertEquals(res.getBody(), SystemConstants.CARD_ISSUSED);
	}
	
	/**
	 * delete card fail 1 - Customer id does not exist
	 * @throws BarclaysException
	 */
	@Test
	public void test07() throws BarclaysException {
		List<Integer> ids = new ArrayList<>();
		ids.add(1);
		ids.add(2);
		Assertions.assertThrows(BarclaysException.class, () -> customerController.deleteCustomerCards(1, ids));
	}
	
	/**
	 * delete card fail 2 - Card id list not provided
	 * @throws BarclaysException
	 */
	@Test
	public void test08() throws BarclaysException {
		Assertions.assertThrows(BarclaysException.class, () -> customerController.deleteCustomerCards(customer1.getId(), null));
	}

	/**
	 * delete card fail 3 - Card ids does not exists
	 * @throws BarclaysException
	 */
	@Test
	public void test09() throws BarclaysException {
		List<Integer> ids = new ArrayList<>();
		ids.add(1);
		ids.add(2);
		Assertions.assertThrows(BarclaysException.class, () -> customerController.deleteCustomerCards(customer1.getId(), ids));
	}
	
	/**
	 * delete card fail 4 - Card does not belong to customer
	 * @throws BarclaysException
	 */
	@Test
	public void test10() throws BarclaysException {
		List<Integer> ids = new ArrayList<>();
		ids.add(card2.getCardId());
		Assertions.assertThrows(BarclaysException.class, () -> customerController.deleteCustomerCards(customer1.getId(), ids));
	}
	
	/**
	 * delete card success
	 * @throws BarclaysException
	 */
	@Test
	public void test11() throws BarclaysException {
		List<Integer> ids = new ArrayList<>();
		ids.add(card1.getCardId());
		ResponseEntity<String> res = customerController.deleteCustomerCards(customer1.getId(), ids);
		Assertions.assertEquals(res.getBody(), SystemConstants.CARD_DELETE);
	}
	
	/**
	 * delete customer success 1
	 * @throws BarclaysException
	 */
	@Test
	public void test12() throws BarclaysException {
		ResponseEntity<String> res = customerController.deleteCustomer(customer1.getId());
		Assertions.assertEquals(res.getBody(), SystemConstants.CUSTOMER_DELETE);
	}

	/**
	 * delete customer success 2
	 * @throws BarclaysException
	 */
	@Test
	public void test13() throws BarclaysException {
		ResponseEntity<String> res = customerController.deleteCustomer(customer2.getId());
		Assertions.assertEquals(res.getBody(), SystemConstants.CUSTOMER_DELETE);
	}

	/**
	 * Delete Customer fail as the customer does not exist
	 * @throws BarclaysException
	 */
	@Test
	public void test14() throws BarclaysException {
		Assertions.assertThrows(BarclaysException.class, () -> customerController.deleteCustomer(customer1.getId()));
	}
	
	@AfterAll
	public static void Free() {
		customer1 = null;
		customer2 = null;
		card1 = null;
		card2 = null;
	}
}
