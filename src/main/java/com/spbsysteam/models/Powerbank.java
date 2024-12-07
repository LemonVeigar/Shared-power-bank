package com.spbsysteam.models;

import java.math.BigDecimal;

/**
 * Powerbank类表示充电宝的实体。
 */
public class Powerbank {
    private int id;
    private String location;
    private double latitude;
    private double longitude;
    private int batteryLevel;
    private String status;
    private BigDecimal pricePerHour;

    /**
     * 构造方法，初始化Powerbank对象。
     *
     * @param id           充电宝ID
     * @param location     位置
     * @param latitude     纬度
     * @param longitude    经度
     * @param batteryLevel 剩余电量
     * @param status       状态（如 "available", "unavailable", "maintenance"）
     * @param pricePerHour 租赁价格（元/小时）
     */
    public Powerbank(int id, String location, double latitude, double longitude, int batteryLevel, String status, BigDecimal pricePerHour) {
        this.id = id;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.batteryLevel = batteryLevel;
        this.status = status;
        this.pricePerHour = pricePerHour;
    }

    // Getters

    public int getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getPricePerHour() {
        return pricePerHour;
    }

    // Setters（如果需要修改充电宝信息，可以添加相应的setter方法）

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPricePerHour(BigDecimal pricePerHour) {
        this.pricePerHour = pricePerHour;
    }
}
