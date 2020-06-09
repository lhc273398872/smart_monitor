package com.example.smart_monitor.model;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

public class OrderGpsToDriver implements Serializable {

    private static final long serialVersionUID = 1616094750771898520L;
    //重写Driver

    private long order_id;
    private String order_start; //订单开始日期
    private String order_end;   //订单结束日期
    private int order_state;    ////订单情况
    private String order_location;
    private String order_latitude;
    private String order_longitude;

    private long driver_id;

    private long admin_id;
    private String admin_name;

    private String house_name;
    private int house_tem;
    private String house_location;
    private String house_size;
    private String house_latitude;
    private String house_longitude;


    /**
     * 订单情况
     * 0：等待司机； 1：运输中； 2已到目的地; 3:等待司机同意; 4:司机拒绝
     * 仅2、3、4可删除
     *
     */

    public OrderGpsToDriver() {}

    public OrderGpsToDriver(long order_id) {
        this();
        this.order_id = order_id;
    }

    protected boolean isCorrect() {
        if (order_id < 0){
            return false;
        }
        return true;
    }

    public String getOrder_start() {
        return order_start;
    }

    public void setOrder_start(String order_start) {
        this.order_start = order_start;
    }

    public String getOrder_end() {
        return order_end;
    }

    public void setOrder_end(String order_end) {
        this.order_end = order_end;
    }

    public int getOrder_state() {
        return order_state;
    }

    public void setOrder_state(int order_state) {
        this.order_state = order_state;
    }

    public long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(long order_id) {
        this.order_id = order_id;
    }

    public String getOrder_location() {
        return order_location;
    }

    public void setOrder_location(String order_location) {
        this.order_location = order_location;
    }

    public String getOrder_latitude() {
        return order_latitude;
    }

    public void setOrder_latitude(String order_latitude) {
        this.order_latitude = order_latitude;
    }

    public String getOrder_longitude() {
        return order_longitude;
    }

    public void setOrder_longitude(String order_longitude) {
        this.order_longitude = order_longitude;
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

    public long getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(long driver_id) {
        this.driver_id = driver_id;
    }

    public long getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(long admin_id) {
        this.admin_id = admin_id;
    }

    public String getAdmin_name() {
        return admin_name;
    }

    public void setAdmin_name(String admin_name) {
        this.admin_name = admin_name;
    }

}