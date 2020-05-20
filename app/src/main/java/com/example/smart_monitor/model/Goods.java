package com.example.smart_monitor.model;

import android.location.Criteria;

import java.io.Serializable;

import zuo.biao.library.base.BaseModel;

/**用户类
 * @author Lemon
 */
public class Goods implements Serializable {

    //TODO 未知用途
    public static final int SEX_MAIL = 0;
    public static final int SEX_FEMALE = 1;
    public static final int SEX_UNKNOWN = 2;

    private static final long serialVersionUID = -6013599671767919986L;

    private long goods_id;
    private long house_id;
    private long order_id;
    private long admin_id;

//    private int isPropering; //温度是否适宜 0：适宜 1：非适宜

    //在父类中有get、set方法
    private String goods_name; //名称
//    private int goods_state; //货物运输情况 0：在仓库 1：运输中
    private int goods_tag; //货物类型 0：冷冻运输（-18~-22） 1：冷藏运输（0~7） 2：恒温运输（18~22）
    private String goods_weight; //TODO 货物重量暂时为字符串
    private int goods_number; //数量
    private String goods_remark; //备注

    private boolean goods_select;
    private int select_number;

    /**默认构造方法，JSON等解析时必须要有
     */
    public Goods() {
        //default
    }

    public Goods(long goods_id) {
        this();
        this.goods_id = goods_id;
    }
    public Goods(long goods_id, String goods_name) {
        this(goods_id);
        this.goods_name = goods_name;
    }

    public long getGoods_id() {
        return goods_id;
    }
    public void setGoods_id(long goods_id) {
        this.goods_id = goods_id;
    }
    public long getHouse_id() {
        return house_id;
    }
    public void setHouse_id(long house_id) {
        this.house_id = house_id;
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
    public String getGoods_weight() {return goods_weight;}
    public void setGoods_weight(String goods_weight) {this.goods_weight = goods_weight;}
//    public int getGoods_state() { return goods_state; }
//    public void setGoods_state(int goods_state) { this.goods_state = goods_state; }
//    public int getIsPropering() { return isPropering; }
//    public void setIsPropering(int isPropering) { this.isPropering = isPropering; }
    public String getGoods_remark() { return goods_remark; }
    public void setGoods_remark(String goods_remark) { this.goods_remark = goods_remark; }
    public String getGoods_name() {return goods_name;}
    public void setGoods_name(String goods_name) {this.goods_name = goods_name;}
    public int getGoods_number() {return goods_number;}
    public void setGoods_number(int number) {this.goods_number = number;}
    public int getGoods_tag() { return goods_tag; }
    public void setGoods_tag(int tag) { this.goods_tag = tag; }
    public boolean getGoods_select() {
        return goods_select;
    }
    public void setGoods_select(boolean goods_select) {
        this.goods_select = goods_select;
    }
    public int getSelect_number() {
        return select_number;
    }
    public void setSelect_number(int select_number) {
        this.select_number = select_number;
    }
//    public boolean getStarred() { return starred; }
//
//    public void setStarred(boolean starred) { this.starred = starred; }

    protected boolean isCorrect() {//根据自己的需求决定，也可以直接 return true
        return goods_id > 0;// && StringUtil.isNotEmpty(phone, true);
    }
}
