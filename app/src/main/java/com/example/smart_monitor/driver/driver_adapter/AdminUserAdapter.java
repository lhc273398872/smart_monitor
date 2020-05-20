package com.example.smart_monitor.driver.driver_adapter;

import android.app.Activity;
import android.view.ViewGroup;

import com.example.smart_monitor.driver.driver_view.AdminUserView;
import com.example.smart_monitor.model.AdminUser;
import com.example.smart_monitor.model.Driver;
import com.example.smart_monitor.view.DriverView;

import zuo.biao.library.base.BaseAdapter;

public class AdminUserAdapter extends BaseAdapter<AdminUser, AdminUserView> {

    public AdminUserAdapter(Activity context) {
        super(context);
    }

    @Override
    public AdminUserView createView(int position, ViewGroup parent) {
        return new AdminUserView(context, parent);

    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getAdmin_id();
    }


}
