package com.zosh.service;

import com.stripe.model.Address;
import com.stripe.model.Customer;
import com.stripe.param.ChargeCreateParams;
import com.zosh.model.Order;
import com.zosh.model.PaymentResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImplementation implements PaymentService{
	// Test Card for India - 4000003560000008
	// CVV - 123
	
	@Value("${stripe.api.key}")
	 private String stripeSecretKey;

	@Override
	public PaymentResponse generatePaymentLink(Order order) throws StripeException {

		// Include address collection
		Map<String, Object> extraParams = new HashMap<>();
		Map<String, Object> billingAddressCollection = new HashMap<>();
		billingAddressCollection.put("required", true);
		extraParams.put("billing_address_collection", billingAddressCollection);
//		String extraParamsJson = new Gson().toJson(extraParams);

	  Stripe.apiKey = stripeSecretKey;

	        SessionCreateParams params = SessionCreateParams.builder()
	                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
	                .setMode(SessionCreateParams.Mode.PAYMENT)
					.setBillingAddressCollection(
							SessionCreateParams.BillingAddressCollection.REQUIRED
					)
	                .setSuccessUrl("http://localhost:3000/payment/success/"+order.getId())
	                .setCancelUrl("http://localhost:3000/cancel")
	                .addLineItem(SessionCreateParams.LineItem.builder()
	                        .setQuantity(1L)
	                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
	                                .setCurrency("INR")
	                                .setUnitAmount((long) order.getTotalAmount()*100)
	                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
	                                        .setName("Your Food")
	                                        .build())
	                                .build())
	                        .build())
	                .build();

	        Session session = Session.create(params);
	        session.setBillingAddressCollection("Address here...");
	        System.out.println("session _____ " + session);
	        
	        PaymentResponse res = new PaymentResponse();
	        res.setPayment_url(session.getUrl());
	        
	        return res;
	    
	}

}
