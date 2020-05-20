package com.example.smart_monitor.model;

import java.io.Serializable;

import zuo.biao.library.base.BaseModel;

public class Car implements Serializable {

    private static final long serialVersionUID = -7620589932244521730L;

    private long car_id;
    private long driver_id;
    private String car_weight;  //汽车重量
    private String car_tem;     //汽车温度
    private String car_type;    //汽车型号
    public Car(){

    }

    public Car(Long car_id){
        this();
        this.car_id = car_id;
    }

    public long getCar_id() {
        return car_id;
    }

    public void setCar_id(long car_id) {
        this.car_id = car_id;
    }

    public long getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(long driver_id) {
        this.driver_id = driver_id;
    }

    public String getCar_weight() {
        return car_weight;
    }

    public void setCar_weight(String car_weight) {
        this.car_weight = car_weight;
    }

    public String getCar_tem() {
        return car_tem;
    }

    public void setCar_tem(String car_tem) {
        this.car_tem = car_tem;
    }

    public String getCar_type() {
        return car_type;
    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }

    protected boolean isCorrect() {
        return car_id > 0;
    }
}
