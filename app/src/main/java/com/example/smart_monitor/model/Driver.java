package com.example.smart_monitor.model;

import java.io.Serializable;

import zuo.biao.library.base.BaseModel;

public class Driver implements Serializable{
    private static final long serialVersionUID = -5334010956973222849L;

    private long driver_id;
    private String driver_name; //名字
    private String tel; //电话号码
    private int isRunning; //0：等待中，1：运输中

    private String driver_location;
    private String driver_longitude;
    private String driver_latitude;
    private long over_order;
    private long wrong_ops;

//    private int state; //用户类型 0：公司管理员 1：司机
//    private int sex; //性别
//    private String head; //头像
//    private String tag; //标签
    //Driver包含gps类和car类
    public Driver(){

    }

    public Driver(long driver_id){
        this();
        this.driver_id = driver_id;
    }
    public Driver(long driver_id, String driver_name) {
        this(driver_id);
        this.driver_name = driver_name;
    }

    public long getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(long driver_id) {
        this.driver_id = driver_id;
    }
    public int getIsRunning() {
        return isRunning;
    }
    public void setIsRunning(int isRunning) {
        this.isRunning = isRunning;
    }
    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }

    protected boolean isCorrect() {
        return driver_id > 0;
    }
    public String getDriver_name() {
        return driver_name;
    }
    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
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

    public long getOver_order() {
        return over_order;
    }

    public void setOver_order(long over_order) {
        this.over_order = over_order;
    }

    public long getWrong_ops() {
        return wrong_ops;
    }

    public void setWrong_ops(long wrong_ops) {
        this.wrong_ops = wrong_ops;
    }

//    public int getSex() {
//        return sex;
//    }
//    public void setSex(int sex) {
//        this.sex = sex;
//    }
//    public int getState() {
//        return state;
//    }
//    public void setState(int state) {
//        this.state = state;
//    }
//    public String getHead() {
//        return head;
//    }
//    public void setHead(String head) {
//        this.head = head;
//    }

//    public String getTag() {
//        return tag;
//    }
//    public void setTag(String tag) {
//        this.tag = tag;
//    }
}
