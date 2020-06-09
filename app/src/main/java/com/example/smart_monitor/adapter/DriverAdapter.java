package com.example.smart_monitor.adapter;

import android.app.Activity;
import android.view.ViewGroup;

import com.example.smart_monitor.model.Driver;
import com.example.smart_monitor.model.User;
import com.example.smart_monitor.view.DriverView;

import zuo.biao.library.base.BaseAdapter;

public class DriverAdapter extends BaseAdapter<Driver, DriverView> {

    public DriverAdapter(Activity context) {
        super(context);
    }

    @Override
    public DriverView createView(int position, ViewGroup parent) {
        return new DriverView(context, parent);

    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getDriver_id();
    }


}
