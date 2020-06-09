package com.example.smart_monitor.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.DriverActivity;
import com.example.smart_monitor.activity.ItemActivity;
import com.example.smart_monitor.activity.OrderGoodsActivity;
import com.example.smart_monitor.driver.driver_service.DriverGpsService;
import com.example.smart_monitor.fragment.ItemListFragment;
import com.example.smart_monitor.model.Order;
import com.example.smart_monitor.util.HttpRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import zuo.biao.library.base.BaseView;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;

public class OrderView extends BaseView<Order> implements OnClickListener, OnHttpResponseListener {
    private static final String TAG = "OrderView";

    public OrderView(Activity context, ViewGroup parent) {
        super(context, R.layout.order_view, parent);
    }

    private boolean onlyRead = false;
    public OrderView(Activity context, ViewGroup parent, boolean onlyRead) {
        super(context, R.layout.order_view, parent);
        this.onlyRead = onlyRead;
    }

    private Order order;

    private LinearLayout llOrderView;
    private LinearLayout llOrder;
    private LinearLayout llDriver;
    private LinearLayout llButton;
    private LinearLayout llRemark;
    private LinearLayout llEndTime;

    private TextView tvOrderState;
    private TextView tvOrderViewId;
    private TextView tvOrderStartTime;
    private TextView tvOrderEndTime;
    private TextView tvOrderTem;

    private TextView tvDriverViewName;
    private TextView tvDriverViewNumber;

    private Button btnOver;
    private Button btnCancel;
    private Button btnRemarkGood;
    private Button btnRemarkBad;

    @Override
    public View createView() {

        llOrderView = findView(R.id.llOrderView);
        llOrder = findView(R.id.llOrder);
        llDriver = findView(R.id.llDriver);
        llButton = findView(R.id.llButton);
        llRemark = findView(R.id.llRemark);
        llEndTime = findView(R.id.llEndTime);

        btnOver = findView(R.id.btnOver);
        btnCancel = findView(R.id.btnCancel);
        btnRemarkGood = findView(R.id.btnRemarkGood);
        btnRemarkBad = findView(R.id.btnRemarkBad);

        tvOrderState = findView(R.id.tvOrderState);
        tvOrderViewId = findView(R.id.tvOrderViewId);
        tvOrderStartTime = findView(R.id.tvOrderStartTime);
        tvOrderEndTime = findView(R.id.tvOrderEndTime);
        tvOrderTem = findView(R.id.tvOrderTem);

        tvDriverViewName = findView(R.id.tvDriverViewName);
        tvDriverViewNumber = findView(R.id.tvDriverViewNumber);

        if (!onlyRead){
            llOrder.setOnClickListener(this);
        }
        llDriver.setOnClickListener(this);

        btnOver.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnRemarkGood.setOnClickListener(this);
        btnRemarkBad.setOnClickListener(this);

        llOrderView.setVisibility(View.VISIBLE);

        return super.createView();
    }

    //TODO * 订单目录数据导入
    //此处存有Order订单数据
    @SuppressLint("ResourceAsColor")
    @Override
    public void bindView(Order data_){
        super.bindView(data_ != null ? data_ : new Order());

        itemView.setBackgroundResource(selected ? R.drawable.alpha3 : R.color.white_slight);

        order = data_;
        tvOrderViewId.setText(StringUtil.getTrimedString(data_.getOrder_id()));
        tvOrderStartTime.setText(StringUtil.getTrimedString(data_.getOrder_start()));
        tvOrderEndTime.setText(StringUtil.getTrimedString(data_.getOrder_end()));
        switch (data_.getCar_tem()){
            case 0:
                tvOrderTem.setText("温度适宜");
                tvOrderTem.setTextColor(R.color.green);
                break;
            case 1:
                tvOrderTem.setText("温度过高");
                tvOrderTem.setTextColor(R.color.red);
                break;
            case -1:
                tvOrderTem.setText("温度过低");
                tvOrderTem.setTextColor(R.color.blue_sky);
        }
        String state = null;

        switch (data_.getOrder_state()){
            case 0:
                state = data_.getAdmin_sure() == 1? "司机已经到达仓库": "等待司机到仓库";
                if (data_.getAdmin_sure() == 1){
                    llButton.setVisibility(View.VISIBLE);
                } else {
                    llButton.setVisibility(View.GONE);
                }
                llDriver.setVisibility(View.VISIBLE);
                llEndTime.setVisibility(View.GONE);
                break;
            case 1:
                state = data_.getAdmin_sure() == 1? "司机已经到达目的地": "运输中";
                if (data_.getAdmin_sure() == 1){
                    llButton.setVisibility(View.VISIBLE);
                } else {
                    llButton.setVisibility(View.GONE);
                }
                llDriver.setVisibility(View.VISIBLE);
                llEndTime.setVisibility(View.GONE);
                break;
            case 2:
                state = "已到目的地";
                llButton.setVisibility(View.GONE);
                llDriver.setVisibility(View.VISIBLE);
                llEndTime.setVisibility(View.VISIBLE);
                break;
            case 4:
                state = "司机拒绝";
                llButton.setVisibility(View.GONE);
                llDriver.setVisibility(View.VISIBLE);
                llEndTime.setVisibility(View.GONE);
                break;
            default:
                state = "等待司机确认";
                llButton.setVisibility(View.GONE);
                llDriver.setVisibility(View.VISIBLE);
                llEndTime.setVisibility(View.GONE);
                break;
        }
        llRemark.setVisibility(View.GONE);

        tvOrderState.setText(state);

        tvDriverViewName.setText(StringUtil.getTrimedString(data_.getDriver_name()));
        tvDriverViewNumber.setText(StringUtil.getTrimedString(data_.getTel()));
    }

