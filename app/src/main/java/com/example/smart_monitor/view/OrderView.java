package com.example.smart_monitor.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.DriverActivity;
import com.example.smart_monitor.activity.ItemActivity;
import com.example.smart_monitor.activity.OrderGoodsActivity;
import com.example.smart_monitor.fragment.ItemListFragment;
import com.example.smart_monitor.model.Order;
import zuo.biao.library.base.BaseView;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;

public class OrderView extends BaseView<Order> implements OnClickListener {
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

    private LinearLayout llOrder;
    private LinearLayout llDriver;
    private LinearLayout llButton;

    private TextView tvOrderState;
    private TextView tvOrderViewId;
    private TextView tvOrderStartTime;
    private TextView tvOrderEndTime;

    private TextView tvDriverViewName;
    private TextView tvDriverViewNumber;
    @Override
    public View createView() {

        llOrder = findView(R.id.llOrder);
        llDriver = findView(R.id.llDriver);
        llButton = findView(R.id.llButton);

        tvOrderState = findView(R.id.tvOrderState);
        tvOrderViewId = findView(R.id.tvOrderViewId);
        tvOrderStartTime = findView(R.id.tvOrderStartTime);
        tvOrderEndTime = findView(R.id.tvOrderEndTime);

        tvDriverViewName = findView(R.id.tvDriverViewName);
        tvDriverViewNumber = findView(R.id.tvDriverViewNumber);

        if (!onlyRead){
            llOrder.setOnClickListener(this);
        }
        llDriver.setOnClickListener(this);

        return super.createView();
    }

    //TODO * 订单目录数据导入
    //此处存有Order订单数据
    @Override
    public void bindView(Order data_){
        super.bindView(data_ != null ? data_ : new Order());

        itemView.setBackgroundResource(selected ? R.drawable.alpha3 : R.color.white_slight);

        order = data_;
        tvOrderViewId.setText(StringUtil.getTrimedString(data_.getOrder_id()));
        tvOrderStartTime.setText(StringUtil.getTrimedString(data_.getOrder_start()));
        tvOrderEndTime.setText(StringUtil.getTrimedString(data_.getOrder_end()));
        String state = null;
        switch (data_.getOrder_state()){
            case 0:
                state = "等待司机到仓库";
                llButton.setVisibility(View.GONE);
                llDriver.setVisibility(View.VISIBLE);
                break;
            case 1:
                state = "运输中";
                llButton.setVisibility(View.GONE);
                llDriver.setVisibility(View.VISIBLE);
                break;
            case 2:
                state = "已到目的地";
                llButton.setVisibility(View.GONE);
                llDriver.setVisibility(View.VISIBLE);
                break;
            case 4:
                state = "司机拒绝";
                llButton.setVisibility(View.GONE);
                llDriver.setVisibility(View.VISIBLE);
                break;
            default:
                state = "等待司机确认";
                llButton.setVisibility(View.GONE);
                llDriver.setVisibility(View.VISIBLE);
                break;
        }
        tvOrderState.setText(state);

        tvDriverViewName.setText(StringUtil.getTrimedString(data_.getDriver_name()));
        tvDriverViewNumber.setText(StringUtil.getTrimedString(data_.getTel()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llDriver:
                showShortToast("弹出司机详情界面");
                toActivity(DriverActivity.createIntent(context, order.getDriver_id()));
                break;
            case R.id.llOrder:
                showShortToast("弹出货物详情界面");
                toActivity(OrderGoodsActivity.createIntent(context, order.getOrder_id(), order.getAdmin_id()));
                break;
            default:
                Log.d("OrderView","测试点击");
                break;
        }
    }

}

