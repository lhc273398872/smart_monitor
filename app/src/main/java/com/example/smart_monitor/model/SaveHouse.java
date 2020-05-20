package com.example.smart_monitor.model;

import java.io.Serializable;

//TODO * 添加经纬度存储
public class SaveHouse implements Serializable {
    private static final long serialVersionUID = -450040103783123661L;

    private Long house_id;
    private Long admin_id;
    private String house_name;
    private int house_tem;
    private String house_location;
    private String house_size;
    private String house_latitude;
    private String house_longitude;

    public SaveHouse(){

    }

    public SaveHouse(Long house_id){
        this();
        this.house_id = house_id;
    }

    public Long getHouse_id() {
        return house_id;
    }

    public void setHouse_id(Long house_id) {
        this.house_id = house_id;
    }

    public Long getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(Long admin_id) {
        this.admin_id = admin_id;
    }

    public String getHouse_name() {
        return house_name;
    }

    public void setHouse_name(String house_name) {
        this.house_name = house_name;
    }

    public int getHouse_tem() {
        return house_tem;
    }

    public void setHouse_tem(int house_tem) {
        this.house_tem = house_tem;
    }

    public String getHouse_location() {
        return house_location;
    }

    public void setHouse_location(String house_location) {
        this.house_location = house_location;
    }

    public String getHouse_size() {
        return house_size;
    }

    public void setHouse_size(String house_size) {
        this.house_size = house_size;
    }

    public String getHouse_latitude() {
        return house_latitude;
    }

    public void setHouse_latitude(String house_latitude) {
        this.house_latitude = house_latitude;
    }

    public String getHouse_longitude() {
        return house_longitude;
    }

    public void setHouse_longitude(String house_longitude) {
        this.house_longitude = house_longitude;
    }
}
