package com.property.billing.model;

import java.math.BigDecimal;
import java.util.List;

public class Payment {
    private String id;
    private String ownerId;
    private BigDecimal totalAmount;
    private List<String> billIds;
    private String remark;
    private Long payTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public List<String> getBillIds() { return billIds; }
    public void setBillIds(List<String> billIds) { this.billIds = billIds; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Long getPayTime() { return payTime; }
    public void setPayTime(Long payTime) { this.payTime = payTime; }
}
