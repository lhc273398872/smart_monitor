package com.example.smart_monitor.model;

import java.io.Serializable;

public class CarTems implements Serializable{

    private static final long serialVersionUID = -2020699518803587007L;

    private long tem_id;
    private long driver_id;
    private int tem;

    public CarTems(){

    }

    public CarTems(long tem_id){
        this();
        this.setTem_id(tem_id);
    }

    public long getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(long driver_id) {
        this.driver_id = driver_id;
    }

    public long getTem_id() {
        return tem_id;
    }

    public void setTem_id(long tem_id) {
        this.tem_id = tem_id;
    }

    public int getTem() {
        return tem;
    }

    public void setTem(int tem) {
        this.tem = tem;
    }

}
