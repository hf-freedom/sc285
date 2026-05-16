package com.property.billing.model;

public enum FeeType {
    PROPERTY_FEE("物业费", 1),
    WATER_FEE("水费", 2),
    ELECTRICITY_FEE("电费", 3),
    PARKING_FEE("停车费", 4);

    private String name;
    private int priority;

    FeeType(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }
}
