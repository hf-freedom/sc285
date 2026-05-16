package com.property.billing.model;

import java.math.BigDecimal;

public class DiscountApplication {
    private String id;
    private String ownerId;
    private String billId;
    private BigDecimal discountAmount;
    private String reason;
    private ApprovalStatus status;
    private String approver;
    private String approvalRemark;
    private Long createTime;
    private Long approvalTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { this.status = status; }
    public String getApprover() { return approver; }
    public void setApprover(String approver) { this.approver = approver; }
    public String getApprovalRemark() { return approvalRemark; }
    public void setApprovalRemark(String approvalRemark) { this.approvalRemark = approvalRemark; }
    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }
    public Long getApprovalTime() { return approvalTime; }
    public void setApprovalTime(Long approvalTime) { this.approvalTime = approvalTime; }
}