    private static final int UPDATAORDER = 1;
    private static final int CANCELORDER = 2;
    @Override
    public void onClick(View v) {
        List<String> orderStateList = new ArrayList<>();
        Date date;
        SimpleDateFormat dateFormat;
        String current_time;
        switch (v.getId()) {
            case R.id.llDriver:
                showShortToast("弹出司机详情界面");
                toActivity(DriverActivity.createIntent(context, order.getDriver_id()));
                break;
            case R.id.llOrder:
                showShortToast("弹出货物详情界面");
                toActivity(OrderGoodsActivity.createIntent(context, order.getOrder_id(), order.getAdmin_id(), true));
                break;
            case R.id.btnOver:
                if (order.getOrder_state() == 1){
                    llButton.setVisibility(View.GONE);
                    llRemark.setVisibility(View.VISIBLE);
                } else {
                    orderStateList.clear();
                    orderStateList.add("" + (order.getOrder_state() + 1));
                    orderStateList.add("0");
                    orderStateList.add("" + order.getOrder_id());
                    android.util.Log.d(TAG, "OrderView onClick: " + orderStateList.get(0));
                    HttpRequest.updateInfo(orderStateList, "updataSimpleOrderForAdmin.do", UPDATAORDER, OrderView.this);
                }

                break;
            case R.id.btnCancel:
                orderStateList.clear();
                orderStateList.add("" + order.getOrder_state());
                orderStateList.add("0");
                orderStateList.add("" + order.getOrder_id());
                HttpRequest.updateInfo(orderStateList, "updataSimpleOrderForAdmin.do", CANCELORDER, OrderView.this);

                break;
            case R.id.btnRemarkGood:
                //当订单完成时
                orderStateList.clear();
                //修改订单状态
                orderStateList.add("" + (order.getOrder_state() + 1));
                orderStateList.add("0");
                orderStateList.add("" + order.getOrder_id());
                //修改司机状态
                orderStateList.add("0");
                orderStateList.add("" + order.getDriver_id());
                //获取系统时间
                date = new Date();
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                current_time = dateFormat.format(date);

                orderStateList.add(current_time);
                android.util.Log.d(TAG, "OrderView onClick: " + orderStateList.get(0));
                HttpRequest.updateInfo(orderStateList, "updataSimpleOrderForAdmin.do", UPDATAORDER, OrderView.this);
                break;
            case R.id.btnRemarkBad:
                orderStateList.clear();
                //修改订单状态
                orderStateList.add("" + (order.getOrder_state() + 1));
                orderStateList.add("0");
                orderStateList.add("" + order.getOrder_id());
                //修改司机状态
                orderStateList.add("1");
                orderStateList.add("" + order.getDriver_id());
                //获取系统时间
                date = new Date();
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                current_time = dateFormat.format(date);

                orderStateList.add(current_time);
                android.util.Log.d(TAG, "OrderView onClick: " + orderStateList.get(0));
                HttpRequest.updateInfo(orderStateList, "updataSimpleOrderForAdmin.do", UPDATAORDER, OrderView.this);
                break;
            default:
                Log.d("OrderView","测试点击");
                break;
        }
    }

    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {
        if (e != null){
            e.printStackTrace();
            showShortToast(R.string.get_failed);
            return;
        }

        if (resultJson == null || resultJson.isEmpty()){
            showShortToast(R.string.get_failed);
            return;
        }

        JSONObject result = com.alibaba.fastjson.JSON.parseObject(resultJson);
        Integer e_state;

        switch (requestCode) {
            case UPDATAORDER:
                e_state = result.getInteger("e");
                if (e_state == null || e_state == 0){
                    showShortToast(R.string.get_failed);
                    return;
                } else {
                    showShortToast("成功确认订单");
                    llOrderView.setVisibility(View.GONE);
                }
                break;
            case CANCELORDER:
                e_state = result.getInteger("e");
                if (e_state == null || e_state == 0){
                    showShortToast(R.string.get_failed);
                    return;
                } else {
                    showShortToast("成功取消订单");
                    llOrderView.setVisibility(View.GONE);
                }
                break;
        }
    }
}

