package com.example.stripeSubscription.Services;


import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.model.Coupon;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.keys.secret}")
    private String API_SECRET_KEY;

    public StripeService() {
    }

    public String createCustomer(String email, String token) {
        String id = null;
        try {
            Stripe.apiKey = API_SECRET_KEY;
            Map<String, Object> customerParams = new HashMap<>();
            // add customer unique id here to track them in your web application
            customerParams.put("description", "Customer for " + email);
            customerParams.put("email", email);

            customerParams.put("source", token); // ^ obtained with Stripe.js
            //create a new customer
            Customer customer = Customer.create(customerParams);
            id = customer.getId();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return id;
    }

    public String createSubscription(String customerId , String coupon) {
        String id = null;
        try {
            Stripe.apiKey = API_SECRET_KEY;

            List<Object> items = new ArrayList<>();
            Map<String, Object> item1 = new HashMap<>();
            item1.put(
                    "price",
                    "price_1LXJ04SFupRFZB1oeA49zE22"
            );
            items.add(item1);
            Map<String, Object> params = new HashMap<>();
            params.put("customer",customerId);
            params.put("items", items);

            //add coupon if available
            if (!coupon.isEmpty()) {
                params.put("coupon", coupon);
            }

            Subscription sub = Subscription.create(params);
            id = sub.getId();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return id;
    }

    public boolean cancelSubscription(String subscriptionId) {
        boolean status;
        try {
            Stripe.apiKey =API_SECRET_KEY;
            Subscription sub = Subscription.retrieve(subscriptionId);
            sub.cancel(null);
            status = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            status = false;
        }
        return status;
    }

    public Coupon retrieveCoupon(String code) {
        try {
            Stripe.apiKey = API_SECRET_KEY;
            return Coupon.retrieve(code);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String createCharge(String email, String token,int amount) {
        String id = null;
        try {
            Stripe.apiKey = API_SECRET_KEY;
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount",amount);
            chargeParams.put("currency", "usd");
            chargeParams.put("description", "Charge for " + email);
            chargeParams.put("source", token); // ^ obtained with Stripe.js

            //create a charge
            Charge charge = Charge.create(chargeParams);
            id = charge.getId();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return id;
    }

}
