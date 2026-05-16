package com.property.billing.model;

public enum ApprovalStatus {
    PENDING("待审批"),
    APPROVED("已通过"),
    REJECTED("已拒绝");

    private String name;

    ApprovalStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
