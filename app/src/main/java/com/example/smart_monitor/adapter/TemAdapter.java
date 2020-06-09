package com.example.smart_monitor.adapter;

import android.app.Activity;
import android.view.ViewGroup;

import com.example.smart_monitor.model.CarTems;
import com.example.smart_monitor.model.Driver;
import com.example.smart_monitor.view.DriverView;
import com.example.smart_monitor.view.TemView;

import zuo.biao.library.base.BaseAdapter;

public class TemAdapter extends BaseAdapter<CarTems, TemView> {

    public TemAdapter(Activity context) {
        super(context);
    }

    @Override
    public TemView createView(int position, ViewGroup parent) {
        return new TemView(context, parent);

    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getTem_id();
    }


}
