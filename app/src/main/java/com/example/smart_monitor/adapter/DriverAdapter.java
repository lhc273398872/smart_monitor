package com.example.smart_monitor.adapter;

import android.app.Activity;
import android.view.ViewGroup;

import com.example.smart_monitor.model.Driver;
import com.example.smart_monitor.model.User;
import com.example.smart_monitor.view.DriverView;

import zuo.biao.library.base.BaseAdapter;

/** 使用方法：复制>粘贴>改名>改代码 */
/**adapter模板
 * <br> 适用于几乎所有列表、表格，包括：
 * <br> 1.RecyclerView及其子类
 * <br> 2.ListView,GridView等AbsListView的子类
 * @author Lemon
 * @use 修改.ItemView代码 >> new DemoAdapter(...),具体参考.DemoActivity(setList方法内)
 */
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
