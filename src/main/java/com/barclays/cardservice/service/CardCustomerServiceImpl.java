package com.barclays.cardservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barclays.cardservice.constants.SystemConstants;
import com.barclays.cardservice.dto.CardDTO;
import com.barclays.cardservice.dto.CustomerDTO;
import com.barclays.cardservice.entity.Card;
import com.barclays.cardservice.entity.Customer;
import com.barclays.cardservice.exception.BarclaysException;
import com.barclays.cardservice.repository.CardRepository;
import com.barclays.cardservice.repository.CustomerRepository;

/**
 * CardCustomerServiceImpl - Card customer service interface implementation
 */
@Service(value = "cardCustomerService")
@Transactional
public class CardCustomerServiceImpl implements CardCustomerService {

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	CardRepository cardRepository;
	
	/**
	 * getCustomerDetails - Get customer details for given customer id
	 * @param customerId
	 * @return Customer details along with card details
	 * @throws BarclaysException
	 */
	@Override
	public CustomerDTO getCustomerDetails(Integer customerId) throws BarclaysException {
		Optional<Customer> opt = customerRepository.findById(customerId);
		if (!opt.isPresent())
			throw new BarclaysException(SystemConstants.CUSTOMER_NOT_FOUND);
		
		Customer customer = opt.get();
		
		List<Card> cards = cardRepository.findByCustomer_customerId(customerId);
		List<CardDTO> cardDTOs = new ArrayList<>();
		cards.forEach(card -> cardDTOs.add(convertCardToDTO(card, null)));
		
		CustomerDTO customerDTO = convertCustomerToDTO(customer, cardDTOs);
		
		return customerDTO;
	}

	/**
	 * addCustomer - Add new customer
	 * @param customerDTO
	 * @return Id of newly created customer
	 * @throws BarclaysException
	 */
	@Override
	public Integer addCustomer(CustomerDTO customerDTO) throws BarclaysException {
		Customer customer = convertCustomerDTOtoModel(customerDTO);
		Customer newCustomer = customerRepository.save(customer);
		
		List<CardDTO> cardDTOs = customerDTO.getCards();
		if (cardDTOs != null && cardDTOs.size() > 0) {
			cardDTOs.forEach(cardDTO -> cardRepository.save(cardDTOtoModel(newCustomer, cardDTO)));
		}
		
		return newCustomer.getCustomerId();
	}

	/**
	 * issueCardToExistingCustomer - Issue new card to customer
	 * @param customerId, cardDTO
	 * @throws BarclaysException
	 */
	@Override
	public void issueCardToExistingCustomer(Integer customerId, CardDTO cardDTO) throws BarclaysException {
		Optional<Customer> opt = customerRepository.findById(customerId);
		if (!opt.isPresent())
			throw new BarclaysException(SystemConstants.CUSTOMER_NOT_FOUND);
		
		cardRepository.save(cardDTOtoModel(opt.get(), cardDTO));
	}

	/**
	 * deleteCustomer - Delete customer data along with their card(s) data
	 * @param customerId
	 * @throws BarclaysException
	 */
	@Override
	public void deleteCustomer(Integer customerId) throws BarclaysException {
		Optional<Customer> opt = customerRepository.findById(customerId);
		if (!opt.isPresent())
			throw new BarclaysException(SystemConstants.CUSTOMER_NOT_FOUND);
		
		Customer customer = opt.get();
		cardRepository.deleteByCustomer_customerId(customerId);
//		List<Card> cards = cardRepository.findByCustomer_customerId(customerId);
//		cards.forEach(card -> cardRepository.delete(card));
		customerRepository.delete(customer);
	}

	/**
	 * deleteCardOfExistingCustomer - Delete cards of given customers
	 * @param customerId, cardIdsToDelete - list of card id's to be deleted
	 * @throws BarclaysException
	 */
	@Override
	public void deleteCardOfExistingCustomer(Integer customerId, List<Integer> cardIdsToDelete) throws BarclaysException {
		if (cardIdsToDelete == null || cardIdsToDelete.size() == 0) {
			throw new BarclaysException(SystemConstants.CARD_LIST_NOT_PROVIDED);
		}
		
		Optional<Customer> opt = customerRepository.findById(customerId);
		if (!opt.isPresent())
			throw new BarclaysException(SystemConstants.CUSTOMER_NOT_FOUND);
		
		Customer customer = opt.get();
		
		for (Integer cardId: cardIdsToDelete) {
			Optional<Card> cardOpt = cardRepository.findById(cardId);
			
			if (!cardOpt.isPresent())
				throw new BarclaysException(SystemConstants.CARD_NOT_FOUND);
			
			Card card = cardOpt.get();
			if (card.getCustomer().getCustomerId() != customer.getCustomerId()) 
				throw new BarclaysException(SystemConstants.CARD_DOES_NOT_BELONG_TO_CUSTOMER);
			
			cardRepository.delete(card);
		}
	}
	
	/**
	 * convertCustomerDTOtoModel - Convert Customer DTO to model
	 * @param customerDTO
	 * @return Customer entity object
	 */
	private Customer convertCustomerDTOtoModel(CustomerDTO customerDTO) {
		Customer customer = new Customer();
		customer.setCustomerId(customerDTO.getId());
		customer.setName(customerDTO.getName());
		customer.setEmailid(customerDTO.getEmail());
		customer.setDateOfBirth(customerDTO.getDob());
		return customer;
	}
	
	/**
	 * convertToDTO - Convert Customer entity to CustomerCardDTO
	 * @param customer,cardDTOs
	 * @return CustomerDTO object
	 */
	private CustomerDTO convertCustomerToDTO(Customer customer, List<CardDTO> cardDTOs) {
		if (customer == null) return null;
		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setId(customer.getCustomerId());
		customerDTO.setEmail(customer.getEmailid());
		customerDTO.setName(customer.getName());
		customerDTO.setDob(customer.getDateOfBirth());
		customerDTO.setCards(cardDTOs);
		return customerDTO;
	}

	/**
	 * cardDTOtoModel - Convert CardDTO to card entity
	 * @param customer, cardDTO
	 * @return Card entity object
	 */
	private Card cardDTOtoModel(Customer customer, CardDTO cardDTO) {
		Card card = new Card();
		card.setCardId(cardDTO.getCardId());
		card.setCardNumber(cardDTO.getCardNumber());
		card.setExpiryDate(cardDTO.getExpiryDate());
		card.setCustomer(customer);
		return card;
	}
	
	/**
	 * convertCardToDTO - Convert Card object to cardDTO;
	 * @param card, customer
	 * @return CardDTO object
	 */
	private CardDTO convertCardToDTO(Card card, Customer customer) {
		CardDTO cardDTO = new CardDTO();
		cardDTO.setCardId(card.getCardId());
		cardDTO.setCardNumber(card.getCardNumber());
		cardDTO.setExpiryDate(card.getExpiryDate());
		cardDTO.setCustomer(convertCustomerToDTO(customer, null));
		return cardDTO;
	}
}
