package com.property.billing.model;

import java.util.ArrayList;
import java.util.List;

public class Owner {
    private String id;
    private String name;
    private String phone;
    private String idCard;
    private boolean blacklisted;
    private List<String> restrictedServices = new ArrayList<>();
    private Long createTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public boolean isBlacklisted() { return blacklisted; }
    public void setBlacklisted(boolean blacklisted) { this.blacklisted = blacklisted; }
    public List<String> getRestrictedServices() { return restrictedServices; }
    public void setRestrictedServices(List<String> restrictedServices) { this.restrictedServices = restrictedServices; }
    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }
}
