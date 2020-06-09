package com.example.smart_monitor.model;

import java.io.Serializable;
import java.util.List;

import zuo.biao.library.base.BaseModel;

public class Order implements Serializable {

    private static final long serialVersionUID = -5159744237866992049L;

    //重写Driver

    private long order_id;
    private long house_id;
    private String order_start; //订单开始日期
    private String order_end;   //订单结束日期
    private int order_state;    ////订单情况
    private String order_location;
    private String order_latitude;
    private String order_longitude;

    private long driver_id;        //存储运输司机
    private String driver_location;
    private String driver_latitude;
    private String driver_longitude;
    private String driver_name;
    private String tel;

    private int car_tem;

    private long admin_id;		//存储管理员信息
    private String admin_name;
    private int admin_sure;

    /**
     * 订单情况
     * 0：等待司机； 1：运输中； 2已到目的地; 3:等待司机同意; 4:司机拒绝
     * 仅2、3、4可删除
     *
     */

    public Order() {}

    public Order(long order_id) {
        this();
        this.order_id = order_id;
    }

    public Order(long order_id, long driver_id){
        this(order_id);
        this.driver_id = driver_id;
    }

    protected boolean isCorrect() {
        if (order_id < 0){
            return false;
        }
        return true;
    }

    public long getHouse_id() {
        return house_id;
    }

    public void setHouse_id(long house_id) {
        this.house_id = house_id;
    }

    public long getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(long driver_id) {
        this.driver_id = driver_id;
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

    public long getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(long admin_id) {
        this.admin_id = admin_id;
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

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getDriver_longitude() {
        return driver_longitude;
    }

    public void setDriver_longitude(String driver_longitude) {
        this.driver_longitude = driver_longitude;
    }

    public String getDriver_latitude() {
        return driver_latitude;
    }

    public void setDriver_latitude(String driver_latitude) {
        this.driver_latitude = driver_latitude;
    }

    public String getDriver_location() {
        return driver_location;
    }

    public void setDriver_location(String driver_location) {
        this.driver_location = driver_location;
    }

    public String getAdmin_name() {
        return admin_name;
    }

    public void setAdmin_name(String admin_name) {
        this.admin_name = admin_name;
    }

    public int getCar_tem() {
        return car_tem;
    }

    public void setCar_tem(int car_tem) {
        this.car_tem = car_tem;
    }

    public int getAdmin_sure() {
        return admin_sure;
    }

    public void setAdmin_sure(int admin_sure) {
        this.admin_sure = admin_sure;
    }
}
