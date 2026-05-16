package com.property.billing.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStorage {
    public static final Map<String, Owner> OWNERS = new ConcurrentHashMap<>();
    public static final Map<String, House> HOUSES = new ConcurrentHashMap<>();
    public static final Map<String, Bill> BILLS = new ConcurrentHashMap<>();
    public static final Map<String, Payment> PAYMENTS = new ConcurrentHashMap<>();
    public static final Map<String, DiscountApplication> DISCOUNT_APPLICATIONS = new ConcurrentHashMap<>();
    
    public static final Map<String, Double> FEE_RATES = new ConcurrentHashMap<String, Double>() {{
        put("PROPERTY_FEE", 2.5);
        put("WATER_FEE", 3.5);
        put("ELECTRICITY_FEE", 0.6);
        put("PARKING_FEE", 150.0);
    }};
}
