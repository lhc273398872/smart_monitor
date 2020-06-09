package com.example.smart_monitor.driver.driver_view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.DriverActivity;
import com.example.smart_monitor.activity.OrderGoodsActivity;
import com.example.smart_monitor.driver.driver_activity.AdminUserActivity;
import com.example.smart_monitor.driver.driver_fragment.DriverOrderListFragment;
import com.example.smart_monitor.model.Order;
import com.example.smart_monitor.util.HttpRequest;

import java.util.ArrayList;
import java.util.List;

import zuo.biao.library.base.BaseView;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;

public class DriverOrderView extends BaseView<Order> implements OnClickListener, OnHttpResponseListener{
    private static final String TAG = "OrderView";

    public DriverOrderView(Activity context, ViewGroup parent) {
        super(context, R.layout.order_view, parent);
    }

    private boolean onlyRead = false;
    public DriverOrderView(Activity context, ViewGroup parent, boolean onlyRead) {
        super(context, R.layout.order_view, parent);
        this.onlyRead = onlyRead;
    }

    private Order order;

    private LinearLayout llOrderView;
    private LinearLayout llOrder;
    private LinearLayout llDriver;
    private LinearLayout llButton;
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
    @Override
    public View createView() {

        llOrderView = findView(R.id.llOrderView);
        llOrder = findView(R.id.llOrder);
        llDriver = findView(R.id.llDriver);
        llButton = findView(R.id.llButton);
        llEndTime = findView(R.id.llEndTime);

        btnOver = findView(R.id.btnOver);
        btnCancel = findView(R.id.btnCancel);

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

        llEndTime.setVisibility(View.VISIBLE);

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
                state = "等待司机到仓库";
                llButton.setVisibility(View.GONE);
                llDriver.setVisibility(View.VISIBLE);
                llEndTime.setVisibility(View.GONE);
                break;
            case 1:
                state = "运输中";
                llButton.setVisibility(View.GONE);
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
                state = "司机已拒绝";
                llButton.setVisibility(View.GONE);
                llDriver.setVisibility(View.VISIBLE);
                llEndTime.setVisibility(View.GONE);
                break;
            default:
                state = "等待司机确认";
                llDriver.setVisibility(View.GONE);
                llButton.setVisibility(View.VISIBLE);
                llEndTime.setVisibility(View.GONE);
                break;
        }

        tvOrderState.setText(state);

        tvDriverViewName.setText(StringUtil.getTrimedString(data_.getAdmin_name()));
        tvDriverViewNumber.setText(StringUtil.getTrimedString(data_.getTel()));
    }

    private final int CANCELORDER = 1;
    private final int OVERORDER = 2;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llDriver:
                showShortToast("弹出管理员详情界面");
                toActivity(AdminUserActivity.createIntent(context, order.getAdmin_id()));
                break;
            case R.id.llOrder:
                showShortToast("弹出货物详情界面");
                toActivity(OrderGoodsActivity.createIntent(context, order.getOrder_id(), order.getAdmin_id(),false));
                break;
            case R.id.btnOver:
                List<String> orderStateList = new ArrayList<>();
                orderStateList.add("0");
                orderStateList.add("" + order.getOrder_id());
                orderStateList.add("" + order.getDriver_id());
                HttpRequest.updateInfo(orderStateList, "updataSimpleOrderState.do", OVERORDER, DriverOrderView.this);
                break;
            case R.id.btnCancel:
                HttpRequest.getInfo(order.getOrder_id(), "cancelSimpleOrder.do", CANCELORDER, DriverOrderView.this);
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
        if (resultJson == null){
            showShortToast(R.string.get_failed);
            return;
        }

        JSONObject result = JSON.parseObject(resultJson);
        Integer e_state = 0;

        switch (requestCode){
            case CANCELORDER:
                e_state = result.getInteger("e");

                if (e_state == null || e_state == 0){
                    showShortToast(R.string.get_failed);
                } else {
                    order.setOrder_state(4);
                    bindView(order);
                    showShortToast("订单已拒绝");
                    llOrderView.setVisibility(View.GONE);
                }
                break;
            case OVERORDER:
                e_state = result.getInteger("e");

                if (e_state == null || e_state == 0){
                    showShortToast(R.string.get_failed);
                } else {
                    order.setOrder_state(1);
                    bindView(order);
                    showShortToast("订单已接收");
                    llOrderView.setVisibility(View.GONE);
                }
                break;
        }
    }
}

