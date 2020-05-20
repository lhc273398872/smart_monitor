package com.example.smart_monitor.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.smart_monitor.R;
import com.example.smart_monitor.model.Goods;
import com.example.smart_monitor.view.ItemView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zuo.biao.library.base.BaseAdapter;
import zuo.biao.library.model.Entry;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class ItemAdapter extends BaseAdapter<Goods, ItemView> {

    public boolean checkBoxFlag;

    public ItemAdapter(Activity context, boolean checkBoxFlag) {
        super(context);
        this.checkBoxFlag = checkBoxFlag;
    }

    @Override
    public ItemView createView(int position, ViewGroup parent) {
        ItemView itemView = new ItemView(context, parent, checkBoxFlag);
        return itemView;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getGoods_id();
    }
}
