package com.property.billing.model;

public enum BillStatus {
    UNPAID("未缴费"),
    PARTIAL_PAID("部分缴费"),
    PAID("已缴费"),
    OVERDUE("逾期");

    private String name;

    BillStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
