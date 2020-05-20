package com.example.smart_monitor.model;

import java.io.Serializable;

import zuo.biao.library.base.BaseModel;

public class CarGps extends BaseModel {

    //gps坐标

    private long driver_id;
    private String driver_longitude;
    private String driver_latitud;
    public CarGps(){

    }

    public CarGps(Long id){
        this();
        this.id = id;
    }

    @Override
    protected boolean isCorrect() {
        return false;
    }

    public long getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(long driver_id) {
        this.driver_id = driver_id;
    }

    public String getDriver_longitude() {
        return driver_longitude;
    }

    public void setDriver_longitude(String driver_longitude) {
        this.driver_longitude = driver_longitude;
    }

    public String getDriver_latitud() {
        return driver_latitud;
    }

    public void setDriver_latitud(String driver_latitud) {
        this.driver_latitud = driver_latitud;
    }
}
