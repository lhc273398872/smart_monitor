package com.example.smart_monitor.model;

import java.io.Serializable;

public class AdminUser implements Serializable {

    private static final long serialVersionUID = 6403008751644125592L;

    private long admin_id;
    private String admin_name;
    private String tel;

    public AdminUser(){

    }

    public AdminUser(long admin_id){
        this();
        this.admin_id = admin_id;
    }

    public AdminUser(long admin_id, String admin_name){
        this(admin_id);
        this.admin_name = admin_name;
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

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

//    public String getAdmin_pswd() {
//        return admin_pswd;
//    }
//
//    public void setAdmin_pswd(String admin_pswd) {
//        this.admin_pswd = admin_pswd;
//    }

    protected boolean isCorrect() {
        return admin_id > 0;
    }
}
