package com.barclays.cardservice.constants;

public interface CustomerControllerConstants {
	public static final String ROOT_MAPPING = "/api/customer";
	public static final String GET_CUSTOMER = "/{customerId}";
	public static final String DELETE_CUSTOMER = "/{customerId}";
	public static final String ADD_CUSTOMER = "/";
	public static final String NEW_CARD = "{customerId}/newcard";
	public static final String DELETE_CARDS = "/{customerId}/cards";
}
