package com.example.smart_monitor.driver.driver_adapter;

import android.app.Activity;
import android.view.ViewGroup;

import com.example.smart_monitor.driver.driver_view.DriverOrderView;
import com.example.smart_monitor.model.Order;
import com.example.smart_monitor.view.OrderView;

import zuo.biao.library.base.BaseAdapter;

public class DriverOrderAdapter extends BaseAdapter<Order, DriverOrderView> {

        public DriverOrderAdapter(Activity context) {
            super(context);
        }

        @Override
        public DriverOrderView createView(int position, ViewGroup parent) {
            return new DriverOrderView(context, parent);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getOrder_id();
        }
}
