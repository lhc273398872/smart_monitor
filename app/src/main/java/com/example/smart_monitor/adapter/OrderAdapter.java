package com.example.smart_monitor.adapter;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.smart_monitor.model.Order;
import com.example.smart_monitor.view.OrderView;

import zuo.biao.library.base.BaseAdapter;

public class OrderAdapter extends BaseAdapter<Order, OrderView> {

        public OrderAdapter(Activity context) {
            super(context);
        }

        @Override
        public OrderView createView(int position, ViewGroup parent) {
            return new OrderView(context, parent);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getOrder_id();
        }
}
