package com.barclays.cardservice.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barclays.cardservice.constants.CustomerControllerConstants;
import com.barclays.cardservice.constants.SystemConstants;
import com.barclays.cardservice.dto.CardDTO;
import com.barclays.cardservice.dto.CustomerDTO;
import com.barclays.cardservice.exception.BarclaysException;
import com.barclays.cardservice.service.CardCustomerService;

/**
 * CustomerController - Rest api for customer details related routes
 * 
 */
@RestController
@RequestMapping(CustomerControllerConstants.ROOT_MAPPING)
public class CustomerController {
	
	@Autowired
	CardCustomerService cardCustomerService;
	
	/**
	 * getCustomer - Get all details of customer including card details for given customer id
	 * @param customerId - Id of customer
	 * @return Customer Details json
	 * @throws BarclaysException
	 */
	@GetMapping(CustomerControllerConstants.GET_CUSTOMER)
	public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Integer customerId) throws BarclaysException {
		CustomerDTO customer = cardCustomerService.getCustomerDetails(customerId);
		return new ResponseEntity<>(customer, HttpStatus.OK);
	}
	
	/**
	 * addCustomer - Create new customer
	 * @param customer - customer data
	 * @return ID of newly created customer
	 * @throws BarclaysException
	 */
	@PostMapping(CustomerControllerConstants.ADD_CUSTOMER)
	public ResponseEntity<Integer> addCustomer(@RequestBody CustomerDTO customer) throws BarclaysException {
		Integer id = cardCustomerService.addCustomer(customer);
		return new ResponseEntity<>(id, HttpStatus.OK);
	}
	
	/**
	 * issueCard - Issues new card to a user
	 * @param customerId - Customer id, card - Details for new card
	 * @return Http response
	 * @throws BarclaysException
	 */
	@PostMapping(CustomerControllerConstants.NEW_CARD)
	public ResponseEntity<String> issueCard(@PathVariable Integer customerId, @RequestBody CardDTO card) throws BarclaysException {
		cardCustomerService.issueCardToExistingCustomer(customerId, card);
		return new ResponseEntity<>(SystemConstants.CARD_ISSUSED, HttpStatus.OK);
	}
	
	/**
	 * deleteCustomer - Delete customer data along with their card data for the given customer ID
	 * @param customerId - Customer id
	 * @return Http Response
	 * @throws BarclaysException
	 */
	@DeleteMapping(CustomerControllerConstants.DELETE_CUSTOMER)
	public ResponseEntity<String> deleteCustomer(@PathVariable Integer customerId) throws BarclaysException {
		cardCustomerService.deleteCustomer(customerId);
		return new ResponseEntity<>(SystemConstants.CUSTOMER_DELETE, HttpStatus.OK);
	}
	
	/**
	 * deleteCustomerCards - Delete customer cards
	 * @param customerId - Customer id, cardIds - List of card ids to be deleted
	 * @return Http response
	 * @throws BarclaysException
	 */
	@DeleteMapping(CustomerControllerConstants.DELETE_CARDS)
	public ResponseEntity<String> deleteCustomerCards(@PathVariable Integer customerId, @RequestBody List<Integer> cardIds) throws BarclaysException {
		cardCustomerService.deleteCardOfExistingCustomer(customerId, cardIds);
		return new ResponseEntity<>(SystemConstants.CARD_DELETE, HttpStatus.OK);
	}
}
