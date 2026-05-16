package com.property.billing.model;

public class House {
    private String id;
    private String buildingNo;
    private String unitNo;
    private String roomNo;
    private double area;
    private String ownerId;
    private boolean hasParkingSpace;
    private Long createTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBuildingNo() { return buildingNo; }
    public void setBuildingNo(String buildingNo) { this.buildingNo = buildingNo; }
    public String getUnitNo() { return unitNo; }
    public void setUnitNo(String unitNo) { this.unitNo = unitNo; }
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public boolean isHasParkingSpace() { return hasParkingSpace; }
    public void setHasParkingSpace(boolean hasParkingSpace) { this.hasParkingSpace = hasParkingSpace; }
    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }
}
